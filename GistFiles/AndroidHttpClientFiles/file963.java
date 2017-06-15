package net.tomlins.testapplication.json;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONHelper {

    private static int CONNECTION_TIMEOUT = 5000;
    private static int SOCKET_TIMEOUT = 5000;

    public static final String TAG = "JSONHelper";


    public static JSONObject getJSONObjectFromUrl(String url, int connTimeOut, int sockTimeOut) {
        CONNECTION_TIMEOUT = connTimeOut;
        SOCKET_TIMEOUT = sockTimeOut;
        return getJSONObjectFromUrl(url);
    }

    public static JSONObject getJSONObjectFromUrl(String url) {


        InputStream is;
        try {
            HttpParams httpParameters = new BasicHttpParams();

            // Set the connection timeout in milliseconds. Zero means no timeout.
            HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);

            // Set the socket timeout in milliseconds. Zero means no timeout.
            HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

            HttpClient httpclient = new DefaultHttpClient(httpParameters); // for port 80 requests!
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }
        catch (Exception e) {
            Log.e(TAG, "Connection/Socket Timeout on request");
            return null;
        }

        // Read response to string
        String result;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            return null;
        }

        // Convert string to JSONObject
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }

        return jsonObject;

    }

}