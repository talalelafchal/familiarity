package com.insset.wekaru.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.insset.wekaru.R;
import com.insset.wekaru.classes.API;
import com.insset.wekaru.classes.Branch;
import com.insset.wekaru.classes.Globals;
import com.insset.wekaru.classes.Line;
import com.insset.wekaru.classes.Station;

import java.util.ArrayList;
import java.util.List;

public class MapResults extends FragmentActivity {


    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public List<Polyline> mPolylines = new ArrayList<Polyline>();
    public String keyword;
    public ArrayList<Line> mesLinesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        keyword = getIntent().getStringExtra("keyword");
        mesLinesArray = Globals.mesLinesArray;

        Log.i("keyword", keyword);

        setContentView(R.layout.activity_map_results);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Double lng = extras.getDouble("maLongitude");
            Double lat = extras.getDouble("maLatitude");




            setUpMapIfNeeded(lat,lng);

            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }


        LatLng maPos = new LatLng(48.872240, 2.382358);
        Marker m = mMap.addMarker(new MarkerOptions()
                        .position(maPos)
                        .title("Wekaru")
                        .snippet("Ma position")
        );

        MapResults.this.dessinerLignes(mesLinesArray);
        //spinner.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded(Double maLatitude, Double maLongitude) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            LatLng latlng = new LatLng(48.872240, 2.382358);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        }
    }



























    private void dessinerLignes(ArrayList<Line> result){

        ArrayList<Line> mesLinesArray = result;
        for (int i =0;i<mesLinesArray.size();i++) {
            final Line ligneParcourue = mesLinesArray.get(i);

            Log.i("Ligne: : ", ligneParcourue.getName());

            for (int j=0;j<ligneParcourue.branches.size();j++){
                Branch branchParcourue = ligneParcourue.branches.get(j);
                Log.i("Branche parcourue : ", branchParcourue.getName());

                for (int y = 0; y<branchParcourue.stations.size(); y++){
                    Log.i("Stations: ", branchParcourue.stations.get(y).toString());
                    Log.i("Nombre de stations",Integer.toString(branchParcourue.stations.size()));
                    if(y<branchParcourue.stations.size()-1) {

                        this.mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(branchParcourue.stations.get(y).getLatitude(), branchParcourue.stations.get(y).getLongitude()), new LatLng(branchParcourue.stations.get(y + 1).getLatitude(), branchParcourue.stations.get(y + 1).getLongitude()))
                                .width(10)
                                .color(Color.parseColor(ligneParcourue.getColor())));
                    }


                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(branchParcourue.stations.get(y).getLatitude(), branchParcourue.stations.get(y).getLongitude()))
                            .radius(30)
                            .strokeColor(Color.parseColor(ligneParcourue.getColor()))
                            .fillColor(Color.parseColor(ligneParcourue.getColor())));


                    LatLng laPos = new LatLng(branchParcourue.stations.get(y).getLatitude(), branchParcourue.stations.get(y).getLongitude());
                     mMap.addMarker(new MarkerOptions()
                                     .position(laPos)
                                     .title(ligneParcourue.getName())
                                     .snippet(ligneParcourue.getColor())
                                     .alpha(0)
                     );







                    Paint paintText = new Paint();
                    paintText.setTextSize(20);
                    paintText.setColor(Color.parseColor(ligneParcourue.getColor()));
                    String strText = branchParcourue.stations.get(y).getName();

                    LatLng latlngMarker = new LatLng(branchParcourue.stations.get(y).getLatitude(), branchParcourue.stations.get(y).getLongitude()+0.002);

                    Rect boundsText = new Rect();
                    paintText.getTextBounds(strText, 0, strText.length(), boundsText);
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                    Bitmap bmpText = Bitmap.createBitmap(boundsText.width()+1000,
                            boundsText.height(), conf);

                    Canvas canvasText = new Canvas(bmpText);
                    canvasText.drawText(strText, canvasText.getWidth() / 2, canvasText.getHeight(), paintText);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latlngMarker)
                            .icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                            .anchor(0.5f, 1);

                    mMap.addMarker(markerOptions);









                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                    {
                        @Override
                        public boolean onMarkerClick(Marker arg0) {
                            if(arg0.getTitle() != null){

                                //Toast.makeText(MapResults.this, arg0.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                            }

                            Log.i("category", getIntent().getStringExtra("keyword"));

                            Intent mainIntent = new Intent(MapResults.this, ResultsSearch.class);
                            mainIntent.putExtra("lineId", arg0.getTitle());
                            mainIntent.putExtra("nameLine", ligneParcourue.getName());
                            mainIntent.putExtra("codeColor", arg0.getSnippet());
                            mainIntent.putExtra("keyword", getIntent().getStringExtra("keyword"));
                            MapResults.this.startActivity(mainIntent);
                            MapResults.this.finish();

                            return true;
                        }

                    });

                }

            }





        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickCoords) {
                for (Polyline polyline : mPolylines) {
                    for (LatLng polyCoords : polyline.getPoints()) {
                        float[] results = new float[1];
                        Location.distanceBetween(clickCoords.latitude, clickCoords.longitude,
                                polyCoords.latitude, polyCoords.longitude, results);

                        if (results[0] < 100) {
                            //polyline.setVisible(false);
                            Log.e("TAG", "Found @ " + clickCoords.latitude + " " + clickCoords.longitude + " " + results[0]);
                            return;
                        }
                    }
                }
            }
        });
    }
}