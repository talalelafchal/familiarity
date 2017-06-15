package com.example.mapdemo;

import android.graphics.Point;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MovingMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker mMarker;
    private List<LatLng> mPoints;
    private final Handler mHandler = new Handler();
    private int mCurrentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving_map);
        mPoints = SampleRoute.GetPoints();
        mCurrentPos = 0;

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        addMarkerAndZoom();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveCamera(mPoints.get(mCurrentPos));
                if(++mCurrentPos < mPoints.size()){
                    mHandler.postDelayed(this, 1500);
                }
            }
        }, 1500);
    }

    private void moveCamera(LatLng destination){
        Projection projection =  mMap.getProjection();

        LatLngBounds bounds = projection.getVisibleRegion().latLngBounds;
        int boundsTopY = projection.toScreenLocation(bounds.northeast).y;
        int boundsBottomY = projection.toScreenLocation(bounds.southwest).y;
        int boundsTopX = projection.toScreenLocation(bounds.northeast).x;
        int boundsBottomX = projection.toScreenLocation(bounds.southwest).x;

        int offsetY = (boundsBottomY - boundsTopY) / 10;
        int offsetX = (boundsTopX - boundsBottomX ) / 10;

        Point destinationPoint = projection.toScreenLocation(destination);
        int destinationX = destinationPoint.x;
        int destinationY = destinationPoint.y;

        int scrollX = 0;
        int scrollY = 0;

        if(destinationY <= (boundsTopY + offsetY)){
            scrollY = -(Math.abs((boundsTopY + offsetY) - destinationY));
        }
        else if(destinationY >= (boundsBottomY - offsetY)){
            scrollY = (Math.abs(destinationY - (boundsBottomY - offsetY)));
        }
        if(destinationX >= (boundsTopX - offsetX)){
            scrollX = (Math.abs(destinationX - (boundsTopX - offsetX)));
        }
        else if(destinationX <= (boundsBottomX + offsetX)){
            scrollX = -(Math.abs((boundsBottomX + offsetX) - destinationX));
        }
        mMap.animateCamera(CameraUpdateFactory.scrollBy(scrollX, scrollY));
        mMarker.setPosition(destination);
    }

    private void addMarkerAndZoom(){
        LatLng start = mPoints.get(mCurrentPos++);
        mMarker = mMap.addMarker(new MarkerOptions().position(start).icon(
                BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_VIOLET)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 18));
    }
}
