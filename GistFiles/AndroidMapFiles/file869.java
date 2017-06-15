package com.example.map;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
 
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
 
public class MapSample extends MapActivity {
 
    /** 緯度、経度を表示するボタン。 */
    private Button mButton01;
    /** MapViewを格納する。 */
    private MapView mMap;
    /** BindするContext。 */
    private Context mContext;
 
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map_sample );
 
        // Contextを格納(Toastで使用)
        mContext = this.getApplicationContext();
        // res/layout/activity_map_sample.xmlのmapViewのを呼び出す
        mMap = (MapView) findViewById( R.id.mapView );
 
        // 緯度・経度を表示するボタン。押すと緯度経度のToastを表示する
        mButton01 = (Button) findViewById( R.id.Button01 );
 
        // Button01のイベント処理
        mButton01.setOnClickListener( new OnClickListener() {
            // Button01が押された時の処理
            @Override
            public void onClick( View v ) {
                // 地図の中心点の緯度・経度を取得
                long lat = mMap.getMapCenter().getLatitudeE6();
                long lon = mMap.getMapCenter().getLongitudeE6();
                Toast.makeText( mContext, "lat:" + lat + " lon:" + lon, Toast.LENGTH_LONG ).show();
            }
        } );
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}