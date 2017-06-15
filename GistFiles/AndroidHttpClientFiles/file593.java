package com.it_gets_and_posts_hopefully.webcalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by iseult on 15/01/17.
 */

public class GET extends AsyncTask<Void, Void, Void> {
    Activity activity;
    ProgressDialog progressDialog;
    public String desc;
    String url;
    String[] headerField;
    String[] header;

    public  GET(Activity activity, ProgressDialog progressDialog, String url, String[] headerField, String[] header) {
        this.activity = activity;
        this.progressDialog = progressDialog;
        this.url = url;
        this.headerField = headerField;
        this.header = header;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("OzEdu");
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            desc = GET(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);
            List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
            if (headerField != null) {
                for (int i = 0; i < headerField.length; ++i) {
                    params.add(new BasicNameValuePair(headerField[i], header[i]));
                }
                httpGet = new HttpGet(url + "?" + URLEncodedUtils.format(params, "utf-8"));
            }

            HttpResponse httpResponse = httpclient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {result = "Failed";}
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }
}