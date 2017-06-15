import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * MapsActivity renders markers on a map and tracks device position. When the device comes close
 * to a marker the marker is removed and score is increased.
 */
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private int score = 0;

    private MarkerOptions myMarker;
    private ArrayList<MarkerOptions> targets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // keep screen on as long as the app is in focus
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Connect to Google API to be able to use the LocationService
        buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        setupMapObjects();

        renderMapObjects();
    }

    // Sets up markers for the device location and the targets.
    private void setupMapObjects(){

        LatLng STARTING_POINT = new LatLng(58.393501, 15.565061);
        myMarker = new MarkerOptions()
                .position(STARTING_POINT)
                .title("Marker in Linköping")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pink_monster));


        targets = new ArrayList<>();
        targets.add(new MarkerOptions()
                        .position(new LatLng(58.393794, 15.564398))
                        .title("Target 1")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pinky))
        );
        targets.add(new MarkerOptions()
                        .position(new LatLng(58.393645, 15.564490))
                        .title("Target 2")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.castiel))
        );
    }

    // Renders markers for the device location and the targets.
    private void renderMapObjects() {
        mMap.clear();
        mMap.addMarker(myMarker);
        for (MarkerOptions target : targets) {
            mMap.addMarker(target);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(), 19));
    }



    @Override
    public void onLocationChanged(Location location) {

        Iterator<MarkerOptions> iter = targets.iterator();
        while (iter.hasNext()) {
            MarkerOptions target = iter.next();
            Location targetLocation = targetToLocation(target);
            float distance = location.distanceTo(targetLocation);
            float CAPTURE_DISTANS_IN_M = 10.0f;
            if (distance < CAPTURE_DISTANS_IN_M) {
                Toast.makeText(this, target.getTitle() + " destroyed!", Toast.LENGTH_SHORT).show();
                iter.remove();
                score++;
            }
        }
        TextView positionTV = (TextView) findViewById(R.id.positionTV);
        String text = "Score: " + score;
        positionTV.setText(text);

        myMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        renderMapObjects();

    }




//--------------------------------------------------------------------------------------------------

    // Creates a new LocationRequest with update intervals and priority
    private void createLocationRequest() {

        // The desired interval for location updates. Inexact. Updates may be more or less frequent.
        final long UPDATE_INTERVAL_IN_MS = 10000;

        // The fastest rate for active location updates. Exact. Updates will never be more frequent
        // than this value.
        final long FASTEST_UPDATE_INTERVAL_IN_MS = UPDATE_INTERVAL_IN_MS / 2;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }




    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mLastLocation == null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Lost connection to GoogleApiClient, restarting");
        buildGoogleApiClient();
    }




    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }


    /**
     * Converts a MarkerOptions position to a Location
      * @param target MarkerOptions that holds a position
     * @return Location object for the marker position
     */
    protected Location targetToLocation(MarkerOptions target){
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(target.getPosition().latitude);
        temp.setLongitude(target.getPosition().longitude);
        return temp;
    }

    /**
     * Builds a connection to the Google API to be able to use LocationServices
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
}