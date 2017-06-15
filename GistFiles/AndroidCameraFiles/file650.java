package com.chitchat.android.chitchat.views;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chitchat.android.chitchat.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rahul Chandra on 12/15/15.
 */
public class MapFragment extends Fragment
    implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final double
            PES_LAT = 12.934131,
            PES_LNG = 77.536756;

    private GoogleApiClient mLocationClient;
    private LocationListener mLocationListner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        if (servicesOK()) {
            if (initMap()) {
                gotoLocation(PES_LAT, PES_LNG, 15);
                // Enabling my current location button
                // mMap.setMyLocationEnabled(true);

                mLocationClient = new GoogleApiClient.Builder(getActivity())
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                mLocationClient.connect();

                return rootView;
            } else {
                Toast.makeText(getActivity(), "Map not connected!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(container.getContext(), "Services not ok!", Toast.LENGTH_SHORT).show();
            //setContentView(R.layout.activity_main);
        }
        return null;
    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, getActivity(), ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            mMap = mapFragment.getMap();
        }
        return (mMap != null);
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(update);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getActivity(), "Ready to Map!", Toast.LENGTH_SHORT).show();

        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getActivity(), "Location changed: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                gotoLocation(location.getLatitude(), location.getLongitude(), 15);
            }
        };

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(60000);
        request.setFastestInterval(30000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, request, mLocationListner);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, mLocationListner);
    }
}
