package com.ozateck.notification;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{
	
	private static final String TAG = "MainActivity";
	
	private Button notifyBtn;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);
		
		notifyBtn = (Button)this.findViewById(R.id.notify_btn);
		notifyBtn.setOnClickListener(this);
		
		// Calendar
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 27);
		startCal.set(Calendar.SECOND, 0);
		long startTime = startCal.getTimeInMillis();
		
		// BroadcastReceiver
		Intent wdReceiver = new Intent(this, WeatherDetectReceiver.class);
		PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, wdReceiver, PendingIntent.FLAG_CANCEL_CURRENT);
		
		// AlermManager
		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, startTime, pIntent);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	@Override
	public void onClick(View view){
		
		if(view == notifyBtn){
			
		}
	}
}
