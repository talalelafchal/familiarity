package com.example.androidtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Seb on 15.08.13.
 */
public class InternetThread extends Thread {

    Handler handler;

    public InternetThread(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://www.flowtrail-stromberg.de/");

        try
        {
            HttpResponse response = httpclient.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String website = sb.toString();

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("website", website);
            msg.setData(bundle);
            handler.sendMessageAtFrontOfQueue(msg);
        }
        catch (Exception e)
        {
            //TODO Fehlermeldung
            e.printStackTrace();
        }
    }
}
