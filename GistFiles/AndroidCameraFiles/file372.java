package com.chocoy.mypunch;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by 105762 on 2015/1/15.
 */
public class MainService extends Service {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MainService(String name) {
        super();
        //TODO
        Log.i("MainService","constructor is called.");
    }

    public MainService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(this.getClass().toString(),"onCreate.");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
        public int getRandomInt(){
            return (int)(Math.random() * 100);
        }
    }
}
