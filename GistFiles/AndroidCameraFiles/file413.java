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
import android.os.Handler;
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
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
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
    private Location lastLocation;
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
            case R.id.action_center_user_location:


                if (map != null) {

                    if (!map.isMyLocationEnabled()) {
                        enableLocation(true);
                    }
                    if (lastLocation != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
                    } else {
                        Log.w(TAG, "lastLocation null: ");
                    }

                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;

        //map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setLogoGravity(Gravity.BOTTOM | Gravity.END);
        map.getUiSettings().setLogoEnabled(true);
        map.getUiSettings().setAttributionEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);

        //new DrawGeoJson().execute();

        // Customize the user location icon using the getMyLocationViewSettings object.
        map.getMyLocationViewSettings().setPadding(0, 500, 0, 0);
        map.getMyLocationViewSettings().setForegroundTintColor(Color.parseColor("#56B881"));
        map.getMyLocationViewSettings().setAccuracyTintColor(Color.parseColor("#FBB03B"));


        CameraPosition cameraPosition = map.getCameraPosition();
        scaleView.update((float) cameraPosition.zoom, cameraPosition.target.getLatitude());

        mapboxMap.setOnCameraChangeListener(this);

        new DrawGeoJson2().execute();

/*
        TileSet ICC = new TileSet("tileset", "http://geoserveis.icc.cat/icc_mapesbase/wms/service?REQUEST=GetMap&VERSION=1.1.0&SERVICE=WMS&SRS=EPSG:23031&BBOX=290368.84,4543236.42,292203.28,4545070.86&WIDTH=520&HEIGHT=520&LAYERS=mtc50m&STYLES=&FORMAT=JPEG&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&EXCEPTION=INIMAGE");

        RasterSource webMapSource = new RasterSource(
                "web-map-source",ICC,520);

        mapboxMap.addSource(webMapSource);

        // Add the web map source to the map.
        RasterLayer webMapLayer = new RasterLayer("web-map-layer", "web-map-source");
        mapboxMap.addLayer(webMapLayer);
*/


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
            if (locationEngine.getLastLocation() != null) {
                lastLocation = locationEngine.getLastLocation();
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
                        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
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

    /*
    https://medium.com/nextome/show-a-geojson-layer-on-google-maps-osm-mapbox-on-android-cd75b8377ba
     */

    private class DrawGeoJson extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {

            StringBuilder sb = new StringBuilder();

            try {
                // Load GeoJSON file
                InputStream inputStream = getAssets().open("sample.geojson");
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                inputStream.close();

            } catch (Exception exception) {
                Log.e(TAG, "Exception Loading GeoJSON: " + exception.toString());
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String sb) {
            super.onPostExecute(sb);
            Log.i(TAG, "onPostExecute: ");

            GeoJsonSource source = new GeoJsonSource("geojson", sb);
            map.addSource(source);

            //mapboxMap.addLayer(new LineLayer("geojson", "geojson"));

            LineLayer lineLayer = new LineLayer("linelayer", "geojson");

            // The layer properties for our line. This is where we make the line dotted, set the
            // color, etc.
            lineLayer.setProperties(
/*                    PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),


                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
*/
                    PropertyFactory.lineWidth(2f),
                    PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
            );

            map.addLayer(lineLayer);


            FeatureCollection featureCollection = FeatureCollection.fromJson(sb);
            List<Feature> features = featureCollection.getFeatures();

            IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
            //Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.mapbox_compass_icon);
            //Icon icon = iconFactory.fromDrawable(iconDrawable);
            Icon iconStart = iconFactory.fromBitmap(getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_flag_green_24dp));
            Icon icon = iconFactory.fromBitmap(getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_place_black_24dp));
            Icon iconEnd = iconFactory.fromBitmap(getBitmapFromVectorDrawable(MainActivity.this, R.drawable.ic_flag_red_24dp));


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

    private class DrawGeoJson2 extends AsyncTask<Void, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(Void... voids) {

            ArrayList<LatLng> points = new ArrayList<>();

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

                // Parse JSON
                JSONObject json = new JSONObject(sb.toString());
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

            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            super.onPostExecute(points);

            if (points.size() > 0) {
                Log.d(TAG, "onPostExecute: ");
                // Draw polyline on map
                map.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .color(Color.parseColor("#3bb2d0"))
                        .width(2));


                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng marker : points) {
                    builder.include(marker);
                }
                LatLngBounds bounds = builder.build();

                Log.d(TAG, "onPostExecute: " + bounds);
                int padding = 30;// offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                CameraPosition newPosition = new CameraPosition.Builder()
                        .target(bounds.getCenter()) // Sets the new camera position
                        .build(); // Creates a CameraPosition from the builder

                final CameraUpdate center = CameraUpdateFactory.newCameraPosition(newPosition);

                map.easeCamera(cu, 2000);

/*
                map.animateCamera(center, new MapboxMap.CancelableCallback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onFinish() {

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                map.moveCamera(center);
                            }
                        }, 100);
                    }
                });
*/


/*
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(bounds.getLatNorth(),bounds.getLonWest())) // Sets the new camera position
                        .build(); // Creates a CameraPosition from the builder


                map.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 2000);
*/

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
}
