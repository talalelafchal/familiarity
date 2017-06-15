package edu.liu.locationexample;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Method;
import java.util.Date;

public class MyActivity extends Activity {

    MockLocationProvider mock;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LocationManager locationManager = (LocationManager)this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);



        mock = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);
        mock.pushLocation(25.780107,-80.234871);

        Button btnNewYork = (Button)findViewById(R.id.btnNewYork);
        btnNewYork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mock.pushLocation(40.75662,-73.982148);
            }
        });

        Button btnFlorida = (Button)findViewById(R.id.btnFlorida);
        btnFlorida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mock.pushLocation(25.780107,-80.234871);
            }
        });

        Button btnCalifornia = (Button)findViewById(R.id.btnCalifornia);
        btnCalifornia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mock.pushLocation(37.76203,-122.422371);
            }
        });

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("myapp", location.toString());


                MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
                GoogleMap map = mapFragment.getMap();
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                LatLng centerLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(centerLatLng, 10);
                map.moveCamera(cameraUpdate);
                map.setMyLocationEnabled(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.v("myapp", "status = %d");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.v("myapp","provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.v("myapp","provider disabled");
            }
        });
    }

    protected void onDestroy() {
        mock.shutdown();
        super.onDestroy();
    }


}
