package com.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SupportMapFragment sup = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		GoogleMap mMap = sup.getMap();
		        mMap.addMarker(new MarkerOptions().position(
		                new LatLng(18.899181,99.013081))
		                .title("MJU UNIVERSITY")
		                .snippet("MAEJO"));
		        mMap.addMarker(new MarkerOptions().position(
		                new LatLng(18.799068,98.954329))
		                .title("CMU UNIVERSITY"));
		        mMap.addMarker(new MarkerOptions().position(
		                new LatLng(18.805162,98.963342))
		                .title("PAYAP UNIVERSITY"));   
		        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
		                new LatLng(18.899181,99.013081), 11));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
