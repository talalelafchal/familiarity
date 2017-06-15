package com.example.wenfahu.simplecam;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wenfahu on 16-9-21.
 */
public abstract class CWorker implements Runnable {
    private Handler handler;
    private Context context;
    public Bundle data;
    public Mat input;
    public int lastError;
    public boolean running;

    public CWorker(Handler handler, Context context, Mat input) {
        this.handler = handler;
        this.context = context;
        this.data = null;
        this.running = false;
        this.input = input;
    }

    public void run() {
        this.running = true;
        this.data = new Bundle();
        this.lastError = 0;
        this.lastError = doRun(this.input);
    }

    public abstract int work(Mat input, Bundle data);

    public void updateUI(Bundle data) {}

    public void postData(Mat output, Bundle data) {}

    public int doRun(Mat input) {
        int error = -128;
        try {
            error = work(input, data);
            Log.d(this.getClass().getName(), "doRun: end");
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
        if (handler != null) {
            Message msg = new Message();
            msg.setData(data);
            handler.sendMessage(msg);
        }
        return error;
    }
}
