/*
* Written by Hirsh Agarwal
* H2 Micro
*/

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Hirsh Agarwal on 2/17/2016.
 */
public class WebClient extends AsyncTask<String, Void, String>{

    //Fields
    String data;
    Context context;
    private String returnData;

    public WebClient(String dataList, Context context){
        data = dataList;
        this.context = context;
    }


    @Override
    protected String doInBackground(String... params) {
        StringBuffer response = new StringBuffer();
        try {
            String url = params[0];
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setUseCaches (false);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("organization", 1);
            String urlParameters = "data=test";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("RESPONSE --------------------------" +response.toString());
        returnData = response.toString();
        return response.toString();
    }

    public String getResponse(){
        return returnData;
    }
}