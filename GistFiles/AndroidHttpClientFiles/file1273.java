package com.jgdev.emplea_do.api;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Created by jgdev on 5/9/14.
 */
public abstract class ApiManager extends AsyncTask<String, Void, String> {

    public abstract void onResponse(String result, String etag );
    public abstract void onNotModified(String etag);
    public abstract void onFileNotFound(String result);
    public abstract void onError();

    private HttpClient mHttpClient = HttpClientBuilder.create().build();
    private Integer status = 0;
    private String etag;

    final String USER_AGENT = "Mozilla/5.0";

    @Override
    protected String doInBackground(String... params) {
        try {
            HttpGet request = new HttpGet(params[0]);

            request.setHeader("User-Agent", USER_AGENT);
            if(params[1]  != null) {
                request.setHeader("If-None-Match", params[1]);
            }

            HttpResponse response = mHttpClient.execute(request);

            etag = response.getFirstHeader("Etag").getValue();

            status = response.getStatusLine().getStatusCode();

            StringBuffer result = new StringBuffer();
            String content = "";

            try {
                BufferedReader mBufferReader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                while ((content = mBufferReader.readLine()) != null) {
                    result.append(content);
                }
            }
            catch (Exception e) { }

            return result.toString();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    public void onPostExecute(String result) {
        switch (status) {
            case 200:
                onResponse(result, this.etag);
                return;

            case 304:
                onNotModified(this.etag);
                return;

            case 404:
                onFileNotFound(result);
                return;

            case 500:
                onError();
                return;
        }

        onError();
    }
}