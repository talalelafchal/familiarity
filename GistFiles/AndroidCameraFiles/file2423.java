package com.webserveis.app.kmlviewer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.pengrad.mapscaleview.MapScaleView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.commons.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*
https://stackoverflow.com/questions/36575447/how-to-use-fitbounds-on-mapbox-android-sdk

 */
public class MainActivity extends AppCompatActivity
        implements PermissionsListener, OnMapReadyCallback, MapboxMap.OnCameraChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MapView mapView;
    private MapboxMap map;
    private FloatingActionButton floatingActionButton;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;
    private MapScaleView scaleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the location engine object for later use.
        locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();

        mapView = (MapView) findViewById(R.id.mapview);
        scaleView = (MapScaleView) findViewById(R.id.scaleView);

        mapView.onCreate(savedInstanceState);
        //mapView.setStyleUrl(Style.OUTDOORS);

        //mapView.setStyleUrl(Style.SATELLITE_STREETS);

        mapView.getMapAsync(this);


        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_mapstyle_normal:
                if (mapView != null) mapView.setStyleUrl(Style.MAPBOX_STREETS);
                item.setChecked(true);
                return true;
            case R.id.action_mapstyle_outdoor:
                if (mapView != null) mapView.setStyleUrl(Style.OUTDOORS);
                item.setChecked(true);
                return true;
            case R.id.action_mapstyle_satelite:
                if (mapView != null) mapView.setStyleUrl(Style.SATELLITE);
                item.setChecked(true);
                return true;
            case R.id.action_mapstyle_hybrid:
                if (mapView != null) mapView.setStyleUrl(Style.SATELLITE_STREETS);
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        prepareMap();

    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        // Ensure no memory leak occurs if we register the location listener but the call hasn't
        // been made yet.
        if (locationEngineListener != null) {
            locationEngine.removeLocationEngineListener(locationEngineListener);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation(true);
        } else {
            Toast.makeText(this, "You didn't grant location permissions.",
                    Toast.LENGTH_LONG).show();
            //finish();
        }
    }


    @Override
    public void onCameraChange(CameraPosition position) {
        scaleView.update((float) position.zoom, position.target.getLatitude());
        //Log.d(TAG, "onCameraChange: " + position.target.getLatitude() + "," + position.target.getLongitude());


        Log.d(TAG, "onCameraChange: " + map.getProjection().getVisibleRegion().latLngBounds.getCenter());
    }

    private void prepareMap() {
        runOnUiThread(new Runnable() {
            public void run() {
                //map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setRotateGesturesEnabled(false);
                map.getUiSettings().setLogoGravity(Gravity.BOTTOM | Gravity.END);
                map.getUiSettings().setLogoEnabled(true);
                map.getUiSettings().setAttributionEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(true);

                // Customize the user location icon using the getMyLocationViewSettings object.
                map.getMyLocationViewSettings().setPadding(0, 500, 0, 0);
                map.getMyLocationViewSettings().setForegroundTintColor(Color.parseColor("#56B881"));
                map.getMyLocationViewSettings().setAccuracyTintColor(Color.parseColor("#FBB03B"));

                //set scaleview hud
                CameraPosition cameraPosition = map.getCameraPosition();
                scaleView.update((float) cameraPosition.zoom, cameraPosition.target.getLatitude());
                map.setOnCameraChangeListener(MainActivity.this);

                //Load gson and markers
                new drawGeoJson().execute();

                map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        Log.d(TAG, "Map clicked [" + point.getLatitude() + "," + point.getLongitude() + "]" + point.getAltitude());
                    }
                });
            }
        });
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                permissionsManager.requestLocationPermissions(this);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsManager.requestLocationPermissions(this);
        } else if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            Log.d(TAG, "enableLocation: GRANTED!");
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
            }

            locationEngineListener = new LocationEngineListener() {
                @Override
                public void onConnected() {
                    // No action needed here.
                }

                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged: " + location);
                    if (location != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                        String speed = String.valueOf(location.getSpeed());
                        Toast.makeText(MainActivity.this, speed, Toast.LENGTH_SHORT).show();
                        //map.easeCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16),2000);
                        locationEngine.removeLocationEngineListener(this);
                        Log.i(TAG, "removeLocationEngineListener: ");
                    }
                }
            };
            Log.i(TAG, "addLocationEngineListener: ");
            locationEngine.addLocationEngineListener(locationEngineListener);
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_black_24dp);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_black_24dp);
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);


    }

    private class drawGeoJson extends AsyncTask<Void, Void, Boolean> {

        private String jsonString;
        private List<LatLng> points;
        private LatLngBounds bounds;

        @Override
        protected Boolean doInBackground(Void... voids) {

            points = new ArrayList<>();

            try {
                // Load GeoJSON file
                InputStream inputStream = getAssets().open("sample.geojson");
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }

                inputStream.close();

                jsonString = sb.toString();

                // Parse JSON
                JSONObject json = new JSONObject(jsonString);
                JSONArray features = json.getJSONArray("features");
                JSONObject feature = features.getJSONObject(0);

                JSONObject geometry = feature.getJSONObject("geometry");
                if (geometry != null) {
                    String type = geometry.getString("type");

                    Log.d(TAG, "type: " + type);
                    // Our GeoJSON only has one feature: a line string
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")) {

                        // Get the Coordinates
                        JSONArray coords = geometry.getJSONArray("coordinates");
                        for (int lc = 0; lc < coords.length(); lc++) {
                            JSONArray coord = coords.getJSONArray(lc);
                            LatLng latLng = new LatLng(coord.getDouble(1), coord.getDouble(0));
                            points.add(latLng);
                        }
                    }
                }
            } catch (Exception exception) {
                Log.e(TAG, "Exception Loading GeoJSON: " + exception.toString());
            }

            //calculate bounds
            if (points.size() > 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng marker : points) {
                    builder.include(marker);
                }
                bounds = builder.build();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Log.d(TAG, "onPostExecute: ");
                // Draw polyline on map
                drawPolyLine(points, 2, Color.parseColor("#3bb2d0"));
                //drawAllMarks(jsonString);

                //drawMark(points.get(points.size()-1),"end",null,R.drawable.ic_flag_red_24dp);
                //drawMark(points.get(0),"start",null,R.drawable.ic_flag_green_24dp);

                drawMark(bounds.getCenter(), "start", null, R.drawable.ic_place_blue_24dp);
                drawMark(new LatLng(bounds.getLatNorth(), bounds.getLonWest()), "start", null, R.drawable.ic_place_blue_24dp);
                drawMark(new LatLng(bounds.getLatNorth(), bounds.getLonEast()), "start", null, R.drawable.ic_place_blue_24dp);
                drawMark(new LatLng(bounds.getLatSouth(), bounds.getLonWest()), "start", null, R.drawable.ic_place_blue_24dp);
                drawMark(new LatLng(bounds.getLatSouth(), bounds.getLonEast()), "start", null, R.drawable.ic_place_blue_24dp);
                map.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16), 2000);

            }
        }

    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void drawPolyLine(List<LatLng> points, int lineWidth, int color) {
        if (lineWidth <= 0) lineWidth = 1;
        map.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(color)
                .width(lineWidth));
    }

    private void drawMark(@NonNull LatLng coordinates, String title, String description, @DrawableRes int drawableResource) {
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon icon = iconFactory.fromBitmap(getBitmapFromVectorDrawable(MainActivity.this, drawableResource));
        map.addMarker(new MarkerViewOptions()
                .position(coordinates)
                .title(title)
                .snippet(description)
                .icon(icon)
        );


    }

    private void drawAllMarks(String sb) {
        FeatureCollection featureCollection = FeatureCollection.fromJson(sb);
        List<Feature> features = featureCollection.getFeatures();

        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        //Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.mapbox_compass_icon);
        //Icon icon = iconFactory.fromDrawable(iconDrawable);
        Icon icon = iconFactory.fromBitmap(getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_place_blue_24dp));

        for (Feature f : features) {
            if (f.getGeometry() instanceof Point) { //Point
                Position coordinates = (Position) f.getGeometry().getCoordinates();
                map.addMarker(new MarkerViewOptions()
                        .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                        .title(f.getStringProperty("Name"))
                        .snippet(f.getStringProperty("description"))
                        .icon(icon)
                );

                //Log.d(TAG, "onPostExecute: " + f.getProperties().toString());
            }
        }
    }
}
