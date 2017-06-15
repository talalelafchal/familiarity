package com.ozateck.notification;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.IBinder;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class WeatherDetectService extends Service{

	private static final String TAG = "WeatherDetectService";
	private NotificationManager nManager;
	private static final int WEATHER_NOTIFICATION = 19790213;
	
	@Override
	public IBinder onBind(Intent intent){
		Log.d(TAG, "onBind");
		return null;
	}
	
	@Override
	public void onCreate(){
		Log.d(TAG, "onCreate");

		// NotificationManager
		nManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		Log.d(TAG, "onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(TAG, "onStartCommand");
		
		// Start detection
		startDetection();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy");
	}
	
	private void startDetection(){
		Log.d(TAG, "startDetection");
		
		if(isGPSActive() == false){
			Log.e(TAG, "isGPSActive:false");
			return;
		}
		
		Location location = getLastKnownLocation();
		if(location == null){
			Log.e(TAG, "location:null");
			return;
		}
		Log.d(TAG, "lat:" + location.getLatitude());
		Log.d(TAG, "lon:" + location.getLongitude());
		
		if(isOnline() == false){
			Log.e(TAG, "isOnline:false");
			return;
		}

		WeatherDetectJson jd = new WeatherDetectJson(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "name:" + jd.getNameStr());
		Log.d(TAG, "main:" + jd.getMainStr());
		Log.d(TAG, "description:" + jd.getDescriptionStr());
		String nameStr = jd.getNameStr();
		String mainStr = jd.getMainStr();
		String descriptionStr = jd.getDescriptionStr();
		
		// Notification
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "NotificationSample";
		notification.number = WEATHER_NOTIFICATION;

		// PendingIntent
		Intent intent = new Intent(this.getApplicationContext(), SubActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
		notification.setLatestEventInfo(this.getApplicationContext(),
				"Weather[" + nameStr + "]", mainStr + ":" + descriptionStr, pIntent);
		nManager.notify(WEATHER_NOTIFICATION, notification);
		
		// Finish this service
		this.stopSelf();
	}
	
	private boolean isGPSActive(){
		
		try{
			int mode = Settings.Secure.getInt(
					getContentResolver(),
					Settings.Secure.LOCATION_MODE);
			
			if(mode == Settings.Secure.LOCATION_MODE_OFF){
				Log.d(TAG, "LOCATION_MODE_OFF");
				return false;
			}else if(mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY){
				Log.d(TAG, "LOCATION_MODE_SENSORS_ONLY");
				return true;
			}else if(mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING){
				Log.d(TAG, "LOCATION_MODE_BATTERY_SAVING");
				return true;
			}else if(mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
				Log.d(TAG, "LOCATION_MODE_HIGH_ACCURACY");
				return true;
			}else{
				Log.d(TAG, "OTHER");
				return false;
			}
			
		}catch(SettingNotFoundException e){
			Log.e(TAG, "SNFE:" + e.toString());
		}
		return false;
	}
	
	private Location getLastKnownLocation(){
		
		LocationManager lManager =
				(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); 
		String provider = lManager.getBestProvider(criteria, true);
		Log.d(TAG, "provider:" + provider);
		return lManager.getLastKnownLocation(provider);
	}
	
	private boolean isOnline(){
		
		ConnectivityManager cm = 
				(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info != null){
			int type = info.getType();
			if(type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI){
				Log.d(TAG, "NetworkInfo:" + info.getTypeName());
				return true;
			}
		}
		return false;
	}
}
