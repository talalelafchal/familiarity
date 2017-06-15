package com.truckfly.truckfly;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.Map;

public class GoogleMapViewManager extends SimpleViewManager<GoogleMapView> {
    public static final String REACT_CLASS = "RCTGoogleMap";

    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("topMarkerTap",
                        MapBuilder.of("phasedRegistrationNames",
                                MapBuilder.of(
                                        "bubbled", "onMarkerTap",
                                        "captured", "onMarkerTapCaptured")))
                .put("topMarkerInfoWindowTap",
                        MapBuilder.of("phasedRegistrationNames",
                                MapBuilder.of(
                                        "bubbled", "onMarkerInfoWindowTap",
                                        "captured", "onMarkerInfoWindowTapCaptured")))
                .build();
    }

    @ReactProp(name = "myLocationEnabled")
    public void setMyLocationEnabled(GoogleMapView view, boolean enabled) {
        view.getMap().setMyLocationEnabled(enabled);
    }

    @ReactProp(name = "trafficEnabled")
    public void setTrafficEnabled(GoogleMapView view, boolean enabled) {
        view.getMap().setTrafficEnabled(enabled);
    }

    @ReactProp(name = "indoorEnabled")
    public void setIndoorEnabled(GoogleMapView view, boolean enabled) {
        view.getMap().setIndoorEnabled(enabled);
    }

    @ReactProp(name = "mapType")
    public void setMapType(GoogleMapView view, int type) {
        view.getMap().setMapType(type);
    }

    @ReactProp(name = "cameraPosition")
    public void setCameraPosition(GoogleMapView view, ReadableMap options) {
        view.setCameraPosition(options);
    }

    @ReactProp(name = "markers")
    public void setMarkers(GoogleMapView view, ReadableArray markers) {
        view.setMarkers(markers);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public GoogleMapView createViewInstance(ThemedReactContext context) {
        return new GoogleMapView(context);
    }
}
