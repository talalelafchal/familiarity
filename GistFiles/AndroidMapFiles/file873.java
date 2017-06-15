package net.beshkenadze.charity.ui.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import net.beshkenadze.charity.R;

/**
 * Created by Aleksandr Beshkenadze <beshkenadze@gmail.com> on 24.08.13.
 */
public class MapTabFragment extends BaseTabFragment {
    private static View rootView;
    SupportMapFragment mapView;
    GoogleMap map;

    public MapTabFragment(String title) {
        super(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        mapView = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        map = mapView.getMap();

        // Gets the MapView from the XML layout and creates it
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        if (map != null) {
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(true);
            map.addMarker(new MarkerOptions().position(new LatLng(50.167003, 19.383262)));

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            try {
                MapsInitializer.initialize(this.getActivity());
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
            map.animateCamera(cameraUpdate);

        }

        return rootView;
    }
}
