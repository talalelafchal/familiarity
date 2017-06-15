import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by surinrobot on 1/29/15 AD.
 */
public class MyHttpPost {
    public static final int HTTP_POST_OK = 1;
    public static final int HTTP_POST_ERROR = 2;

    private HttpClient client;
    private HttpPost post;
    private HttpResponse response;

    public MyHttpPost(String link){
        client  = new DefaultHttpClient();
        post    = new HttpPost(link);
    }

    public void doPost(final ArrayList<NameValuePair> data, final Handler handler){
        new Thread(){
            public void run(){
                Message message = new Message();
                message.what = HTTP_POST_ERROR;

                try{
                    post.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
                    response = client.execute(post);
                    
                    message.what = HTTP_POST_OK;
                    message.obj = (String) EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    handler.sendMessage(message);
                }
            };
        }.start();
    }
}
