package com.example.summer.newapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat;
    private double lon;
    private String name;


    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lat = getIntent().getExtras().getDouble("lat");
        lon = getIntent().getExtras().getDouble("lon");
        name = getIntent().getStringExtra("name");

////        lat = getIntent().getDoubleExtra("Lat", -34);
////        lat = getIntent().getDoubleExtra("Lon", 151);
////        name = getIntent().getStringExtra("Name");
//
//
  }
//
//
    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap = googleMap;
////
////
////        // Add a marker in Sydney and move the camera
////       LatLng mesto = new LatLng(lat, lon);
////        mMap.addMarker(new MarkerOptions().position(mesto).title("Name"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(mesto));
//
//        // googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name));
        LatLng mesto = new LatLng(lat, lon);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mesto, 12));
        googleMap.addMarker(new MarkerOptions().position(mesto).title(name));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5), 3000, null);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mesto));
    }


}
