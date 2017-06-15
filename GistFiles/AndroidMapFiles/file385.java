package com.tukangandroid.tutorial;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TukangAndroidApp extends MapActivity{
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private MyOwnLocationOverlay myLocationOverlay;
	private MapController mapController;
	private TextView txtRadius;
	private Button btnSearch;
	private boolean isFound;
	
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
		// Create dummy list of GeoPoint
		GeoPoint point1 = new GeoPoint(35100000, 129100000);
		GeoPoint point2 = new GeoPoint(35110000, 129110000);
		GeoPoint point3 = new GeoPoint(35120000, 129120000);
		List<GeoPoint> points = new ArrayList<GeoPoint>();
		points.add(point1);
		points.add(point2);
		points.add(point3);
		mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.icon);
		HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(drawable);
		
        for(GeoPoint point : points) {
        	// Create a location because Location has a distanceTo method that we can
        	// use for buffering. Notice that distanceTo calculate distance in meter
	        Location gpsLocation = new Location("current location");
	        
	        // Get our current gps point and use it to create a location
	        GeoPoint currentLocation = myLocationOverlay.getMyLocation();
	        double lat = (double) (currentLocation.getLatitudeE6() / 1000000.0);
	        double lng = (double) (currentLocation.getLongitudeE6() / 1000000.0);
	        gpsLocation.setLatitude(lat);
	        gpsLocation.setLongitude(lng);
	        Location pointLocation = new Location("point");
	        pointLocation.setLatitude(point.getLatitudeE6() / 1000000.0);
	        pointLocation.setLongitude(point.getLongitudeE6() / 1000000.0);
	        
	        // Calculate the distance between current location and point location
	        if(gpsLocation.distanceTo(pointLocation) < Float.parseFloat(txtRadius.getText().toString())) {
		        isFound = true;
	        	OverlayItem overlayitem = new OverlayItem(point, "", "");
		        itemizedOverlay.addOverlay(overlayitem);
	        }
        }
        // If any location found, draw the placemark
        if(isFound)
        	mapOverlays.add(itemizedOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
