package com.stonete.qrtoken.utils;

import android.content.Context;

import com.stonete.qrtoken.activity.IActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class MyLog {

    public static boolean DEBUG = true;
    // --Commented out by Inspection (2015/3/4 11:58):private static Boolean MYLOG_WRITE_TO_FILE = false;
    // --Commented out by Inspection (2015/3/4 11:58):private static SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String TAG = "MyLog";


    public static void w(String tag, String msg) {
        if (!DEBUG) return;
        msg = "==> " + msg;
        android.util.Log.w(tag, msg);
    }

    public static void e(String tag, String xmsg) {
        if (!DEBUG) return;
        String msg = "==> " + xmsg;
        android.util.Log.e(TAG, msg);
        writeMsg2LogFile("E", xmsg);
    }

    public static void writeMsg2LogFile(String LogType,String msg){
        logFile = getLogFile();
        if(logFile!= null && logFile.canWrite()){
            try {
                FileWriter writer = new FileWriter(logFile.getAbsolutePath(), true);
                msg = TimeUtils.currentFormateData() + "= " + LogType + " =>"  +  msg + "\n";
                writer.write(msg);
                writer.close();
//                android.util.Log.e(TAG, "LOG PATH=" + logFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void i(Object msg) {
        i("MyLog", String.valueOf(msg));
    }

    public static void d(String tag, String msg) {
        if (!DEBUG) return;
        android.util.Log.d(TAG, tag + msg);
    }

    public static void i(String tag, String msg) {
        if (!DEBUG) return;
        writeMsg2LogFile("I", tag + msg);
        msg = "==> " + msg;
        android.util.Log.i(TAG, tag + msg);
    }

    public static void v(String tag, String msg) {
        msg = "==> " + msg;
        if (!DEBUG) return;
        android.util.Log.v(TAG, tag + msg);
    }

    private static boolean initLogFile() {
        logFile = getLogFile(false);
        return logFile != null;
    }

    public static File getLogFile(boolean random,int... errorCode){
        if (!DEBUG) return null;
        Context ctx = IActivity.currentActivity;
        String sdCardPath = null;
        if(ctx != null){
            sdCardPath = FileUtils.getCatchPath(ctx);
        }

        if(sdCardPath == null){
            return null;
        }
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int day = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
        String xDay = month + "-" + day;
        for(int ec : errorCode){
            xDay = xDay + "_" + ec;
        }

        String filedir = sdCardPath +  "/" + xDay + ".txt";
        if(random){
            xDay = xDay + "_" + UUID.randomUUID().hashCode();
            filedir = sdCardPath +  "/" + xDay + "response.htm";
        }


        File file = new File(filedir);
        if (!file.exists()) {
            try {
                file.createNewFile();
                MyLog.i("create log file " + filedir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private static File logFile = null;
    public static  File getLogFile(){
        if(logFile == null){
            initLogFile();
        }
        return logFile;
    }

}
