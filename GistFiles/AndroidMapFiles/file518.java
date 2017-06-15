package com.teicm.fiveandone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);



        @Override
        public void onConnectionSuspended(int i) {
            if (i == CAUSE_SERVICE_DISCONNECTED) {
                Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
            } else if (i == CAUSE_NETWORK_LOST) {
                Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
            }
        }



        public void onLocationChanged(Location location) {


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //update userLocation
            userLocation = latLng;


            LatLng nearbyEntity = new NearbySearch(userLocation).findEntity();

            if (nearbyEntity != null){
                //display marker
                setMarker(nearbyEntity.latitude, nearbyEntity.longitude, "name of obj/npc");
            }
        }




    }
