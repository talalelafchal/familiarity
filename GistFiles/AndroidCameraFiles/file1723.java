package com.riot.projetoriotboothrfid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean status = NetworkUtil.getConnectivityStatusString(context);

        SplashActivity inst = SplashActivity.instance();

        MySingleton singleton = MySingleton.getInstance();

        singleton.setInternet(status);
        if(inst != null)  { // your activity can be seen, and you can update it's context
            inst.conexaoComInternet();
        }
    }
}