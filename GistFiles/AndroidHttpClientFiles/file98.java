package com.example.kenyawakita.httpfileget;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
    public Activity owner;
    public String ReceiveStr;
    private String string;

    public AsyncHttpRequest(Activity activity) {
        owner = activity;
        }

            @Override
    protected String doInBackground(String... url) {
        try {
            HttpGet httpGet = new HttpGet(url[0]);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpGet.setHeader("Connection", "Keep-Alive");

            HttpResponse response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new Exception("");
            } else {
                ReceiveStr = string;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        }
            @Override
    protected void onPostExecute(String result) {
        TextView textView2 = (TextView) owner.findViewById(R.id.textView2);
        textView2.setText(ReceiveStr);
            }
}