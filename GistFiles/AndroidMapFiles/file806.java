package com.example.johan_lunds.mapboxissue;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.LinearInterpolator;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int[] PULSE_DRAWABLES = new int[]{
            R.drawable.explore_fishing_water_pulse_00000,
            R.drawable.explore_fishing_water_pulse_00001,
            R.drawable.explore_fishing_water_pulse_00002,
            R.drawable.explore_fishing_water_pulse_00003,
            R.drawable.explore_fishing_water_pulse_00004,
            R.drawable.explore_fishing_water_pulse_00005,
            R.drawable.explore_fishing_water_pulse_00006,
            R.drawable.explore_fishing_water_pulse_00007,
            R.drawable.explore_fishing_water_pulse_00008,
            R.drawable.explore_fishing_water_pulse_00009,
            R.drawable.explore_fishing_water_pulse_00010,
            R.drawable.explore_fishing_water_pulse_00011,
            R.drawable.explore_fishing_water_pulse_00012,
            R.drawable.explore_fishing_water_pulse_00013,
            R.drawable.explore_fishing_water_pulse_00014,
            R.drawable.explore_fishing_water_pulse_00015,
            R.drawable.explore_fishing_water_pulse_00016,
            R.drawable.explore_fishing_water_pulse_00017,
            R.drawable.explore_fishing_water_pulse_00018,
            R.drawable.explore_fishing_water_pulse_00019,
            R.drawable.explore_fishing_water_pulse_00020,
            R.drawable.explore_fishing_water_pulse_00021,
            R.drawable.explore_fishing_water_pulse_00022,
            R.drawable.explore_fishing_water_pulse_00023,
            R.drawable.explore_fishing_water_pulse_00024,
            R.drawable.explore_fishing_water_pulse_00025,
            R.drawable.explore_fishing_water_pulse_00026,
            R.drawable.explore_fishing_water_pulse_00027,
            R.drawable.explore_fishing_water_pulse_00028,
            R.drawable.explore_fishing_water_pulse_00029,
            R.drawable.explore_fishing_water_pulse_00030,
            R.drawable.explore_fishing_water_pulse_00031,
            R.drawable.explore_fishing_water_pulse_00032,
            R.drawable.explore_fishing_water_pulse_00033,
            R.drawable.explore_fishing_water_pulse_00034,
            R.drawable.explore_fishing_water_pulse_00035,
            R.drawable.explore_fishing_water_pulse_00036,
    };

    private MapView mMapView;
    private MapboxMap mMap;

    private SymbolLayer mWaterPulseLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // token taken from https://github.com/mapbox/mapbox-android-demo/blob/f303ce66b8b97bfd3eb92b7d38dd324f4ce64722/MapboxAndroidDemo/src/main/res/values/strings.xml#L9
        Mapbox.getInstance(this, "pk.eyJ1IjoiY2FtbWFjZSIsImEiOiJjaW9vbGtydnQwMDAwdmRrcWlpdDVoM3pjIn0.Oy_gHelWnV12kJxHQWV7XQ");

        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapview);

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMap = mapboxMap;

        addSource();
        addPulseImages();
        addWaterPulseLayer();
        createWaterPulseAnimator();
    }

    private void addSource() {
        List<Feature> markerCoordinates = new ArrayList<>();
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromCoordinates(Position.fromCoordinates(18, 59.5)))
        );
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
        // NOTE: empty source will also reproduce the issue
        GeoJsonSource geoJsonSource = new GeoJsonSource("source", featureCollection);

        mMap.addSource(geoJsonSource);
    }

    private void addPulseImages() {
        for (int i = 0; i < PULSE_DRAWABLES.length; i++) {
            BitmapDrawable frame = (BitmapDrawable) getDrawable(PULSE_DRAWABLES[i]);
            Bitmap bitmap = frame.getBitmap();
            String imageName = String.format(Locale.US, "pulse-frame-%d", i);

            mMap.addImage(imageName, bitmap);
        }
    }

    private void addWaterPulseLayer() {
        mWaterPulseLayer = new SymbolLayer("pulse-layer", "source");
        mMap.addLayer(mWaterPulseLayer);
    }

    private void createWaterPulseAnimator() {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(1500);
        animator.setStartDelay(0);
        animator.setIntValues(0, PULSE_DRAWABLES.length);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int previousValue = -1;

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int i = (int) animator.getAnimatedValue();

                if (previousValue != i) {
                    String imageName = String.format(Locale.US, "pulse-frame-%d", i);
                    mWaterPulseLayer.setProperties(iconImage(imageName));
                }

                previousValue = i;
            }
        });

        animator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }
}
