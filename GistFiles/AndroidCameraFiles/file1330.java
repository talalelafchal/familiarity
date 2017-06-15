package dk.shape.goboat.animation.animators;

import android.content.Context;
import android.view.Choreographer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import dk.shape.goboat.R;
import dk.shape.shared.utils.Logger;

//
// MapAnimator
// GoBoat
//
// Created by Ben De La Haye on 07/11/2016.
// Copyright (c) 2016 SHAPE A/S. All rights reserved.
//
public class MapAnimator {

    private static final double LOCATION_PRECISION = 0.00001; // See http://gis.stackexchange.com/questions/8650/measuring-accuracy-of-latitude-and-longitude

    public interface Listener {
        void onMapBearingChanged(float heading);
    }

    private static final boolean DEBUG = false; // BuildConfig.DEBUG;

    private static final int MAP_UPDATE_INTERVAL = 16; // 16 == 60 frames a second

    private static final int BEARING_ANIM_DURATION = 160;
    private static final int LOCATION_ANIM_DURATION = 100;

    private static final float MIN_ZOOM_LEVEL = 10;
    private static final float DEFAULT_ZOOM_LEVEL = 15;
    private static final float MAX_ZOOM_LEVEL = 19;

    private static final int RESET_ZOOM_DELAY = 10000; // 10 seconds
    private static final int ZOOM_ANIM_DURATION = 400;

    private final Listener _listener;

    private boolean _shouldAnimateMap;
    private boolean _animatingMap;

    private GoogleMap _googleMap;

    private float _targetZoom;
    private float _currentZoom;

    private long _mapZoomUpdateMillis;
    private boolean _zoomUpdated;

    private boolean _firstLocationUpdate = true;
    private LatLng _targetLocation;

    private float _targetBearing;

    public MapAnimator(Listener listener) {
        _listener = listener;
    }

    // region Public API

    public void setMap(Context context, GoogleMap googleMap) {
        _googleMap = googleMap;

        _googleMap.getUiSettings().setScrollGesturesEnabled(false);
        _googleMap.getUiSettings().setCompassEnabled(false);
        _googleMap.getUiSettings().setRotateGesturesEnabled(false);
        _googleMap.getUiSettings().setZoomGesturesEnabled(false);
        _googleMap.getUiSettings().setMapToolbarEnabled(false);
        _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        _googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style));

        _googleMap.setIndoorEnabled(false);
        _googleMap.setTrafficEnabled(false);

        _googleMap.setMinZoomPreference(MIN_ZOOM_LEVEL);
        _googleMap.setMaxZoomPreference(MAX_ZOOM_LEVEL);

        if (!_animatingMap) startAnimationThread();
    }

    public void updateZoom(float zoom) {
        if (DEBUG) Logger.info(this, "updateZoom - zoom:" + zoom);
        _targetZoom = zoom;
        _zoomUpdated = true;
    }

    public void updateLocation(LatLng location) {
        if (DEBUG) Logger.debug(this, "updateLocation - location:" + location);

        _targetLocation = location;
    }

    public void updateBearing(float bearing) {
        _targetBearing = bearing;
    }

    public void restartAnimationThread() {
        if (!_animatingMap) {
            _shouldAnimateMap = true;
            Choreographer.getInstance().postFrameCallback(frameTimeNanos -> updateMap());
        }
    }

    public void stopAnimationThread() {
        _shouldAnimateMap = false;
    }

    // endregion

    private void startAnimationThread() {
        _shouldAnimateMap = true;
        if (!_animatingMap) Choreographer.getInstance().postFrameCallback(frameTimeNanos -> updateMap());
    }

    private void updateMap() {
        if (_targetLocation == null) {
            // Maybe it won't be next frame
            Choreographer.getInstance().postFrameCallback(frameTimeNanos -> updateMap());
            return;
        }

        if (!_shouldAnimateMap) {
            _animatingMap = false;
            return;
        } else {
            _animatingMap = true;
        }

        boolean isUsefulUpdate = false;

        long currentMillis = System.currentTimeMillis();
        CameraPosition currentCameraPosition = _googleMap.getCameraPosition();
        CameraPosition.Builder builder = CameraPosition.builder(currentCameraPosition);

        // Location
        LatLng intendedLocation = getIntendedLocation(currentCameraPosition);
        builder.target(intendedLocation);
        if (intendedLocation.latitude != _targetLocation.latitude || intendedLocation.longitude != _targetLocation.longitude) {
            isUsefulUpdate = true;
        }

        // Bearing
        float intendedBearing = getIntendedBearing(currentCameraPosition);
        builder.bearing(intendedBearing);
        if (!bearingMightAsWellBeEqual(intendedBearing, getMappedTargetBearing())) {
            _listener.onMapBearingChanged(intendedBearing);
            isUsefulUpdate = true;
        }

        // Zoom
        float intendedZoom = DEFAULT_ZOOM_LEVEL;
        if (!_firstLocationUpdate) {
            intendedZoom = getIntendedZoom(currentCameraPosition, currentMillis);
        } else {
            _firstLocationUpdate = false;
        }

        builder.zoom(intendedZoom);
        if (DEBUG) Logger.info(this, "intendedZoom: " + intendedZoom + ", _targetZoom: " + _targetZoom);
        if (zoomMightAsWellBeEqual(intendedZoom, _targetZoom)) {
            isUsefulUpdate = true;
        }

        // Update map
        if (isUsefulUpdate) {
            Logger.info(this, "--> Useful update, updating Google map");
            _googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }

        Choreographer.getInstance().postFrameCallback(frameTimeNanos -> updateMap());
    }

    // region Helper methods

    // Compass (_targetBearing) range is -180 to 180, whereas map bearing range is 0 to 360
    private float getMappedTargetBearing() {
        return _targetBearing < 0 ? (_targetBearing + 360) : _targetBearing;
    }

    private boolean bearingMightAsWellBeEqual(double value1, double value2) {
        return value1 <= value2 + (value2 / 100) && value1 >= value2 - (value2 / 100);
    }

    private boolean zoomMightAsWellBeEqual(double value1, double value2) {
        return value1 <= value2 + (value2 / 1000000) && value1 >= value2 - (value2 / 1000000);
    }

    private float getIntendedBearing(CameraPosition currentCameraPosition) {

        float currentBearing = currentCameraPosition.bearing;
        float bearingDifference = getMappedTargetBearing() - currentBearing;

        // Correct map skipping from 0 to 360
        if (bearingDifference > 180) {
            bearingDifference -= 360;
        } else if (bearingDifference < -180) {
            bearingDifference += 360;
        }

        currentBearing += bearingDifference / (BEARING_ANIM_DURATION / MAP_UPDATE_INTERVAL);

        return currentBearing;
    }

    private LatLng getIntendedLocation(CameraPosition currentCameraPosition) {

        LatLng currentLocation = currentCameraPosition.target;
        if (_firstLocationUpdate) {
            currentLocation = _targetLocation;
            _firstLocationUpdate = false;

        } else {

            double locationDifferenceLat = _targetLocation.latitude - currentLocation.latitude;
            double locationDifferenceLong = _targetLocation.longitude - currentLocation.longitude;

            // Just return the target is we are within spitting distance of it
            if (locationDifferenceLat <= LOCATION_PRECISION && locationDifferenceLong <= LOCATION_PRECISION) {
                return _targetLocation;
            }

            currentLocation = new LatLng(currentLocation.latitude + locationDifferenceLat / (LOCATION_ANIM_DURATION / MAP_UPDATE_INTERVAL),
                                        currentLocation.longitude + locationDifferenceLong / (LOCATION_ANIM_DURATION / MAP_UPDATE_INTERVAL));
        }

        return currentLocation;
    }

    private float getIntendedZoom(CameraPosition currentCameraPosition, long currentMillis) {

        // Zooming
        float intendedZoom;
        float zoomDifference;
        if (_zoomUpdated) {

            if (DEBUG) Logger.debug(this, "_zoomUpdated, ignoring zoom reset");

            intendedZoom = _targetZoom;
            _mapZoomUpdateMillis = currentMillis;
            _zoomUpdated = false;

        } else {

            if (currentMillis - _mapZoomUpdateMillis > RESET_ZOOM_DELAY) {

                if (DEBUG) Logger.debug(this, "Resetting zoom level");

                // Reset zoom
                _targetZoom = DEFAULT_ZOOM_LEVEL;
                _mapZoomUpdateMillis = currentMillis;
            }

            _currentZoom = currentCameraPosition.zoom;
            intendedZoom = _currentZoom;
            zoomDifference = _targetZoom - intendedZoom;
            intendedZoom += zoomDifference / (ZOOM_ANIM_DURATION / MAP_UPDATE_INTERVAL);
        }

        return intendedZoom;
    }

    // endregion
}