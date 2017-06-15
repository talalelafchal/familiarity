package com.example.myfirstapp;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.maps.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
/**
 * @author Sai Valluri
 * This class is a representation of a Google Map. In the application, it shows a google map. Then it closes onto
 * the coordinates(retrieved from GPS tracker) of the current location of the user and displays a list of nearby 
 * restaurants which is already implemented in the Places API
 * 
 */
public class RestaurantLocator extends Activity {
    private GoogleMap mMap;
    private GPSTracker gps;
    private LatLng coordinate;
    private CameraUpdate center;
    private CameraUpdate zoom;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        gps = new GPSTracker(this);
        
        mMap.setMyLocationEnabled(true);
        coordinate = new LatLng(gps.getLatitude(), gps.getLongitude());
        center = CameraUpdateFactory.newLatLng(coordinate);
        zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        
}
       
}
