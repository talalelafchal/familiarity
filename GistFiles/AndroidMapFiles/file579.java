package com.example.my_android_google_maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

	private GoogleMap my_google_maps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			
			if (my_google_maps == null) {
                my_google_maps = ((SupportMapFragment) getSupportFragmentManager().
                        findFragmentById(R.id.map)).getMap();}
			
			my_google_maps.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			 
			my_google_maps.setMyLocationEnabled(true);
			
			my_google_maps.setBuildingsEnabled(true); 
			
			my_google_maps.setTrafficEnabled(true); 
			
			my_google_maps.setIndoorEnabled(true);
           
			my_google_maps.getUiSettings().setZoomControlsEnabled(true);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
}