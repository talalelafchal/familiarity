package com.kalyan.fsdashboard.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.webkit.WebView;
import android.app.Activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Timer;

/**
 * Created by kalyan on 12/17/15.
 */
public class ReloadWebView extends TimerTask {
    Activity context;
    Timer timer;
    WebView wv;

    public ReloadWebView(Activity context, int seconds, WebView wv) {
        this.context = context;
        this.wv = wv;

        timer = new Timer();
        /* execute the first task after seconds */
        timer.schedule(this,
                seconds * 1000,  // initial delay
                seconds * 1000); // subsequent rate

        /* if you want to execute the first task immediatly */
        /*
        timer.schedule(this,
                0,               // initial delay null
                seconds * 1000); // subsequent rate
        */
    }

    @Override
    public void run() {
        if(context == null || context.isFinishing()) {
            // Activity killed
            this.cancel();
            return;
        }

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                AsyncTask<String, Void, Void> checkURL = new AsyncTask<String,Void,Void>() {
//                    @Override
//                    protected void onPreExecute() {
//                    }
//
//                    @Override
//                    protected Void doInBackground(String... urls) {
//                        int iHTTPStatus;
//                        // Making HTTP request
//                        try {
//                            // defaultHttpClient
//                            DefaultHttpClient httpClient = new DefaultHttpClient();
//                            HttpGet httpRequest = new HttpGet(urls[0]);
//                            HttpResponse httpResponse = httpClient.execute(httpRequest);
//                            iHTTPStatus = httpResponse.getStatusLine().getStatusCode();
//                            System.out.print(iHTTPStatus);
//                            if( iHTTPStatus != 200) {
//                                //Reload
//                                wv.loadUrl(urls[0]);
//                            }
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//
//                        } catch (ClientProtocolException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//
//                        }  catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//                };
//               checkURL.execute(wv.getUrl());
                wv.reload();
            }
        });
    }
}