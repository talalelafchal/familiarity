package com.geojson.core;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.geojson.model.GeoJsonLayer;;
import com.geojsonlibrary.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyMapView  implements IMyMapView {

    private final static String mLogTag = MyMapView.class.getSimpleName();
    private static Logger sLogger = LoggerFactory.getLogger(MyMapView.class);
    private final GoogleMap googleMap;

    private GeoJsonLayer backgroundLayer;
    private GeoJsonLayer unitsLayer;
    private GeoJsonLayer labelLayer;
    private GeoJsonLayer iconsLayer;
    private GeoJsonLayer doorLayer;
    private GeoJsonLayer wallLayer;

    private Context mContext;

    public MyMapView(GoogleMap googleMap, Context context, ViewGroup viewGroup) {
        this.googleMap = googleMap;
        this.mContext = context;

        setMapStyle(googleMap);
    }

    private void setMapStyle(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.setIndoorEnabled(false);

    }

    @Override
    public void onLevelChanged(Long areaId, Long levelId) {

        //every time a new level is selected, clear current map, render new map
        //clear all layers, markers, polygons
        // each layer need download(aync in background), then display on UI thread
        googleMap.clear();

        displayLevel( areaId, levelId);
    }

    @Override
    public void displayLevel(final Long areaId, final Long levelId) {

        //download and display layer separately

        addBackgroundLayerToMap(areaId, levelId);

        DownloadGeoJson displayUnits = new DownloadGeoJson(areaId, levelId, "units") {
            @Override
            public void onDownloadPostExecute(final JsonObject jsonObject) {

                Runnable runnable = new Runnable() {
                    public void run() {
                        unitsLayer = new GeoJsonLayer(googleMap, jsonObject);

                        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unitsLayer.addLayerToMap();
                                addLabelLayer(areaId, levelId);
                                addDoorLayer(areaId, levelId);

                            }
                        }, 1);
                    }
                };

                new Thread(runnable).start();
            }
        };

        displayUnits.DownloadAsync();

    }

    private void addBackgroundLayerToMap(final Long areaId, final Long levelId) {
        final DownloadGeoJson displayBackground = new DownloadGeoJson(areaId, levelId, "background") {
            @Override
            public void onDownloadPostExecute(final JsonObject jsonObject) {

                Runnable runnable = new Runnable() {
                    public void run() {

                        backgroundLayer = new GeoJsonLayer(googleMap, jsonObject);

                        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                backgroundLayer.addLayerToMap();

                            }
                        }, 1);
                    }
                };

                new Thread(runnable).start();
            }
        };

        displayBackground.DownloadAsync();
    }

    private void addLabelLayer(final Long areaId, final Long levelId) {
        DownloadGeoJson displayLabel = new DownloadGeoJson(areaId, levelId, "label") {
            @Override
            public void onDownloadPostExecute(JsonObject jsonObject) {

                labelLayer = new GeoJsonLayer(googleMap, jsonObject);

                labelLayer.mRenderer.mDefaultPointStyle.setVisible(false);

                android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        labelLayer.addLayerToMap();
                    }
                }, 1);

            }
        };

        displayLabel.DownloadAsync();
    }

    private void addDoorLayer(final Long areaId, final Long levelId) {
        DownloadGeoJson displayDoor = new DownloadGeoJson(areaId, levelId, "door") {
            @Override
            public void onDownloadPostExecute(final JsonObject jsonObject) {

                Runnable runnable = new Runnable() {
                    public void run() {
                        doorLayer = new GeoJsonLayer(googleMap, jsonObject);

                        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doorLayer.addLayerToMap();

                                addWallLayer(areaId, levelId);

                            }
                        }, 1);
                    }
                };

                new Thread(runnable).start();
            }
        };

        displayDoor.DownloadAsync();
    }

    private void addWallLayer(final Long areaId, final Long levelId) {
        DownloadGeoJson displayWall = new DownloadGeoJson(areaId, levelId, "wall") {
            @Override
            public void onDownloadPostExecute(final JsonObject jsonObject) {

                Runnable runnable = new Runnable() {
                    public void run() {
                        wallLayer = new GeoJsonLayer(googleMap, jsonObject);

                        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                wallLayer.addLayerToMap();
                            }
                        }, 1);
                    }
                };

                new Thread(runnable).start();

            }
        };

        displayWall.DownloadAsync();
    }

}
