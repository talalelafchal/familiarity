package com.example;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity {

    private MapView mapView;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            MapsInitializer.initialize(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {

        super.onResume();
        mapView.onResume();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (map == null) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                TextView tvNoInternet = new TextView(this);
                tvNoInternet.setGravity(Gravity.CENTER_HORIZONTAL);
                tvNoInternet.setText(getString(R.string.no_net_info));
                ((MapView) findViewById(R.id.map)).addView(tvNoInternet);
            }

            map = ((MapView) findViewById(R.id.map)).getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        double lat = Double.parseDouble(0));
        double lon = Double.parseDouble(0));
        LatLng coords = new LatLng(lon, lat);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 10));
        map.addMarker(new MarkerOptions().position(coords));
    }

    @Override
    protected void onPause() {

        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}