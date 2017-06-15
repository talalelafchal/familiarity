package com.mashinz.admin.workers;

import com.google.gson.*;
import com.mashinz.admin.services.NotificationMessageService;
import com.mashinz.admin.services.NotificationService;
import com.mashinz.core.enums.NotificationAction;
import com.mashinz.core.enums.NotificationMessageStatus;
import com.mashinz.core.enums.NotificationStatus;
import com.mashinz.core.models.Notification;
import com.mashinz.core.models.NotificationMessage;
import com.mashinz.core.utils.EnumUtils;
import com.mashinz.core.utils.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by mohsenk on 1/1/2016 AD.
 */
@Component
public class NotificationSender implements Runnable {


    private static final Logger logger = LoggerFactory
            .getLogger(NotificationSender.class);

    @Value("${one_signal.app_id}")
    String oneSignalAppId;

    @Value("${one_signal.auth_token}")
    String oneSignalAuthToken;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationMessageService notificationMessageService;

    JsonParser jsonParser = new JsonParser();

    Thread thread = null;


    RequestConfig requestConfig;

    public void init() {
        int timeout = 5;
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).setRedirectsEnabled(false).build();

    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public JsonObject makeJson(Notification notification, List<NotificationMessage> messages) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app_id", oneSignalAppId);

        JsonObject contentJson = new JsonObject();
        contentJson.addProperty("en", "");

        JsonObject titleJson = new JsonObject();
        titleJson.addProperty("en", notification.getTitle());


        JsonObject json = new JsonObject();
        json.addProperty("action", EnumUtils.valueOf(NotificationAction.class,notification.getAction()).getDesc());
        if (com.mysql.jdbc.StringUtils.isNullOrEmpty(notification.getData())) {
            json.add("data", JsonNull.INSTANCE);
        } else {
            json.add("data", jsonParser.parse(notification.getData()));
        }
        json.addProperty("content", notification.getContent());
        jsonObject.add("data", json);

        jsonObject.add("contents", contentJson);
        jsonObject.add("headings", titleJson);

        JsonArray playerIdsArray = new JsonArray();
        for (NotificationMessage message : messages) {
            if (StringUtils.isNullOrEmpty(message.getDevice().getNotificationToken())) {
                logger.warn("Notification must be send but token is null, factory id : {}", message.getDevice().getFactoryId());
            } else {
                playerIdsArray.add(new JsonPrimitive(message.getDevice().getNotificationToken()));
            }
        }
        if (playerIdsArray.size() == 0) {
            return null;
        }
        jsonObject.add("include_player_ids", playerIdsArray);

        jsonObject.addProperty("android_background_data", true);
        return jsonObject;
    }

    @Autowired
    Environment env;

    @Override
    public void run() {
        while (true) {
            try {
                boolean devMode = this.env.acceptsProfiles("dev");
                if (devMode) return;
                List<Notification> notifications = notificationService.findByTurn(LocalDateTime.now(), NotificationStatus.Queue.getValue(), 10);
                if (notifications.isEmpty()) {
                    continue;
                }
                logger.info("Proccessing notifications , count  :{}", notifications.size());
                for (Notification notification : notifications) {
                    try {
                        List<NotificationMessage> messages = notificationMessageService.findByNotificationAdnStatus(notification.getId(), NotificationMessageStatus.Queue.getValue());
                        JsonObject jsonObject = makeJson(notification, messages);
                        if (jsonObject == null) {
                            notification.setStatus(NotificationStatus.NoReceiverFound.getValue());
                            continue;
                        }
                        HttpClient client = HttpClientBuilder.create().setRetryHandler(new StandardHttpRequestRetryHandler()).setServiceUnavailableRetryStrategy(new DefaultServiceUnavailableRetryStrategy()).setDefaultRequestConfig(requestConfig).build();

                        HttpPost post = new HttpPost("https://onesignal.com/api/v1/notifications");
                        post.setHeader("Authorization", "Basic " + oneSignalAuthToken);
                        StringEntity requestEntity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
                        post.setEntity(requestEntity);
                        logger.info("going to send notification request");
                        HttpResponse response = client.execute(post);
                        notification.setStatus(NotificationStatus.Sent.getValue());
                        if (response.getStatusLine().getStatusCode() == 204) {
                            notification.setStatus(NotificationStatus.Unsubscribed.getValue());
                            continue;
                        }
                        String json = EntityUtils.toString(response.getEntity());
                        logger.info("reading notification content");
                        logger.info(json);
                        JsonObject resultJson = new JsonParser().parse(json).getAsJsonObject();

                        int statusCode = response.getStatusLine().getStatusCode();
                        int recipients = resultJson.get("recipients").getAsInt();
                        if (recipients == 0) {
                            notification.setStatus(NotificationStatus.Unsubscribed.getValue());
                            continue;
                        }
                        if (statusCode != 200) {
                            logger.error("Notification send response is not success , status code : {} , message : {}", statusCode, resultJson.toString());
                        } else {
                            logger.info("Notification send response is success , data :{}", resultJson.toString());
                            notification.setRemoteId(resultJson.get("id").getAsString());
                        }
                    } catch (Exception ex) {
                        logger.error("", ex);
                        notification.setStatus(NotificationStatus.Failed.getValue());
                    } finally {
                        notificationService.save(notification);
                    }
                }
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    logger.error("", e);
                }
            }
        }
    }
}
