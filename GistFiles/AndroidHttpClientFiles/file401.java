package com.stonete.qrtoken.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;

@SuppressLint("NewApi")
public class AppOnlineUpdate {
    private final static String TAG = "AppOnlineUpdate";

    // version json url
    private final static String VERURL = "http://mloader.sinaapp.com/NFC_Flower_version.html";
    // apk download directory
    private final static String DOWNLOADDIR = Environment.DIRECTORY_DOWNLOADS;
    // apk name
    private final static String APKNAME = "QRToken.apk";
    // apk download url
    private static String APKURL = "";
    private Context mContext;
    private String curVerName;
    private String newVerName = "1.0";
    private int curVerCode;
    private int newVerCode = 1;
    private long dmID;
    private DownloadManager dm;

// --Commented out by Inspection START (2015/3/4 11:58):
//    public AppOnlineUpdate(Context mContext) {
//        this.mContext = mContext;
//        curVerName = getVerName(mContext);
//        curVerCode = getVerCode(mContext);
//        dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//        APKURL = GetDownload_URL.getDownload_URL(mContext);
//    }
// --Commented out by Inspection STOP (2015/3/4 11:58)

    private static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
    }

    private static String getVerName(Context context) {
        String verName = "";
        try {
            verName =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verName;
    }


    private String getURLResponse(String urlString) {
        HttpURLConnection conn = null;
        InputStream is = null;
        String resultData = "";
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine = null;
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return resultData;
    }

    private boolean parseJson(String data) throws JSONException {
        if (!data.equals("")) {
            // Log.d(TAG, "data" + data);
            String verjson = data;
            JSONArray array = new JSONArray(verjson);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                try {
                    newVerCode = Integer.parseInt(obj.getString("verCode"));
                    newVerName = obj.getString("verName");

                } catch (Exception e) {
                    newVerCode = 1;
                    newVerName = "1.0";
                    return false;
                }
            }
        }
        return true;
    }

    private void deleteOldFile() {
        File file = new File(Environment.getExternalStoragePublicDirectory(DOWNLOADDIR), APKNAME);
        if (file.exists())
            file.delete();
    }

}
