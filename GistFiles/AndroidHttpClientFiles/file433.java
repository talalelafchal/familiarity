package dev.coldcore.finanzbuch.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Dominik on 03.09.13.
 */
public class JsonHelper {

    public String getRest(String url) {

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("accept", "application/json");

        try {
            if (httpGet != null && client != null) {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);

                    }
                } else {
                    Log.e(JsonHelper.class.toString(), "Failed to download file");
                }
            } else {
                Log.e(JsonHelper.class.toString(), "incorrect url");
            }

        } catch (ClientProtocolException e) {
            Log.e(JsonHelper.class.toString(), e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(JsonHelper.class.toString(), e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(JsonHelper.class.toString(), e.getMessage());
            e.printStackTrace();
        }

        return builder.toString();
    }


    public String putRest(String url, String jsonString) {

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);


        try {
            if (httpPut != null && client != null) {

                httpPut.setHeader("Content-Type", "application/json");
                httpPut.setHeader("Accept", "application/json");

                StringEntity stringEntity = new StringEntity(jsonString);
                stringEntity.setContentType("application/json;charset=UTF-8");
                stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
                httpPut.setEntity(stringEntity);

                HttpResponse response = client.execute(httpPut);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);

                    }
                } else {
                    Log.e(JsonHelper.class.toString(), "Failed to download file");
                }
            } else {
                Log.e(JsonHelper.class.toString(), "incorrect url");
            }

        } catch (ClientProtocolException e) {
            Log.e(JsonHelper.class.toString(), e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(JsonHelper.class.toString(), e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(JsonHelper.class.toString(), e.getMessage());
            e.printStackTrace();
        }
        Log.i("JSONHelper", builder.toString());
        return builder.toString();
    }





}