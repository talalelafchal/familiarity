package com.your.package;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.your.package.R;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SampleActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "SampleActivity";

    private static final int REQUEST_LOCATION_PERMISSION = 3;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.map_view)
    MapView mapView;

    MapboxMap map;
    LocationEngine locationEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();

        /**
         *  Tried adding this to solve Memory Leak crash. No luck :(
         */
//        locationEngine.removeLocationUpdates();
//        locationEngine.deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;

        requestLocationPermissionIfNeeded(false);
    }

    protected void requestLocationPermissionIfNeeded(boolean forceLocationSettings) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            enableLocation(forceLocationSettings);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    enableLocation(false);
                }
            }
        }
    }

    private void enableLocation(boolean forceLocationSettings) {
        // If we have the last location of the user, we can move the camera to that position.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionIfNeeded(forceLocationSettings);
            return;
        }
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null && map != null) {
            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(lastLocation))
                            .zoom(13)
                            .build()));
        }

        locationEngine.addLocationEngineListener(new LocationEngineListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onLocationChanged(Location location) {
                // Move the map camera to where the user location is and then remove the
                // listener so the camera isn't constantly updating when the user location
                // changes. When the user disables and then enables the location again, this
                // listener is registered again and will adjust the camera once again.
                if (location != null && map != null) {
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location))
                                    .zoom(13)
                                    .build()));

                    locationEngine.removeLocationEngineListener(this);
                }
            }
        });

        // Enable or disable the location layer on the map
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.getMyLocationViewSettings().setAccuracyAlpha(0);
        }
    }
}
