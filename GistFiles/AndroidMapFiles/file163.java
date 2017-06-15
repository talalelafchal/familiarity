package com.example.bongtrop.maps;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Pongsakorn Sommalai on 5/2/2558.
 * Detail use to get location from map
 */
public class setLocationStudentFragment extends SupportMapFragment {

    private GoogleMap map;
    private Context context;
    private Marker marker;


    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup arg1,
                             Bundle arg2) {
        View view = super.onCreateView(mInflater, arg1, arg2);

        context = getActivity();
        map = getMap();

        LocationManager service = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = service.getLastKnownLocation(provider);
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(userLocation);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        map.moveCamera(center);
        map.animateCamera(zoom);

        marker = map.addMarker(new MarkerOptions().position(userLocation).title("Hold screen to select here"));
        marker.showInfoWindow();



        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                marker.setPosition(cameraPosition.target);
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker.showInfoWindow();
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                LatLng loc = map.getCameraPosition().target;
                Toast.makeText(context,loc.toString(), Toast.LENGTH_LONG).show();
                //bar bar bar
            }
        });

        return view;
    }
}
