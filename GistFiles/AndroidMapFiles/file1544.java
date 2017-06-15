package com.panorma.views.fragments.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLndg;
import com.google.android.gms.maps.model.MarkerOptions;
import com.panorma.R;

/**
 * Created by aateek on 3/10/2016.
 */
// In this case, the fragment displays simple text based on the page
public class MapViewPagerFragment extends Fragment {

    private static View rootView;
    private final double LATITUDE = *********;
    private final double LONGITUDE = ********;

    GoogleMap googleMap;
    private SupportMapFragment mapFrag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
            Log.d("InflateException", "onCreateView: ");
        }
        initMap();

        return rootView;
    }


    private void initMap() {

        LatLng location = new LatLng(LATITUDE, LONGITUDE);
        FragmentManager fm = getChildFragmentManager();
        mapFrag = (SupportMapFragment) fm.findFragmentById(
                R.id.map);
        if (mapFrag != null) {
            googleMap = mapFrag.getMap();
        }

        if (googleMap != null) {
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.addMarker(new MarkerOptions().position(location));
            MapsInitializer.initialize(this.getActivity());
            // Updates the location and zoom of the MapView
            CameraUpdate initalUpdate = CameraUpdateFactory.newLatLngZoom(
                    location, 17.0f);

            googleMap.animateCamera(initalUpdate);
        }

    }
}