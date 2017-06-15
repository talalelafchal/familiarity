package com.kalyan.fsdashboard.util;

import android.os.AsyncTask;
import android.webkit.WebView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by kalyan on 12/17/15.
 */
public class ErrorCheck extends AsyncTask<String,Void,Boolean> {

    private Exception exception;


    @Override
    public Boolean doInBackground(String... url) {
        int iHTTPStatus;
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet(url[0]);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            iHTTPStatus = httpResponse.getStatusLine().getStatusCode();
            if( iHTTPStatus != 200) {
                //Reload
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

