package com.truckfly.truckfly;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.Map;

public class GoogleMapModule extends ReactContextBaseJavaModule {
    public GoogleMapModule(ReactApplicationContext reactContext) {
       super(reactContext);
    }

    @Override
    public String getName() {
        return "GoogleMapManager";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        constants.put("mapTypeNone", GoogleMap.MAP_TYPE_NONE);
        constants.put("mapTypeHybrid", GoogleMap.MAP_TYPE_HYBRID);
        constants.put("mapTypeSatellite", GoogleMap.MAP_TYPE_SATELLITE);
        constants.put("mapTypeNormal", GoogleMap.MAP_TYPE_NORMAL);
        constants.put("mapTypeTerrain", GoogleMap.MAP_TYPE_TERRAIN);

        return constants;
    }
}
