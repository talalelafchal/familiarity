package com.truckfly.truckfly;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class GoogleMapView extends MapView implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private static int CAMERA_CHANGE_REACT_THRESHOLD_MS = 500;
    private long lastCameraChangeCall = Long.MIN_VALUE;
    private HashMap<Double, Marker> mMarkerCache;
    private HashMap<Marker, Double> mMarkerKeys;

    public GoogleMapView(Context context) {
        super(context);
        onCreate(null);
        onResume();
        getMapAsync(this);
        mMarkerCache = new HashMap<>();
        mMarkerKeys = new HashMap<>();
    }

    public void setCameraPosition(ReadableMap options) {
        if (mMap == null) {
            Log.w("GoogleMapView", "map not initialized");
            return;
        }

        CameraPosition.Builder builder = CameraPosition.builder(mMap.getCameraPosition());

        if (options.hasKey("latitude") || options.hasKey("longitude")) {
            builder.target(getLatLng(options));
        }

        if (options.hasKey("zoom")) {
            builder.zoom((float) options.getDouble("zoom"));
        }

        if (options.hasKey("viewingAngle")) {
            builder.tilt((float) options.getDouble("viewingAngle"));
        }

        if (options.hasKey("bearing")) {
            builder.bearing((float)options.getDouble("bearing")).build();
        }

        CameraUpdate update = CameraUpdateFactory.newCameraPosition(builder.build());

        if (options.hasKey("duration")) {
            int duration = (int)Math.round(options.getDouble("duration") * 1000);
            mMap.animateCamera(update, duration, null);
        } else {
            mMap.animateCamera(update);
        }
    }


    public void setMarkers(ReadableArray markers) {
        if (mMap == null) {
            return;
        }

        HashMap<Double, Boolean> currentMarkersKeys = new HashMap<>();
        ArrayList<Double> toRemove = new ArrayList<>();

        for (int i = 0; i < markers.size(); i++) {
            ReadableMap markerData = markers.getMap(i);
            Double key = markerData.getDouble("key");
            Marker marker = mMarkerCache.get(key);

            currentMarkersKeys.put(key, true);

            if (marker == null) {
                marker = mMap.addMarker(createMarkerOptions(markerData));
                mMarkerCache.put(key, marker);
                mMarkerKeys.put(marker, key);
            }
            else {
                updateMarker(marker, markerData);
            }
        }

        for (Double key : mMarkerCache.keySet()) {
            if (!currentMarkersKeys.containsKey(key)) {
                toRemove.add(key);
            }
        }

        for (Double key : toRemove) {
            Marker marker = mMarkerCache.get(key);
            mMarkerKeys.remove(marker);
            marker.remove();
            mMarkerCache.remove(key);
        }
    }

    private MarkerOptions createMarkerOptions(ReadableMap markerData) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(getLatLng(markerData.getMap("position")))
                .icon(getIconFromName(markerData.getString("image")));

        if (markerData.hasKey("title")) {
            markerOptions.title(markerData.getString("title"));
        }

        if (markerData.hasKey("snippet")) {
            markerOptions.snippet(markerData.getString("snippet"));
        }

        return markerOptions;
    }

    private void updateMarker(Marker marker, ReadableMap markerData) {
        marker.setPosition(getLatLng(markerData.getMap("position")));

        if (markerData.hasKey("image")) {
            marker.setIcon(getIconFromName(markerData.getString("image")));
        }

        if (markerData.hasKey("title")) {
            marker.setTitle(markerData.getString("title"));
        }

        if (markerData.hasKey("snippet")) {
            marker.setSnippet(markerData.getString("snippet"));
        }
    }

    private BitmapDescriptor getIconFromName(String name) {
        int drawable = getResources().getIdentifier(
                name, "drawable", getContext().getPackageName()
        );
        return BitmapDescriptorFactory.fromResource(drawable);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.getMyLocation();

        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.setOnCameraChangeListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapClick(LatLng position) {
        WritableMap event = Arguments.createMap();
        event.putMap("position", getLatLng(position));

        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(getId(), "topMapTap", event);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        final long snap = System.currentTimeMillis();
        if (lastCameraChangeCall + CAMERA_CHANGE_REACT_THRESHOLD_MS > snap) {
            sendCameraChangeEvent(cameraPosition, true);
            lastCameraChangeCall = snap;
            return;
        }

        sendCameraChangeEvent(cameraPosition, false);

        lastCameraChangeCall = snap;
    }

    private void sendCameraChangeEvent(CameraPosition cameraPosition, boolean continuous) {
        WritableMap event = Arguments.createMap();
        WritableMap region = Arguments.createMap();
        WritableMap boundsMap = Arguments.createMap();
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        boundsMap.putMap("southWest", getLatLng(bounds.southwest));
        boundsMap.putMap("northEast", getLatLng(bounds.northeast));

        region.putDouble("zoom", cameraPosition.zoom);
        region.putDouble("bearing", cameraPosition.bearing);
        region.putDouble("viewingAngle", cameraPosition.tilt);
        region.putMap("position", getLatLng(cameraPosition.target));
        region.putMap("bounds", boundsMap);

        event.putMap("region", region);
        event.putBoolean("continuous", continuous);

        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(getId(), "topChange", event);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        WritableMap event = Arguments.createMap();
        event.putDouble("markerKey", mMarkerKeys.get(marker));
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(getId(), "topMarkerTap", event);

        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        WritableMap event = Arguments.createMap();
        event.putDouble("markerKey", mMarkerKeys.get(marker));
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(getId(), "topMarkerInfoWindowTap", event);
    }

    private LatLng getLatLng(ReadableMap map) {
        if (!map.hasKey("latitude") || !map.hasKey("longitude")) {
            return null;
        }

        return new LatLng(map.getDouble("latitude"), map.getDouble("longitude"));
    }

    private WritableMap getLatLng(LatLng position) {
        WritableMap map = Arguments.createMap();
        map.putDouble("latitude", position.latitude);
        map.putDouble("longitude", position.longitude);
        return map;
    }
}
