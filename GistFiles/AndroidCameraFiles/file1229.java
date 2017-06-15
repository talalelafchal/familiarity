package rainbow.com.fitnessapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, LocationListener, OnConnectionFailedListener {

    //Variables




    private GoogleMap mMap;
    private static final String TAG = "FitnessApp";
    SupportMapFragment mapFrag;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    //Tracking Location Variables
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    //Location mCurrentLocation;
    //private static final long INTERVAL = 1000 * 60 * 5; //5 minute
    //private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    private boolean ifLocationHasChanged = false;
    String result_in_kms = "";


    //Timer Variables
    Handler Handler = new Handler();
    private long startTime, elapsedTime;
    private final int REFRESH_RATE = 100;
    private String hours, minutes, seconds;
    private long secs, mins, hrs;
    private boolean stopped = false;


    //Coding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        buildGoogleApiClient();
    }


    public void onStart() {
        super.onStart();


        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    public void onConnectionSuspended(int i) {
    }

    //Setting up map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }


    //Tracking Location
    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;


        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
    }

    //Calculating Distance
    private String getDistanceOnRoad(double latitude, double longitude,
                                     double prelatitute, double prelongitude) {
        String url = "http://maps.google.com/maps/api/directions/xml?origin="
                + latitude + "," + longitude + "&destination=" + prelatitute
                + "," + prelongitude + "&sensor=false&units=metric";
        String tag[] = { "text" };
        HttpResponse response = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost, localContext);
            InputStream is = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(is);
            if (doc != null) {
                NodeList nl;
                ArrayList args = new ArrayList();
                for (String s : tag) {
                    nl = doc.getElementsByTagName(s);
                    if (nl.getLength() > 0) {
                        Node node = nl.item(nl.getLength() - 1);
                        args.add(node.getTextContent());
                    } else {
                        args.add(" - ");
                    }
                }
                result_in_kms = String.format("%s", args.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result_in_kms;

    }



    //Timer
    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            Handler.postDelayed(this,REFRESH_RATE);
        }
    };

    public void startClick (View view){
        showStopButton();
        if(stopped){
            startTime = System.currentTimeMillis() - elapsedTime;
        }
        else{
            startTime = System.currentTimeMillis();
        }
        Handler.removeCallbacks(startTimer);
        Handler.postDelayed(startTimer, 0);
        startLocationUpdates();
        h1.removeCallbacks(startCalorieTracker);
        h1.postDelayed(startCalorieTracker,0);
        h2.removeCallbacks(startSpeedTracker);
        h2.postDelayed(startSpeedTracker,0);
    }

    public void stopClick (View view){
        hideStopButton();
        Handler.removeCallbacks(startTimer);
        h2.removeCallbacks(startSpeedTracker);
        h1.removeCallbacks(startCalorieTracker);
        stopped = true;
        stopLocationUpdates();

    }

    public void resetClick (View view){
        stopped = false;
        ((TextView)findViewById(R.id.time)).setText("00:00");
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
    }

    private void showStopButton(){
        ((Button)findViewById(R.id.buttonStart)).setVisibility(View.GONE);
        ((Button)findViewById(R.id.buttonReset)).setVisibility(View.GONE);
        ((Button)findViewById(R.id.buttonStop)).setVisibility(View.VISIBLE);
    }

    private void hideStopButton(){
        ((Button)findViewById(R.id.buttonStart)).setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.buttonReset)).setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.buttonStop)).setVisibility(View.GONE);
    }

    private void updateTimer (float time){
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);

        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

        ((TextView)findViewById(R.id.time)).setText(hours + ":" + minutes + ":" + seconds);
    }


    //Calculating Calories
    // I assume 1 km = 85 Calories burnt
    //If you find this inaccurate, you can change it

    private Handler h1= new Handler ();
    private Runnable startCalorieTracker = new Runnable () {

        @Override
        public void run() {
            h1.postDelayed(this, REFRESH_RATE);
            int CaloriesBurnt = Integer.parseInt(result_in_kms)*85;
            ((TextView)findViewById(R.id.distance)).setText(CaloriesBurnt + "Cal");
        }
    };


    //Calculating Speed

    private Handler h2= new Handler ();
    private Runnable startSpeedTracker = new Runnable () {

        @Override
        public void run() {
            h2.postDelayed(this, REFRESH_RATE);
            int Speed = Integer.parseInt(result_in_kms)/(Integer.parseInt(hours)+(Integer.parseInt(minutes)/60)+(Integer.parseInt(seconds)/3600));
            ((TextView)findViewById(R.id.speed)).setText(Speed + "km/h");
        }
    };






}