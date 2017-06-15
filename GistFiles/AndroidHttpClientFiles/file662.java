import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class GCMSendSample {

    public static final String SEND_URL = "https://android.googleapis.com/gcm/send";

    public static final String AUTH_KEY = "key=" + "A*************************************o";

    public static void main(String[] args) throws Exception {
        String regId = "A*************************************daA";
        String message = "ウンコだ捨てろ！";
        sendMessage(regId, message);
    }

    public static void sendMessage(String regId, String message) throws Exception {
        HttpClient client = new DefaultHttpClient();

        HttpPost httpost = new HttpPost(SEND_URL);
        httpost.setHeader("Authorization", AUTH_KEY);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("registration_id", regId));
        nvps.add(new BasicNameValuePair("collapse_key", "update"));
        nvps.add(new BasicNameValuePair("data.message", message));
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = client.execute(httpost);
        HttpEntity entity = response.getEntity();
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(EntityUtils.toString(entity));
    }
}
