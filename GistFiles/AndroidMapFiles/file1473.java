package com.thef.salusk.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.thef.salusk.db.datatypes.LocationPointDataType;
import com.thef.salusk.utils.Constants;
import com.thef.salusk.utils.Utils;

/**
 * This service is constantly listening to changes in the location. Points are filtered by distance threshold and by
 * comparing the distance between a new point to the two previous points. The new validated points are sent to the view.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private Bundle bundle = new Bundle();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ResultReceiver locationReceiver;
    private LocationPointDataType firstPointDataType = new LocationPointDataType();
    private LocationPointDataType secondPointDataType = new LocationPointDataType();
    private LocationPointDataType newLocationPointDataType = new LocationPointDataType();
    private Location firstPoint, secondPoint;
    private Thread locationRequestService;


    /**
     * Called by the system every time a client explicitly starts the service providing the arguments it supplied
     *
     * @param intent  The Intent supplied to startService(Intent)
     * @param flags   Additional data about this start request
     * @param startId A unique integer representing this specific request to start
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationReceiver = intent.getParcelableExtra("locationReceiver");
        if (checkServiceConnection()) { //Start Location Request by connecting to Google Play Services
            buildGoogleApiClient();
            createLocationRequest();
            mGoogleApiClient.connect();
        }
        return Service.START_STICKY;
    }


    /**
     * Invoked asynchronously when GoogleApiClient connect request has successfully completed
     *
     * @param bundle Bundle of data provided to clients by Google Play services
     */
    @Override
    public void onConnected(Bundle bundle) {
        locationRequestService = new Thread(new Runnable() {
            public void run() {
                Looper.prepare(); //Initialise the current thread as a looper.
                startLocationUpdate(); //trying to get the update location
                Looper.loop();
            }
        }, "LocationServiceThread");
        locationRequestService.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failure: " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called when the location has changed
     *
     * @param newLocation the new location point
     */
    @Override
    public void onLocationChanged(Location newLocation) {
        if (newLocation != null) {
            processNewLocation(newLocation);
        }
    }


    /**
     * The system calls this method when the service is no longer used and is being destroyed.
     * Used to interrupt the service, remove the location updates and disconnect from Google Api Client
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        locationRequestService.interrupt();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }


    /**
     * Check that Google Play services is available
     *
     * @return True if Google Play services are available
     */
    private boolean checkServiceConnection() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            return false;
        }
    }


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.TIME_INTERVAL_GPS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL_GPS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Start Location Request Service
     */
    private void startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    /**
     * This method process a new location
     *
     * @param newLocation
     */
    private void processNewLocation(Location newLocation) {
        if (firstPoint == null) {
            firstPoint = newLocation;
            setLocationPointDataType(firstPoint, firstPointDataType);
        } else if (secondPoint == null) {
            if (firstPoint.distanceTo(newLocation) >= Constants.DISTANCE_THRESHOLD) {
                secondPoint = newLocation;
                setLocationPointDataType(secondPoint, secondPointDataType);
            } else return;
        } else if (secondPoint.distanceTo(newLocation) >= Constants.DISTANCE_THRESHOLD) { // Define a distance threshold to omits points that are too close
            setLocationPointDataType(newLocation, newLocationPointDataType);
            double newPointDistanceToFirstPoint = newLocation.distanceTo(firstPoint);
            double newPointDistanceToSecondPoint = newLocation.distanceTo(secondPoint);
            if (newPointDistanceToFirstPoint > newPointDistanceToSecondPoint) { // Save first point, second point and newLocation
                setLocationPointDistanceCalories(firstPoint, secondPoint, secondPointDataType);
                bundle = prepareLocationBundle(Constants.MAP_DRAW_OK);
            } else { // Save first point and newLocation. Second point is identified as possible wrong point. Criteria: distance of new point to second point is greater than the distance to the first point.
                setLocationPointDistanceCalories(firstPoint, newLocation, newLocationPointDataType);
                bundle = prepareLocationBundle(Constants.MAP_DRAW_ERROR);
            }
            locationReceiver.send(Constants.STATUS_NEW_LOCATION, bundle); //Send points to view to update map
            updateFirstPoint(newLocation); // Now new point is first point
        }
    }

    /**
     * This method prepares the bundle of new points to send to the view
     *
     * @param map_draw_status indicates if second point is ok or identified as a wrong point
     * @return
     */
    private Bundle prepareLocationBundle(int map_draw_status) {
        bundle.clear();
        bundle.putParcelable("firstPointDataType", firstPointDataType);
        if (map_draw_status == Constants.MAP_DRAW_OK)
            bundle.putParcelable("secondPointDataType", secondPointDataType);
        bundle.putParcelable("newPointDataType", newLocationPointDataType);
        return bundle;
    }


    private void setLocationPointDataType(Location location, LocationPointDataType locationPointDataType) {
        locationPointDataType.setTimestamp(Utils.getCurrentTimestamp());
        locationPointDataType.setLatitude((float) location.getLatitude());
        locationPointDataType.setLongitude((float) location.getLongitude());
        locationPointDataType.setAccuracy(location.getAccuracy());
        locationPointDataType.setSpeed(location.getSpeed());
    }


    private void setLocationPointDistanceCalories(Location firstPoint, Location secondPoint, LocationPointDataType locationPointDataType) {
        float distance = firstPoint.distanceTo(secondPoint);
        locationPointDataType.setDistance(firstPoint.distanceTo(secondPoint));
        locationPointDataType.setCalories((float) Utils.getCalories(distance));
    }


    private void updateFirstPoint(Location newLocation) {
        firstPoint = newLocation;
        firstPointDataType = newLocationPointDataType;
        secondPoint = null;
    }
}
