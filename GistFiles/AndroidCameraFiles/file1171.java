package com.ozateck.googlemaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.DialogInterface;
import android.util.Log;

import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MapsActivity extends Activity
		implements OnMapReadyCallback, OnMapClickListener,
		OnMarkerClickListener, OnMarkerDragListener{

	private final String TAG = "MapsActivity";
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// Main Layout
		setContentView(R.layout.activity_maps);

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
	protected void onResume(){
		super.onResume();
	}

	@Override
	public void onMapReady(GoogleMap map){

		// Center
		LatLng centerPos = new LatLng(35.367895, 136.637358);
		CameraPosition cameraPos = CameraPosition.builder()
				.target(centerPos)
				.zoom(15)
				.tilt(45.0f)
				.build();

		// Default position / Type
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// Marker
		MarkerOptions mCat = new MarkerOptions();
		mCat.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_0));
		mCat.anchor(0.5f, 1.0f).position(centerPos);
		mCat.title("トラ猫");
		mCat.snippet("名前はまだ無いのである。");
		mCat.draggable(true);
		map.addMarker(mCat);

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
