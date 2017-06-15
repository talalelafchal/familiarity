package edu.ualr.cpsc7398.updatechecker.controller.service.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by uddhav on 2/16/17.
 */
public class OnLineTracker {
    public static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void catchException(Exception ex) {
        ex.printStackTrace();
    }
}
