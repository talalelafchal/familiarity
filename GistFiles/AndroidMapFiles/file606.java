package com.example.map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapSample extends FragmentActivity {

  /** 緯度、経度を表示するボタン。 */
	private Button mButton01;
	/** 移動ボタン。 */
	private Button mButton02;
	/** GoogleMapを格納する。 */
	private GoogleMap mMap;
	/** BindするContext。 */
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_sample);

		// Contextを格納(Toastで使用)
		mContext = this.getApplicationContext();
		// res/layout/activity_map_sample.xmlのmapViewのを呼び出す
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapView)).getMap();
		try {
			// Mapの初期化
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			Log.d("GoogleMap", "You must update Google Maps.");
			finish();
		}

		// 緯度・経度を表示するボタン。押すと緯度経度のToastを表示する
		mButton01 = (Button) findViewById(R.id.Button01);

		// Button01のイベント処理
		mButton01.setOnClickListener(new OnClickListener() {
			// Button01が押された時の処理
			@Override
			public void onClick(View v) {
				LatLng latLng = mMap.getCameraPosition().target;
				// 地図の中心点の緯度・経度を取得
				double lat = latLng.latitude;
				double lon = latLng.longitude;
				Toast.makeText(mContext, "lat:" + lat + " lon:" + lon,
						Toast.LENGTH_LONG).show();
			}
		});
		mButton02 = (Button) findViewById(R.id.Button02);

		// Button02のイベント処理
		mButton02.setOnClickListener(new OnClickListener() {
			// Button01が押された時の処理
			@Override
			public void onClick(View v) {
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(
						35.712496928929276, 139.80357825960232), 15); //スカイツリー
				mMap.moveCamera(cu);
			}
		});
	}
}