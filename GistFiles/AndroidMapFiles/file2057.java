package com.stonete.qrtoken.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AppUtils {

    /**
     * @param c
     * @param pkgName
     * @return 如果存在此应用，则返回app verion name,如果没有则返回null
     */


    public static PackageInfo getPkgInfo(Context c, String pkgName) {

        PackageManager pkgManager = c.getPackageManager();
        try {
            PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return pkgInfo;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersionNameByPkgName(Context c, String pkgName) {
        PackageManager pkgManager = c.getPackageManager();
        try {
            PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return pkgInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int getVersionCode(Context c, String pkgName){
        PackageManager pkgManager = c.getPackageManager();
        try {
            PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return pkgInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppNameByPkgName(Context ctx, String pkgName) {
        PackageManager pm = ctx.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(pm.getApplicationInfo(pkgName,
                    PackageManager.GET_META_DATA)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getDeviceId(Context ctx) {
        TelephonyManager TelephonyMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }

    public static String getBuildTime(Context ctx){
        String buildTime = null;
        try {
            InputStream ips = ctx.getAssets().open("build.txt");
            byte[] bts = new byte[1024];
            int len = ips.read(bts);
            buildTime = new String(bts, 0, len, "GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        buildTime = "编译时间 :" + buildTime;
        return buildTime;
    }

    public static void sendLog2QQ(Context ctx, String log){

        Intent itn = new Intent(android.content.Intent.ACTION_SEND);
        itn.setType("*/*");
        File logFile = MyLog.getLogFile(true);
        MyLog.i(logFile.getAbsolutePath());
        if(logFile == null && !logFile.canWrite()){
            MyToast.show(ctx, "log文件生成失败");
            return;
        }
        try {
            FileWriter writer = new FileWriter(logFile.getAbsolutePath(), true);
            writer.write(log);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        itn.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logFile));
        ctx.startActivity(itn);
    }

}
