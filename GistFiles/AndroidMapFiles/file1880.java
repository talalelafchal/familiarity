package com.stonete.qrtoken.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by kangwei on 15/4/10.
 */
public class SmsUtil {

    public static String getSms(Context ctx){
        ContentResolver contentResolver = ctx.getContentResolver();
        Uri uri = Uri.parse("content://sms");
        Long now = System.currentTimeMillis();
        String selection = "date>" + now;
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);
        int second = 0;
        while(second<60 && !cursor.moveToFirst()){
            cursor.close();
            cursor = contentResolver.query(uri, null, selection, null, null);
            second++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        if(second != 60){
            String phone = cursor.getString(cursor.getColumnIndex("address"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));// 2 = sent, etc.
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            cursor.close();
            MyLog.i(phone + " " + type + " " + date + " " + body);
            if(body.contains("验证码是：")){
                String yzm = body.split("验证码是：")[1].substring(0,6);
                return yzm;
            }
            return null;
        }
        cursor.close();
        return null;

    }
}
