package com.shubhobrata.roy.nasaspaceapps;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LandslideViewerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landslide_viewer);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        database=new DBHelper(LandslideViewerActivity.this,"placeCordinate");
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng dhaka = new LatLng(23.777176, 90.399452);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhaka,6.80f));

        Cursor data= database.getAllData();
        data.moveToFirst();
        double la;
        double lo;
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.person_drawable);
        while(data.isAfterLast()==false){
            la=Double.parseDouble(data.getString(data.getColumnIndex("lat")));
            lo=Double.parseDouble(data.getString(data.getColumnIndex("long")));
            mMap.addMarker(new MarkerOptions().position(new LatLng(la,lo))
                    .title("Submitted by user")
                    .icon(icon)

            );
            data.moveToNext();
        }

        new PointLandSlides().execute();
    }



    private class PointLandSlides extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(LandslideViewerActivity.this, "Contacting to server", Toast.LENGTH_LONG).show();

        }


        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("http://abdalimran.pythonanywhere.com/bd-landslides-data");
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray cities = jsonObj.getJSONArray("cities");
                    JSONArray latitudes=jsonObj.getJSONArray("latitude");
                    JSONArray longitudes=jsonObj.getJSONArray("longitude");


                    for (int i = 0; i < cities.length(); i++) {
                        final String city = cities.getString(i);
                        final Float longitude= Float.parseFloat(longitudes.getString(i));
                        final Float latitude= Float.parseFloat(latitudes.getString(i));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLng location = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(location).title(city));
                            }
                        });

                    }

                } catch (final JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    DBHelper database;
}
