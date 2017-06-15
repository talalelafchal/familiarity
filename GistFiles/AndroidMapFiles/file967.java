package ninja.rosh.lariofalesie;

import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/*

    MAP FRAGMENT

        <fragment
            android:id="@+id/map"
            android:name="com.google.android.gms.maps.SupportMapFragment"
            xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            tools:context=".MapsActivity"
            tools:layout="@layout/support_simple_spinner_dropdown_item"
        />


    TO GET THE MAP

    ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap()

 */


public class MapManager implements GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener{

    final LatLng center = new LatLng(45.988126906972425, 9.243070296943188);
    final float defaultZoom = 9.651244f;
    CameraUpdate defaultCameraPosition = null;
    GoogleMap map;
    GoogleMap.InfoWindowAdapter infoWindowAdapter;
    MapManager.EventListener eventListener;
    MapManager.Interface parent;

    MapManager(GoogleMap.InfoWindowAdapter iwa, MapManager.Interface p){
        infoWindowAdapter = iwa;
        parent = p;

        setup();
    }

    void setEventListener(MapManager.EventListener el){
        eventListener = el;
    }

    void resume(){
        if(map == null) setup();
    }

    public void smoothResetCamera(){
        CameraPosition currentCamera = map.getCameraPosition();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCamera.target, (currentCamera.zoom + defaultZoom) / 2), 500, new GoogleMap.CancelableCallback(){
            @Override
            public void onFinish(){
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, defaultZoom), 500, null);
            }

            @Override
            public void onCancel(){

            }
        });
    }

    void animateCamera(LatLng pos, float zoom){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
    }

    private void setup(){
        map = parent.getMap();

        //disable the toolbar on markers
        UiSettings mapSettings = map.getUiSettings();
        mapSettings.setMapToolbarEnabled(false);
        mapSettings.setCompassEnabled(false);
        mapSettings.setMyLocationButtonEnabled(false);
        mapSettings.setIndoorLevelPickerEnabled(false);

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        defaultCameraPosition = CameraUpdateFactory.newLatLngZoom(center, defaultZoom);
        map.moveCamera(defaultCameraPosition);

        map.setOnCameraChangeListener(this);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);

        map.setInfoWindowAdapter(infoWindowAdapter);
        map.setPadding(0, 10, 0, getSoftbuttonsbarHeight());

        parent.addDataToMap(map);
    }

    private int getSoftbuttonsbarHeight(){
        // getRealMetrics is only available with API 17 and +
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            DisplayMetrics metrics = new DisplayMetrics();

            parent.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            parent.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if(realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition){
        eventListener.onCameraChange(cameraPosition);
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        eventListener.onInfoWindowClick(marker);
    }

    @Override
    public void onMapClick(LatLng latLng){
        eventListener.onMapClick(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        return eventListener.onMarkerClick(marker);
    }

    interface Interface{
        void addDataToMap(GoogleMap map);

        WindowManager getWindowManager();

        GoogleMap getMap();
    }

    interface EventListener{
        void onCameraChange(CameraPosition pos);

        void onMapClick(LatLng point);

        boolean onMarkerClick(Marker marker);

        void onInfoWindowClick(Marker marker);
    }
}
