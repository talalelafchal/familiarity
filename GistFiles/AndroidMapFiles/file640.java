package com.example.mapwebview;

import java.util.Currency;
import java.util.Timer;
import java.util.TimerTask;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyMapActivity extends Activity implements LocationListener {
  private WebView myWebView;
	private Intent intent;
	private LocationManager locManager;
	private Location current_location;
	private static final String MAP_URL = "http://gmaps-samples.googlecode.com/svn/trunk/articles-android-webmap/simple-android-map.html";
	Timer timer;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setBuiltInZoomControls(true);
		myWebView.setWebViewClient(new WebViewClient());
		intent = getIntent();
		final Double lat = Double.parseDouble(intent.getStringExtra("lat"));
		final Double lon = Double.parseDouble(intent.getStringExtra("lon"));

		final int draw_path = intent.getIntExtra("draw_path", 0);
		current_location = getCurrentLocation();
		final String smarker = "javascript:marker(" + lat + "," + lon + ")";
		final String centerURL = "javascript:center(" + lat + "," + lon + ")";

		if (current_location != null) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			if (draw_path == 1) {
				myWebView.loadUrl("https://maps.google.com/maps?saddr="
						+ current_location.getLatitude() + ","
						+ current_location.getLongitude() + "&daddr=" + lat
						+ "," + lon + "&&output=embed");
			} else {
				myWebView.loadUrl("https://maps.google.com/maps?q=" + lat + ","
						+ lon + "&z=16&output=embed");
			} 
		} else {
			myWebView.loadUrl("https://maps.google.com/maps?q=" + lat + ","
					+ lon + "&output=embed&z=16");
		} 
		try {
			timer.schedule(new GetLostLocation(), 1000, 5000);
		} catch (Exception e) {
		}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	Location getCurrentLocation() {
		Location location;
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				this);
		location = locManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

	class GetLostLocation extends TimerTask {
		public void run() {
			getCurrentLocation();

		}
	}

	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
