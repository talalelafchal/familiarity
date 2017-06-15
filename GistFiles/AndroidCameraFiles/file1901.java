package com.murano500k.cropio.task;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

/**
 * Created by artem on 2/3/17.
 */

public abstract class BaseMapActivity extends BaseActivity implements OnMapReadyCallback {


    protected OfflineManager offlineManager;
    protected ProgressBar progressBar;
    protected boolean isEndNotified;
    protected MapView mapView;
    public static final String ACTION_SHOW_MAP = "com.murano500k.cropio.task.map.ACTION_SHOW_MAP";
    public static final String EXTRA_AREA_ID = "com.murano500k.cropio.task.map.EXTRA_AREA_ID";
    public static final int AREA_ID_ALL = 666;
    public static final int AREA_ID_NULL = -1;
    public static final float ZOOM_DEFAULT = 11;
    private static final String TAG = "BaseMapActivity";
    private static final int PADDING_DEFAULT = 50;
    private static final int DURATION_DEFAULT = 5000;

    private MapboxMap mMap;

    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
    }

    protected void updateCameraPosition(LatLngBounds bounds, MapboxMap map){

    }
    protected void downloadOfflineRegionIfNeeded(String title, LatLngBounds bounds, String styleUrl) {
        if(offlineManager==null) offlineManager=OfflineManager.getInstance(this);
        OfflineTilePyramidRegionDefinition def=getOfflineTilePyramidRegionDefinition(bounds, styleUrl);
        byte[] metadata= getMetadata(title);
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                if(offlineRegions!=null && offlineRegions.length!=0){
                    for(OfflineRegion region : offlineRegions){
                        if(region.getMetadata()==metadata && region.getDefinition()==def)
                            Toast.makeText(getApplicationContext(), "Offline region already downloaded", Toast.LENGTH_SHORT).show();
                        else createOfflineRegion(def,metadata);
                    }
                }else createOfflineRegion(def, metadata);
            }
            @Override
            public void onError(String error) {
                Log.e(TAG, "listOfflineRegions onError: "+error );
                createOfflineRegion(def, metadata);
            }
        });

    }
    protected void createOfflineRegion(OfflineTilePyramidRegionDefinition definition, byte[] metadata){
        if(offlineManager==null) offlineManager=OfflineManager.getInstance(this);
        offlineManager.createOfflineRegion(
                definition,
                metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                            @Override
                            public void onStatusChanged(OfflineRegionStatus status) {
                                if (status.isComplete()) {
                                    Toast.makeText(getApplicationContext(), "Region downloaded successfully.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Region downloaded successfully.");
                                }
                            }

                            @Override
                            public void onError(OfflineRegionError error) {
                                Log.e(TAG, "onError reason: " + error.getReason());
                                Log.e(TAG, "onError message: " + error.getMessage());
                            }

                            @Override
                            public void mapboxTileCountLimitExceeded(long limit) {
                                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            //TODO: start
       // else showError("No permissions");
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    protected OfflineTilePyramidRegionDefinition getOfflineTilePyramidRegionDefinition(LatLngBounds bounds , String styleUrl ){
        return new OfflineTilePyramidRegionDefinition(
                styleUrl,
                bounds,
                10,
                20,
                this.getResources().getDisplayMetrics().density);
    }
    protected byte[]getMetadata(String title){
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, title);
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }
        return metadata;
    }

    private void findMe(MapboxMap mMap) {
        if(mMap==null) Toast.makeText(this, "null map", Toast.LENGTH_SHORT).show();
        else {
            mMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
            mMap.getTrackingSettings().setDismissAllTrackingOnGesture(true);
            Location location=mMap.getMyLocation();
            if(location!=null) {
                //moveCamera(new LatLng(location.getLatitude(),location.getLongitude()),mMap);
            }else Log.e(TAG, "findMe: no location");
        }
    }

    public void updateCameraPosi(LatLng latLng, MapboxMap map) {
        Log.d(TAG, "moveCamera: "+latLng);
        CameraPosition cameraPosition= new CameraPosition.Builder()
                .target(latLng)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), DURATION_DEFAULT);
    }
}
