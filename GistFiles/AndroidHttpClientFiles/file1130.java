package com.example.mizuno.prog17_02;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mizuno on 2017/02/24.
 */

public class AsyncTaskGetJson extends AsyncTask<Void, Void, String> {
    private  String API_URL;
    private TraceActivity activity;

    public AsyncTaskGetJson(TraceActivity activity, String device_id) {
        this.activity = activity;
        API_URL = "http://ms000.sist.ac.jp/oc/positions/api/" + device_id;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result = new String();
        ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(postData, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String data = EntityUtils.toString(responseEntity);

                JSONObject rootObject = new JSONObject(data);

                JSONArray userArray = rootObject.getJSONArray("Position");
                Log.d("json1_data", userArray.toString());

                for (int n = 0; n < userArray.length(); n++) {
                    // User data
                    JSONObject userObject = userArray.getJSONObject(n);
                    int id = userObject.getInt("id");
                    double latitude = userObject.getDouble("latitude");
                    double longitude = userObject.getDouble("longitude");
                    String created = userObject.getString("created");
                    result += latitude + "," + longitude+",";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        String str[] = s.split(",", 0);
        double geo[] = new double[str.length];
        for(int i = 0; i< geo.length; i++) geo[i] = Double.parseDouble(str[i]);
        activity.mapLocation(geo);
    }
}
