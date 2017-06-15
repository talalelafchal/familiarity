package com.tukangandroid.tutorial;

import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class TukangAndroidApp extends MapActivity{
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private MyOwnLocationOverlay myLocationOverlay;
	private MapController mapController;
	private TextView txtRadius;
	private Button btnSearch;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new SearchListener());
        
        txtRadius = (TextView) findViewById(R.id.txtRadius);
        
    }
    
	private class SearchListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			mapView.getOverlays().clear();
			mapOverlays = mapView.getOverlays();
	        
	        myLocationOverlay = new MyOwnLocationOverlay(TukangAndroidApp.this, mapView);
	        myLocationOverlay.setMeters(Integer.parseInt(txtRadius.getText().toString()));
	        myLocationOverlay.enableCompass();
	        myLocationOverlay.enableMyLocation();
	        myLocationOverlay.runOnFirstFix(new Runnable() {
	            public void run() {
	                mapController.animateTo(myLocationOverlay.getMyLocation());
	            }
	        });
	        mapView.getOverlays().add(myLocationOverlay);

			displayResults();
		}
    }
	
	private void displayResults() {
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
