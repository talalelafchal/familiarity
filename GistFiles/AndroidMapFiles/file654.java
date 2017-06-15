package com.ozateck.googlemaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.DialogInterface;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MapsActivity extends Activity
		implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMapReadyCallback, OnMapClickListener,
		OnMarkerClickListener, OnMarkerDragListener{

	private final String TAG = "MapsActivity";

	private boolean googleMapFlg = false;
	private GoogleMap mMap;
	private Marker cMarker;

	private boolean googleApiClientFlg = false;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private Location mLastLocation;
	private Location mCurrentLocation;

	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
	private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");

		// Main Layout
		setContentView(R.layout.activity_maps);

		// GoogleApiClient
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		// LocationRequest
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// MapFragment
		MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		// Map
		mMap = mapFragment.getMap();
		mMap.setOnMapClickListener(this);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnMarkerDragListener(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onResume(){
		super.onResume();
	}

	@Override
	protected void onPause(){
		super.onPause();
	}

	@Override
	protected void onStop(){
		super.onStop();
		if (mGoogleApiClient.isConnected()){
			mGoogleApiClient.disconnect();
		}
	}

	//==========
	// Location
	@Override
	public void onConnected(Bundle connectionHint){
		Log.d(TAG, "onConnected");
		Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
		googleApiClientFlg = true;

		// Last location
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(mLastLocation != null){
			Log.d(TAG, "Lat:" + mLastLocation.getLatitude());
			Log.d(TAG, "Lon:" + mLastLocation.getLongitude());
			Toast.makeText(this,
					"onConnected:" +
							mLastLocation.getLatitude() + "_" +
							mLastLocation.getLongitude(),
					Toast.LENGTH_SHORT).show();
		}

		// RequestLocationUpdates
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int cause){
		Log.d(TAG, "onConnectionSuspended:" + cause);
		Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result){
		Log.d(TAG, "onConnectionFailed:" + result.toString());
		Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location){
		Log.d(TAG, "onLocationChanged");
		mCurrentLocation = location;
		if(mCurrentLocation != null){
			Log.d(TAG, "Lat:" + mCurrentLocation.getLatitude());
			Log.d(TAG, "Lon:" + mCurrentLocation.getLongitude());
			Toast.makeText(this,
					"onLocationChanged:" +
							mCurrentLocation.getLatitude() + "_" +
							mCurrentLocation.getLongitude(),
					Toast.LENGTH_SHORT).show();
		}

		// Move Marker
		if(googleApiClientFlg == true && googleMapFlg == true){

			// Center
			LatLng centerPos = new LatLng(
					mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude());
			CameraPosition cameraPos = CameraPosition.builder()
					.target(centerPos)
					.zoom(15)
					.tilt(45.0f)
					.build();

			// Map / Type
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			// Marker
			cMarker.setPosition(centerPos);
		}
	}

	//==========
	// GoogleMaps
	@Override
	public void onMapReady(GoogleMap map){
		Log.d(TAG, "onMapReady");
		googleMapFlg = true;

		// Center
		LatLng centerPos = new LatLng(35.3664838, 136.6301494);
		CameraPosition cameraPos = CameraPosition.builder()
				.target(centerPos)
				.zoom(15)
				.tilt(45.0f)
				.build();

		// Map
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// Marker
		MarkerOptions mOptions = new MarkerOptions();
		mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_0));
		mOptions.anchor(0.5f, 1.0f).position(centerPos);
		mOptions.title("俺");
		mOptions.snippet("貴様、見ているな!!");
		mOptions.draggable(false);// マーカードラッグ
		cMarker = map.addMarker(mOptions);

		/*
		map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1))
				.anchor(0.5f, 1.0f).position(new LatLng(35.3647413, 136.6349692))
				.title("おやじ")
				.snippet("名前はあるが、名乗りません。"));

		// Polylines
		map.addPolyline(new PolylineOptions().geodesic(true).width(3.0f)
						.add(new LatLng(35.367895, 136.637358))
						.add(new LatLng(35.367880, 136.637571))
						.add(new LatLng(35.366644, 136.637694))
						.add(new LatLng(35.366522, 136.636085))
						.add(new LatLng(35.364568, 136.636182))
						.add(new LatLng(35.364488, 136.634969))
		);
		*/
	}

	@Override
	public void onMapClick(LatLng point){
		Log.d(TAG, "point:" + point.toString());
	}

	@Override
	public boolean onMarkerClick(Marker marker){
		Log.d(TAG, "marker:" + marker.getId());

		// Animation
		CameraPosition cameraPos = CameraPosition.builder()
				.target(marker.getPosition())
				.zoom(15)
				.build();

		// Animate the change in camera view over 2 seconds
		mMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(cameraPos),
				1000, null);

		return false;
	}

	@Override
	public void onMarkerDragStart(Marker marker){
		Toast.makeText(getApplicationContext(), "マーカードラッグ開始", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onMarkerDrag(Marker marker){
		//Toast.makeText(getApplicationContext(), "マーカードラッグ中", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onMarkerDragEnd(Marker marker){
		Toast.makeText(getApplicationContext(), "マーカードラッグ終了", Toast.LENGTH_LONG).show();

		// AlertDialog
		showDialog("移動完了:" + marker.getId());
	}

	private void showDialog(String title){

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(title);
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				// OK button pressed
			}
		});
		AlertDialog ad = adb.create();
		ad.show();
	}
}
