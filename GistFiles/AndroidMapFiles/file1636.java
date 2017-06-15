package com.doppler.stackingcoder.pechhulp;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PechhulpActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int REQUEST_PERMISSION = PackageManager.GET_PERMISSIONS;

    // Variabelen globaal declareren
    private GoogleMap mMap;
    public GoogleApiClient client;

    LocationManager locationManager;
    LatLng userPosition;
    String provider;

    double latitude;
    double longtitude;

    String street;
    String postalcode;
    String city;
    String country;

    Location userLocation;

    Intent callIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pechhulp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("RSR Pechhulp");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menu_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

    }


    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }


    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            userLocation = locationManager.getLastKnownLocation(provider);

            if (userLocation == null) {
                continue;
            }

            if (bestLocation == null || userLocation.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = userLocation;
            }
        }
        return bestLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        // Geef de waarde die de methode getLastKnownLocation() teruggeeft
        // mee aan de methode onLocationChanged() als parameter location.
        getLastKnownLocation();
        userPosition = new LatLng(latitude, longtitude);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView popupTitle = (TextView) v.findViewById(R.id.popupTitle);
                TextView streetTextView = (TextView) v.findViewById(R.id.streetTextView);
                TextView cityTextView = (TextView) v.findViewById(R.id.cityTextView);
                TextView hintTextview = (TextView) v.findViewById(R.id.hintTextView);
                popupTitle.setText("Uw Locatie:");
                streetTextView.setText(street + ", " + postalcode);
                cityTextView.setText(city + ", " + country);
                hintTextview.setText("Onthoud deze locatie voor het \n telefoongesprek");
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

        });

        MarkerOptions markerOptions = new MarkerOptions().position(userPosition)
                .title("Uw Locatie:")
                .snippet(street + ", " + city + " " + country)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_mini));

        markerOptions.position(userPosition);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(userPosition));
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));

    }


    // Elke keer dat de locatie veranderd word deze functie aangeroepen.
    // Wanneer de gebruiker zich op een nieuwe locatie bevind word de map leeggemaakt
    // van bestaande markers en word er een nieuwe marker neergezet.
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longtitude = location.getLongitude();


        // Verwijder alle objecten van de map. (Bestaande markers etc.)
        // Zet de marker opnieuw neer voor nieuwe locatie
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> licitydresses = geocoder.getFromLocation(latitude, longtitude, 1);
            if (licitydresses != null && licitydresses.size() > 0) {
                street = licitydresses.get(0).getAddressLine(0);
                postalcode = licitydresses.get(0).getPostalCode();
                city = licitydresses.get(0).getLocality();
                country = licitydresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void callRsr(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        CustomDialogClass popup = new CustomDialogClass(PechhulpActivity.this);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:09003344556"));
        if (PechhulpActivity.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }





}