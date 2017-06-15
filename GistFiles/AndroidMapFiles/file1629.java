package com.enginebai.sample;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
	public static final int LOCATION_UPDATE_MIN_TIME = 5000;

	@Bind(R.id.mapview)
	MapView mMapView;
	@Bind(R.id.toolbar)
	Toolbar mToolbar;
	@Bind(R.id.fab)
	FloatingActionButton mFab;

	private GoogleMap mGoogleMap;
	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Logger.d(String.format("%f, %f", location.getLatitude(), location.getLongitude()));
				drawMarker(location);
				mLocationManager.removeUpdates(mLocationListener);
			} else {
				Logger.d("Location is null");
			}
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {

		}

		@Override
		public void onProviderEnabled(String s) {

		}

		@Override
		public void onProviderDisabled(String s) {

		}
	};
	private LocationManager mLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Logger.init();
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getCurrentLocation();
			}
		});
		mMapView.onCreate(savedInstanceState);
		mGoogleMap = mMapView.getMap();
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		initMap();
		getCurrentLocation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		getCurrentLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		mLocationManager.removeUpdates(mLocationListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	private void initMap() {
		int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (googlePlayStatus != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, -1).show();
			finish();
		} else {
			if (mGoogleMap != null) {
				mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
				mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
			}
		}
	}

	private void getCurrentLocation() {
		boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		Location location = null;
		if (!(isGPSEnabled || isNetworkEnabled))
			Snackbar.make(mMapView, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
		else {
			if (isNetworkEnabled) {
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
				location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			if (isGPSEnabled) {
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
				location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
		}
		if (location != null) {
			Logger.d(String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
					location.getLongitude()));
			drawMarker(location);
		}
	}

	private void drawMarker(Location location) {
		if (mGoogleMap != null) {
			mGoogleMap.clear();
			LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
			mGoogleMap.addMarker(new MarkerOptions()
					.position(gps)
					.title("Current Position"));
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
		}

	}
}
