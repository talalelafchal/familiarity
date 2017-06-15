package com.example.map;

import android.os.Bundle;
 
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
 
public class MapSample extends MapActivity {
 
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
 
        // MapViewのインスタンスを生成
        MapView mMap = new MapView( this, "****** Your Android Maps API key ******" );
        mMap.setEnabled( true );
        mMap.setClickable( true );
 
        // MapViewを画面に張り付ける
        setContentView( mMap );
 
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}