package com.example.zeon.muslimplace.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.zeon.muslimplace.dao.place.PlaceCollection;
import com.example.zeon.muslimplace.dao.place.PlaceItem;
import com.example.zeon.muslimplace.dao.savelocation.PlaceLocationCollection;
import com.example.zeon.muslimplace.manager.LocationManager;
import com.example.zeon.muslimplace.manager.NetworkManager;
import com.example.zeon.muslimplace.R;
import com.example.zeon.muslimplace.utils.SessionUtil;
import com.example.zeon.muslimplace.task.ShowPlace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    public static Context context;
    private DrawerLayout mDrawerLayout;
    SessionUtil session;
    FloatingActionButton addPlace;
    private GoogleMap map;
    SupportMapFragment mapFragment;
    List<PlaceItem> places;
    double plat = 0, plong = 0;
    LatLng latLng;
    double place_lat, place_long;
    GoogleApiClient googleApiClient;
    boolean isAnimate = false;
    int ch = 1;
    Toolbar toolbar;
    NavigationView navigationView;
    PlaceItem placeItem;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        initInstances();

        if (NetworkManager.hasAvailable(MainActivity.this))
            new ShowPlace(MainActivity.this).execute();


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        }

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    initMap(googleMap);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(),
                    "Error while Loading map", Toast.LENGTH_SHORT).show();
        }

        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToAddPlace();
            }
        });
    }


    private void initMap(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                place_long = marker.getPosition().longitude;
                place_lat = marker.getPosition().latitude;

                if (!(marker.getPosition().longitude == plong
                        && marker.getPosition().latitude == plat)) {
                    for(int i=0;i<places.size();i++){
                        if(places.get(i).getLatitude() == place_lat &&
                                places.get(i).getLongitude() == place_long) {
                            placeItem = places.get(i);
                            snackbar = Snackbar.make(findViewById(R.id.rootlayout),
                                    "Press Detail to show detail of " + placeItem.getName(),
                                    Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Detail", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    moveToPlaceInfo();
                                }
                            });
                            snackbar.show();
                        }
                    }
                }
                return false;

            }
        });

        restoreData();


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(snackbar != null)
                    if(snackbar.isShown()) snackbar.dismiss();
            }
        });
    }


    private void moveToAddPlace(){
        if (!LocationManager.isLocationAvaliable(MainActivity.this)) {
            LocationManager.showLocationError(MainActivity.this);
        } else if (plat == 0 && plong == 0) {
            Toast.makeText(getApplicationContext(), "กรุณารอสักครู่ แล้วลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
        } else if (!NetworkManager.hasAvailable(MainActivity.this)) {
            NetworkManager.showNetworkError(findViewById(R.id.rootlayout), MainActivity.this);
        } else {
            Intent i = new Intent(MainActivity.this, IntroAddPlaceActivity.class);
            i.putExtra("Lat", plat);
            i.putExtra("Long", plong);
            startActivity(i);
        }
    }

    private void moveToSaveLocation(){
        if(!LocationManager.isLocationAvaliable(MainActivity.this)){
            LocationManager.showLocationError(MainActivity.this);
            return;
        }
        Intent intent = new Intent(MainActivity.this, GoogleMapActivity.class);
        intent.putExtra("location", new LatLng(plat, plong));
        startActivity(intent);
    }

    private void moveToPlaceInfo(){
        Intent intent = new Intent(MainActivity.this, PlaceInfoActivity.class);
        intent.putExtra("dao", placeItem);
        startActivity(intent);
    }

    private void initInstances() {
        session = new SessionUtil(MainActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addPlace = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    mDrawerLayout.closeDrawers();
                    int i = item.getItemId();

                    switch (i) {
                        case R.id.navigation_item_home:
                            break;
                        case R.id.navigation_item_information:
                            if (!NetworkManager.hasAvailable(MainActivity.this)) {
                                NetworkManager.showNetworkError(findViewById(R.id.rootlayout), MainActivity.this);
                            } else if (session.getUsername().equals("guest")) {
                                Toast.makeText(getApplicationContext(), "ไม่สามารถใช้งานในส่วนตรงนี้ได้", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent information = new Intent(MainActivity.this, InformationActivity.class);
                                startActivity(information);
                            }
                            break;
                        case R.id.navigation_item_travel_experts:
                            startActivity(new Intent(MainActivity.this, TravelExpertActivity.class));
                            break;
                        case R.id.navigation_item_logout:
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("คำเตือน");
                            builder.setMessage("ต้องการออกจากระบบ?");
                            builder.setCancelable(false);
                            builder.setNegativeButton("No", null);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    session.logoutUser();
                                    clearSession();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            builder.show();
                            break;
                    }
                    return true;
                }
            };

    private void clearSession() {
        getSharedPreferences("travel_list", Context.MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("place", Context.MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("location", Context.MODE_PRIVATE).edit().clear().apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        switch (i) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.refresh:
                if (!NetworkManager.hasAvailable(MainActivity.this)) {
                    NetworkManager.showNetworkError(findViewById(R.id.rootlayout), this);
                } else {
                    new ShowPlace(MainActivity.this).execute();
                }
                return true;
            case R.id.filter:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.label_show_by);
                builder.setCancelable(true);
                builder.setItems(R.array.place_show_by, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ch = which + 1;
                        addMarkersMap();
                    }
                });
                builder.show();
                return true;
            case R.id.app_report:
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void backToLogin() {
        session.logoutUser();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendData(List<PlaceItem> places){
        this.places = places;
        addMarkersMap();
    }

    private void restoreData() {
        SharedPreferences pref = getSharedPreferences("place", Context.MODE_PRIVATE);
        String json = pref.getString("data", null);
        if(json == null) return;
        Gson gson = new Gson();
        PlaceCollection placeData = gson.fromJson(json, PlaceCollection.class);
        if(placeData != null) sendData(placeData.getResults());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!LocationManager.isLocationAvaliable(MainActivity.this)) {
            LocationManager.showLocationError(MainActivity.this);
        } else if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
        } else {
            googleApiClient.connect();
        }
        navigationView.setCheckedItem(R.id.navigation_item_home);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(15000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        plat = location.getLatitude();
        plong = location.getLongitude();
        if (!isAnimate) {
            map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(plat, plong), 15)));
            isAnimate = true;
        }
        addMarkersMap();
    }

    private void addMarkersMap() {
        latLng = new LatLng(plat, plong);
        map.clear();

        float[] distance = new float[1];
        DecimalFormat fm = new DecimalFormat("#.###");
        if (places != null) {
            if (ch == 1) {
                for (int i = 0; i < places.size(); i++) {
                    LatLng pos = new LatLng(places.get(i).getLatitude(),
                            places.get(i).getLongitude());
                    Location.distanceBetween(plat, plong, places.get(i).getLatitude(),
                            places.get(i).getLongitude(), distance);
                    addMarker(places.get(i).getName(),
                            pos,
                            "ระยะทาง : " + fm.format(distance[0] / 1000) + " km.",
                            places.get(i).getType());
                }
            } else if (ch == 2) {
                for (int i = 0; i < places.size(); i++) {
                    LatLng pos = new LatLng(places.get(i).getLatitude(),
                            places.get(i).getLongitude());
                    Location.distanceBetween(plat, plong, places.get(i).getLatitude(),
                            places.get(i).getLongitude(), distance);
                    if (places.get(i).getType() == 1) {
                        addMarker(places.get(i).getName(),
                                pos,
                                "ระยะทาง : " + fm.format(distance[0] / 1000) + " km.",
                                places.get(i).getType());
                    }
                }
            } else {
                for (int i = 0; i < places.size(); i++) {
                    LatLng pos = new LatLng(places.get(i).getLatitude(),
                            places.get(i).getLongitude());
                    Location.distanceBetween(plat, plong, places.get(i).getLatitude(),
                            places.get(i).getLongitude(), distance);
                    if (places.get(i).getType() == 2) {
                        addMarker(places.get(i).getName(),
                                pos,
                                "ระยะทาง : " + fm.format(distance[0] / 1000) + " km.",
                                places.get(i).getType());
                    }
                }
            }
        }
    }

    private void addMarker(String name, LatLng position, String distance, int type) {
        BitmapDescriptor icon;
        if (type == 1) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mosque);
        } else {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_halal_place);
        }
        map.addMarker(new MarkerOptions()
                .position(position)
                .title(name)
                .snippet(distance)
                .icon(icon));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }
}
