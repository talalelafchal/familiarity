package com.deep.profilemaper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class AddVenueActivity extends AppCompatActivity implements OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		GoogleMap.OnMarkerDragListener,
		GoogleMap.OnMarkerClickListener,View.OnClickListener {


	private GoogleApiClient mGoogleApiClient;
	private GoogleMap					googleMap;

	private Double						latitude	= 0d, longitude = 0d;
	private Button saveLocationButton, viewSavedLocations;

	private static final String TAG = "AddVenueActivity";

	private static final int REQUEST_LOCATION = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestLocationPermission();
		setContentView(R.layout.activity_add_venue);


		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		//Initializing googleapi client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		saveLocationButton = (Button) findViewById(R.id.save_btn);
		saveLocationButton.setOnClickListener(this);

		Intent targetIntent = new Intent(this, LocationsUpdateService.class);
		startService(targetIntent);

	}

	@Override
	protected void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}

	@Override
	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		//Initializing our map
		this.googleMap = googleMap;
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			googleMap.setMyLocationEnabled(true);
			Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//			Toast.makeText(this,"Location"+location.getLongitude()+": "+location.getLatitude(),Toast.LENGTH_LONG).show();
			if (location != null) {

				longitude = location.getLongitude();
				latitude = location.getLatitude();

				if (latitude != 0 && longitude != 0) {
					googleMap.clear();

					Marker currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(
							new LatLng(latitude, longitude)).icon(
							BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

					googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

					googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

					currentLocationMarker.setDraggable(true);
					googleMap.setOnMarkerClickListener(this);
					googleMap.setOnMarkerDragListener(this);

				}

			}

		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {

		getCurrentLocation();

	}


	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	@Override
	public void onMarkerDrag(Marker marker) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {

		LatLng lastMarker = marker.getPosition();
		latitude = lastMarker.latitude;
		longitude = lastMarker.longitude;

		Log.d("Custom Location :" ,"Latitude :"+latitude+" Longitude : "+longitude);
	}

	//Getting current location
	private void getCurrentLocation() {
		//Creating a location object

		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

			Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//		Toast.makeText(this,"Location"+location.getLongitude()+": "+location.getLatitude(),Toast.LENGTH_LONG).show();
			if (location != null) {

				longitude = location.getLongitude();
				latitude = location.getLatitude();

				if (latitude != 0 && longitude != 0) {
					googleMap.clear();

					Marker currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(
							new LatLng(latitude, longitude)).icon(
							BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

					googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

					googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

					currentLocationMarker.setDraggable(true);
					googleMap.setOnMarkerClickListener(this);
					googleMap.setOnMarkerDragListener(this);

				}

			}

		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		Log.d("Current Location :" ,"Latitude :"+latitude+" Longitude : "+longitude);

		Intent i = new Intent(this, AddZoneActivity.class);
		i.putExtra(AppConstants.KEY_LATITUDE, latitude);
		i.putExtra(AppConstants.KEY_LONGITUDE, longitude);
		startActivity(i);
		return true;
	}

	@Override
	public void onClick(View view) {

		switch (view.getId())
		{
			case R.id.save_btn:
				Intent i = new Intent(this, AddZoneActivity.class);
				i.putExtra(AppConstants.KEY_LATITUDE, latitude);
				i.putExtra(AppConstants.KEY_LONGITUDE, longitude);
				startActivity(i);
				break;
		}

	}

	public boolean requestLocationPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED) {
				Log.v(TAG, "Permission is granted");
				return true;
			} else {

				Log.v(TAG, "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
				return false;
			}
		} else { 
    //permission is automatically granted on sdk<23 upon installation
			Log.v(TAG, "Permission is granted");
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {

			case REQUEST_LOCATION:

				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {


					if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

						Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

						if (location != null) {

							longitude = location.getLongitude();
							latitude = location.getLatitude();

							if (latitude != 0 && longitude != 0) {
								googleMap.clear();

								Marker currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(
										new LatLng(latitude, longitude)).icon(
										BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

								googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

								googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

								currentLocationMarker.setDraggable(true);
								googleMap.setOnMarkerClickListener(this);
								googleMap.setOnMarkerDragListener(this);

							}

						}

					}
				}
				else
				{
					this.finish();
				}
				break;


			default:
				break;
		}
	}
}
