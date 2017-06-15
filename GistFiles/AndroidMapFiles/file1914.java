package com.example.map;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
 
public class MapSample extends MapActivity {
 
    /** 緯度、経度を表示するボタン。 */
    private Button mButton01;
    /** 移動ボタン。 */
    private Button mButton02;
    /** MapViewを格納する。 */
    private MapView mMap;
    /** MapControllerを格納する。 */
    private MapController mMapController;
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
 
        // Mapの操作で使用
        mMapController = mMap.getController();
 
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
 
        // 移動ボタン。押すと設定の緯度経度に移動
        mButton02 = (Button) findViewById( R.id.Button02 );
 
        // Button02のイベント処理
        mButton02.setOnClickListener( new OnClickListener() {
            // Button02が押された時の処理
            @Override
            public void onClick( View v ) {
                GeoPoint point = new GeoPoint( 35709999, 139810767 ); // スカイツリー
                mMapController.animateTo( point );
            }
        } );
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}