import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import com.emmadev.app.Main;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Eva_2 on 1/26/2016.
 */
public class HttpdActivity  {
    public static final String TAG = Main.class.getSimpleName();

    public static  String out = "0";


    public void networkIO(){
        //  Ker Networking cenah google mah ai d hapus error ==============================================
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    // Httpd POST ===========================================================================================================================================================
    public String POSTdata(String myurl, List param) throws IOException {
        // Bikin HTTP client =======================================
        HttpClient httpClient = new DefaultHttpClient();
        // Bikin HTTP Post =========================================
        HttpPost httpPost = new HttpPost(myurl);

        // Url Encoding POST ======================================
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(param));
        } catch (UnsupportedEncodingException e) {
            // Log Errors ========================================
            e.printStackTrace();
        }

        // Buat HTTP req =========================================
        try {
            HttpResponse response = httpClient.execute(httpPost);

            // Log Errors =======================================
            out = "-->>>>"+response.getStatusLine();
            if (response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                out = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            // Log Errors =======================================
            e.printStackTrace();
        } catch (IOException e) {
            // Log Errors =======================================
            e.printStackTrace();
        }
        return out;
    }



    // Httpd GET ===========================================================================================================================================================
    public String GETdata(String myurl) throws IOException{
        InputStream is = null;
        int length = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Buat  InputStream ke string ===============================================
            String contentAsString = convertInputStreamToString(is, length);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public static String JSONdata(String url, JSONObject jsonObject){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            /*
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", person.getName());
            jsonObject.accumulate("country", person.getCountry());
            jsonObject.accumulate("twitter", person.getTwitter());*/

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Content-encoding", "utf-8");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean checkConnection(ConnectivityManager conMgr){
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        Boolean out = false;
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }
}