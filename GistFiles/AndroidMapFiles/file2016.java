package com.joshdholtz.sentry;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.joshdholtz.sentry.Sentry.SentryEventBuilder;
import com.joshdholtz.sentry.Sentry.SentryEventBuilder.SentryEventLevel;
import com.joshdholtz.sentry.Sentry.SentryEventCaptureListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Sentry will look for uncaught exceptions from previous runs and send them        
		Sentry.init(this, "YOUR-DSN");

		// Sets a listener to intercept the SentryEventBuilder before 
		// each capture to set values that could change state
		Sentry.setCaptureListener(new SentryEventCaptureListener() {

			@Override
			public SentryEventBuilder beforeCapture(SentryEventBuilder builder) {

				// Needs permission - <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
				ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				// Sets extra key if wifi is connected
				try {
					builder.getExtra().put("wifi", String.valueOf(mWifi.isConnected()));
				} catch (JSONException e) {}

				return builder;
			}

		});

		// Capture event
		Sentry.captureEvent(new Sentry.SentryEventBuilder()
			.setMessage("Being awesome at stuff")
			.setCulprit("Josh D Holtz")
			.setTimestamp(System.currentTimeMillis())
		);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
