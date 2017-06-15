package com.dashlabs.dash.android.ui.vehicle.trace;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dashlabs.dash.android.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by cyuan on 5/31/17.
 */

public class VehicleTraceActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_trace);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Visited Cities");

        SupportMapFragment fragment = SupportMapFragment.newInstance();
        fragment.getMapAsync(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.map_container, fragment).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<City> cities = City.getCitiesByVehicleId("fake-vehicle-id");
        LatLngBounds.Builder latLngBuilder = LatLngBounds.builder();
        for (City city : cities) {
            googleMap.addMarker(new MarkerOptions().position(city.location).title(city.toString()));
            latLngBuilder.include(city.location);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 15));
    }
}
