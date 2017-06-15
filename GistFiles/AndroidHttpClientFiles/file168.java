package util;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import play.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by lacroiix on 10/11/15.
 */
public class GcmSender {

    public static void send(List<String> to, String data) {

        final String API_KEY = "AIzaSyA4RfBF9h4O7-oV9cmp5qPiveH99p2BV4I";

        try {
            String REQUEST_URL = "https://android.googleapis.com/gcm/send";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(REQUEST_URL);

            httpPost.setHeader("Authorization", "key=" + API_KEY);
            httpPost.setHeader("Content-Type", "application/json");

            String message = "Teste";

            StringEntity params = new StringEntity(
                    "{ \"data\": {\"message\": \"" + message + "\"}"
                            + "\"registration_ids\": [\"" + to.get(0) + "\"]}");

            httpPost.setEntity(params);

            Logger.info("create friends event response :"
                    + httpclient.execute(httpPost).getStatusLine()
                    .getStatusCode());

            System.out.println("---");

        } catch (Exception e) {
            Logger.error(e.toString());
        }
    }
}
