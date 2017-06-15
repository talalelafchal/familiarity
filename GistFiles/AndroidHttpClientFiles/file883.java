package com.ozateck.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WeatherDetectReceiver extends BroadcastReceiver{
	
	private static final String TAG = "WeatherDetectReceiver";

	@Override
	public void onReceive(Context context, Intent intent){
		Log.d(TAG, "onReceive");
		
		// Start service
		Intent wdService = new Intent(context, WeatherDetectService.class);
		context.startService(wdService);
	}
}
