package org.example.maps.mapsexample;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.example.maps.mapsexample.bridge.RequestData;
import org.example.maps.mapsexample.model.Example;
import org.example.maps.mapsexample.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends BaseActivity implements
        IExample,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraChangeListener,
        GoogleApiClient.ConnectionCallbacks {

    public static HashMap<Marker, Result> markers = new HashMap<>();
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private boolean markerClicked_dontCallApi;
    private ProgressBar progressBar;
    private List<Float> lats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setPadding(50, 50, 50, 50);
        map.setOnMarkerClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnInfoWindowClickListener(this);
        //map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Result result = markers.get(marker);

                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                if (result == null) {
                    return view;
                }
                if (result.name != null)
                    TextView.class.cast(view.findViewById(R.id.tv1)).setText(result.name);
                if (result.vicinity != null)
                    TextView.class.cast(view.findViewById(R.id.tv2)).setText(result.vicinity);
                if (result.types != null && result.types.size() > 0)
                    TextView.class.cast(view.findViewById(R.id.tv3)).setText(result.types.get(0));
                return view;
            }
        });

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation == null) {
            //todo listen for location changes
            lastLocation = new Location("");
            lastLocation.setLatitude(-23.550157);
            lastLocation.setLongitude(-46.633922);
        }

        LatLng pos = Utils.getLatLng(lastLocation);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //todo handle connection suspended
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //todo handle connection failed
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, StreetViewActivity.class);
        intent.putExtra("position", marker.getPosition());
        startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        marker.showInfoWindow();

        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final LatLng markerLatLng = marker.getPosition();

        markerClicked_dontCallApi = true;

        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(markerLatLng);

        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });

        return false; //have not consumed the event
        //return true; //have consumed the event
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if (markerClicked_dontCallApi) {
            markerClicked_dontCallApi = false;
            return;
        }

        if (cameraPosition.target.latitude == 0 && cameraPosition.target.longitude == 0) {
            return;
        }

        int dist = (int) SphericalUtil.computeDistanceBetween(map.getProjection().getVisibleRegion().nearLeft, map.getProjection().getVisibleRegion().nearRight);

        //map.clear();

        progressBar.setVisibility(View.VISIBLE);
        RequestData.getResults(this, cameraPosition.target, dist);
    }

    @Override
    public void onDataSuccess(Example example) {
        progressBar.setVisibility(View.GONE);
        //markers.clear();

        if (example.results.size() == 0) {
            toastThis("Nenhum item encontrado");
            return;
        }

        for (Result result : example.results) {

            if(lats.contains(result.geometry.location.lat))
                continue;

            lats.add(result.geometry.location.lat);

            LatLng latLng = Utils.getLatLng(result.geometry);

            final Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(0, 0))
                            //.position(latLng)
                    .title(result.name)
                    .snippet(result.vicinity));

            markers.put(marker, result);

            dropIt(marker, latLng);

        }
    }

    @Override
    public void onDataError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        toastThis(errorMessage);
    }

    public void dropIt(final Marker marker, final LatLng target) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(target);
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / 400);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });

    }
}
