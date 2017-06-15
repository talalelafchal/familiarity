package com.ozateck.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class SubActivity extends Activity implements View.OnClickListener{

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_sub);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	@Override
	public void onClick(View view){
		
	}
}
