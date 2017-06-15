package com.opennotifier.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast消息提示，对Toast进行二次封装
 * @author tangc
 */
public class Tip {

    /**
     * 消息提示
     * @param msg
     */
    public static void show(Context context, int msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
