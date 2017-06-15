package com.map;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.os.Bundle;

public class MainActivity extends MapActivity  {
	MapView mp;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mp = (MapView) findViewById(R.id.mapView);
		mp.setBuiltInZoomControls(true);
		mp.getController().setZoom(9);
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	
}