

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon Dyer on 4/22/16.
 */
public class AJAJ {

    public static void ajaj(String url, Callback callback, String[]... params) {
        new AJAJ(url, callback, params);
    }

    private AJAJ(String url, Callback callback, String[][] params) {
        Task task = new Task(url, callback, params);
        task.execute();
    }

    public interface Callback {
        public void callback(JSONObject json) throws JSONException;
    }

    private class Task extends AsyncTask<Void, Void, Boolean> {

        private final String url;
        private final Callback callback;
        private final String[][] params;
        private String response;

        Task(String url, Callback callback, String[][] params) {
            this.url = url;
            this.callback = callback;
            this.params = params;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            response = postData(url, this.params);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                System.out.println("SERVER RESPONSE: " + response);
                JSONObject json = new JSONObject(response);
                callback.callback(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private static String postData(String url, String[]... params) {
        // Create a new HttpClient and Post Header
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            for (String[] param : params) {
                nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String response = httpclient.execute(httppost, responseHandler);
            return response;
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return null;
    }

}