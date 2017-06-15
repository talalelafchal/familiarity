package com.mypackage;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class connect_api {

    private HttpClient httpClient = new DefaultHttpClient();
    private MultipartEntity mpEntity=new MultipartEntity();

    public JSONObject http_call(String action, JSONArray params) throws UnsupportedEncodingException, JSONException, NullPointerException {

        JSONObject result=new JSONObject();
        HttpPost httpPost = new HttpPost("http://192.168.1.60:3000/"+ action);

        // Add parameters
        for (int i=0; i<params.length(); i++) {
            JSONObject object = params.getJSONObject(i);
            String key = object.getString("key");
            String value = object.getString("value");
            mpEntity.addPart(key, new StringBody(value));
        }

        httpPost.setEntity(mpEntity);

        //making POST request.
        try {
            org.apache.http.HttpResponse response = httpClient.execute(httpPost);
            String jsonString = EntityUtils.toString(response.getEntity());
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                result = jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
        }
    return result;
    }

    public void add_file(File img){
        ContentBody cbFile=new FileBody(img);
        mpEntity.addPart("file",cbFile);
    }

    public void add_to_json(String key, String value, JSONArray params) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("value", value);
        params.put(json);
    }

    public JSONObject get_json(JSONArray params, String link) throws UnsupportedEncodingException, JSONException {
        JSONObject getData = http_call(link, params);
        return getData;
    }

    public ArrayList<String> get_results(JSONObject getData, String parent, String child) throws JSONException {

        JSONArray array = getData.getJSONArray(parent);
        ArrayList<String> result = new ArrayList<String>();

        for (int i=0; i<array.length(); i++) {
            JSONObject jsonObjectGames = new JSONObject(array.getString(i));
            String elt = jsonObjectGames.getString(child);
            result.add(elt);
        }
        return result;
    }
}
