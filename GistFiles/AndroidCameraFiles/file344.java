package com.doppler.stackingcoder.pechhulp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class PechhulpActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int REQUEST_PERMISSION = PackageManager.GET_PERMISSIONS;


    // Variabelen globaal declareren
    private GoogleMap mMap;
    public GoogleApiClient client;
    Geocoder geocoder;

    LocationManager locationManager;

    LatLng userPosition;
    String provider;

    double latitude, longtitude;

    String street, postalcode, city, country;
    Location userLocation;

    Context mContext;

    Location bestLocation;
    List<String> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pechhulp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("RSR Pechhulp");
        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.menu_arrow, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        mContext = getApplicationContext();
        bestLocation = null;

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        providers = locationManager.getProviders(true);


    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        client.connect();
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(PechhulpActivity.this, "Het is nodig dat u toestemming voor locatie inschakeld", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        }

    }

    @Override
    public void onStop() {
        client.disconnect();
        super.onStop();
    }





    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location l = locationManager.getLastKnownLocation(provider);



            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }

        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    // Elke keer dat de locatie veranderd word deze functie aangeroepen.
    // Wanneer de gebruiker zich op een nieuwe locatie bevind word de map leeggemaakt
    // van bestaande markers en word er een nieuwe marker neergezet.
    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (location != null) {
            latitude   = location.getLatitude();
            longtitude = location.getLongitude();



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

        } else {
            Toast.makeText(this, "Geen gegevens over locatie", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        // Geef de waarde die de methode getLastKnownLocation() teruggeeft
        // mee aan de methode onLocationChanged() als parameter location.
        onLocationChanged(getLastKnownLocation());


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
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

        System.out.println(bestLocation + "<===========================================");

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CustomDialogClass popup = new CustomDialogClass(PechhulpActivity.this);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();
            } else {
                Toast.makeText(this, "Toestemming voor het bellen is niet verleend", Toast.LENGTH_SHORT).show();
            }


        } else {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    public void callRsr(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            CustomDialogClass popup = new CustomDialogClass(PechhulpActivity.this);
            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popup.show();
        } else {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(PechhulpActivity.this, "Het is nodig dat u toestemming voor het bellen inschakeld", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION);
        }
    }



}