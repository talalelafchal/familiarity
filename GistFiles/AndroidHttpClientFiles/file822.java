import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.provider.Settings.Secure;
import java.io.IOException;

HttpClient httpclient = new DefaultHttpClient();
HttpPost httppost = new HttpPost("http://a.path.com/r");
HttpGet httpGet = new HttpGet("http://a.path.com/r/nmy2mmjlmjutzdy2zs00nzuwltgxmjktnjmzzdc4ymezzwiz");

try {
    String android_id = Secure.getString(context.getContentResolver(),
    Secure.ANDROID_ID);

    Ln.d("notificationTitle: %s", notificationTitle);
    Ln.d("message: %s", message);
    Ln.d("conversationId: %s", message.getConversation().getNodeId());

    JSONObject jsonObject = new JSONObject();
    jsonObject.accumulate("device_id", android_id);
    jsonObject.accumulate("platform", "android");
    jsonObject.accumulate("node_id", message.getConversation().getNodeId());
    jsonObject.accumulate("message_id", message.toString());

    // 4. convert JSONObject to JSON to String
    String jsonData = jsonObject.toString();

    Ln.d("json data: %s", jsonData);
    httppost.setEntity(new StringEntity(jsonData));
    httppost.setHeader("Accept", "application/json");
    httppost.setHeader("Content-Type", "application/json");

    // Execute HTTP Post Request
    HttpResponse response = httpclient.execute(httppost);
    response = httpclient.execute(httpGet);
    
    } catch (ClientProtocolException e) {
        // Known exception
        Ln.d("ClientProtocolException");
    } catch (IOException e) {
        Ln.d("IOException");
    }
}