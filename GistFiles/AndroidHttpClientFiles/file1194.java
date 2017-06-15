package com.example.seymen.havadurumu;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Parse {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = null;
    public Parse(){

    }
    public static String getJSONFromUrl(String url) throws IOException {
        Log.e("URL",url);
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        }catch (UnsupportedEncodingException e){
            Log.e("JSON Parser", "UnsupportedEncodingException " + e.toString());
            e.printStackTrace();
        }catch (ClientProtocolException e){
            Log.e("JSON Parser", "ClientProtocolException " + e.toString());
            e.printStackTrace();
        }catch (IOException e){
            Log.e("JSON Parser", "IOException " + e.toString());
            e.printStackTrace();
        }
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine())!= null){
                sb.append(line + "\n");

            }
            is.close();
            json = sb.toString();

        }catch (Exception e){
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;
    }

}
