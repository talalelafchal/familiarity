package com.stonete.qrtoken.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by kangwei on 2015/1/26.
 */
public class MyToast {


    public static void show(Context ctx, String msg) {
        if (MyLog.DEBUG) {
            Toast.makeText(ctx,ctx.getPackageName() + " Toast==>"+msg,Toast.LENGTH_SHORT).show();
        }
    }

}
