package edu.colby.ontrack.ontrack;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.parse.ParseRelation;

public class LeaderMapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button onExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_map);
        setUpMapIfNeeded();
        onExit = (Button) findViewById(R.id.button_exit);
        onExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitSession();




            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Location location = getLocation();
                setUpMap(location);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    public void setUpMap(Location loc ) {

//        AppStatus as = new AppStatus(this);
//        Location loc = getLocation();
        addGPS(loc);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(loc.getLatitude(), loc.getLongitude()))
                .zoom(18)
                .bearing(loc.getBearing())
                .tilt(70)
                .build();

        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);

        mMap.animateCamera(camUpd3);

        mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("Leader").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_lead_sheep)));
    }

//    public void addGPS(Location loc){
//        ParseObject currentUser = LaunchActivity.getUser();
//
//        ParseObject gps = new ParseObject("GPSObject");
//        gps.put("Latitude", loc.getLatitude());
//        gps.put("Longitude", loc.getLongitude());
//        gps.saveInBackground();
//        ParseRelation relation = currentUser.getRelation("GPSObject");
//        relation.add(gps);
//        currentUser.saveInBackground();
//
//    }


    public Location getLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                LeaderMapActivity lma = new LeaderMapActivity();
//                lma.setUpMap();
                addGPS(location);
                mMap.clear();
                setUpMap(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        String locationProvider1 = LocationManager.NETWORK_PROVIDER;
        String locationProvider2 = LocationManager.GPS_PROVIDER;

        Location l1 = locationManager.getLastKnownLocation(locationProvider1);
        Location l2 = locationManager.getLastKnownLocation(locationProvider2);

        if(l2 != null){//GPS
            return l1;
        }else if(l1 != null){//Network
            return l1;
        }else{ //return null
            return null;
        }
    }

    public void addGPS(Location loc){
        ParseObject currentUser = LaunchActivity.getUser();

        ParseObject gps = new ParseObject("GPSObject");
        gps.put("Latitude", loc.getLatitude());
        gps.put("Longitude", loc.getLongitude());
        gps.put("user", currentUser);
        Log.d("Logitude", "Long: " + loc.getLongitude() + " Lat: " + loc.getLatitude());
        gps.saveInBackground();

        currentUser.put("GPSObject", gps);

//        ParseRelation relation = currentUser.getRelation("GPSObject");
//        relation.add(gps);
        currentUser.saveInBackground();
    }

    public void exitSession(){
        ParseObject session = LeadActivity.getmSession();
        session.put("Active", false);
        Intent i = new Intent(LeaderMapActivity.this, LaunchActivity.class);
        startActivity(i);

    }

    @Override
    protected void onPause() {
        super.onPause();
        new CountDownTimer(10100, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                exitSession();
//                mTextField.setText("done!");
            }
        }.start();


    }

    @Override
    public void onBackPressed() {
        exitSession();
    }