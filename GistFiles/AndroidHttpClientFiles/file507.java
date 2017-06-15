package edu.ualr.cpsc7398.updatechecker.controller.service.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by uddhav on 2/23/17.
 */
public class CheckUrlResourceDynamicOrStatic implements Runnable {
    private Request request;
    private OkHttpClient httpClient;
    private boolean status = false;
    private Handler h2;

    public CheckUrlResourceDynamicOrStatic(Request request, OkHttpClient httpClient, Handler h1) {
        this.request = request;
        this.httpClient = httpClient;
        this.h2 = h1;
    }

    @Override
    public void run() {
        Response response = null; //if I execute like this in MainThread then I get /MessageQueue-JNI: android.os.NetworkOnMainThreadException
        try {
            response = httpClient.newCall(request).execute();
            Message m = Message.obtain(); //get null message
            Bundle bundle = new Bundle();

            Log.i("code", String.valueOf(response.code()));
            if (response.code() == 304) { //if not modified
                //use the handler to send message
                status = true;
                bundle.putBoolean("OURSTATUS", status); //As we can't pass the object from Bundle
                m.setData(bundle);
                h2.sendMessage(m); //Pushes a message onto the end of the message queue after all pending messages before the current time.

            } else { //if not modified
                //use the handler to send message
                status = false;
                bundle.putBoolean("OURSTATUS", status); //As we can't pass the object from Bundle
                m.setData(bundle);
                h2.sendMessage(m); //Pushes a message onto the end of the message queue after all pending messages before the current time.

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
