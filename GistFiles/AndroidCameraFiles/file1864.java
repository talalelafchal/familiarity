package com.chocoy.mypunch;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PushService extends IntentService {

    public PushService(){
        super("PushService");
    }

    private static int valueToPush;

    private static Long executeDateLongMillis;

    private static Long delayMillis = 5000L;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PushService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(this.getClass().getName(),"onHandleIntent:");

        valueToPush = intent.getIntExtra("valueToPush",0);
        executeDateLongMillis = System.currentTimeMillis();

        Log.d(this.getClass().getName(),"valueToPush is " + valueToPush);

        Log.d(this.getClass().getName(),this.toString());

    }

    class DelayThread extends Thread{

        @Override
        public void run() {
            super.run();

            Long millisPassed = System.currentTimeMillis() - executeDateLongMillis;

            if(millisPassed >= delayMillis){
                Log.d(getClass().getName(), "It is the time to push the value :" + valueToPush);
            } else {

                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

