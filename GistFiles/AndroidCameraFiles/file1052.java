package com.example.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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
				// Geocoderで住所を取得
				Geocoder mGeocoder = new Geocoder(mContext, Locale.getDefault());

				// 住所を格納する文字列バッファ
				StringBuffer sb = new StringBuffer();
				// 緯度、経度、取得可能な最大の住所数を指定
				List<Address> addresses;
				try {
					addresses = mGeocoder.getFromLocation(lat, lon, 1);
					for (Address address : addresses) {
						// 取得された住所情報のサイズでforループ
						int maxSize = address.getMaxAddressLineIndex();
						for (int i = 0; i <= maxSize; i++) {
							sb.append(address.getAddressLine(i));
						}
					}
				} catch (IOException e) {
					// ネットワークエラーの時
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// 緯度・経度が範囲外の値の時
					e.printStackTrace();
				}
				Toast.makeText(mContext,
						"lat:" + lat + " lon:" + lon + " address:" + sb,
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
						35.712496928929276, 139.80357825960232), 15); // スカイツリー
				mMap.moveCamera(cu);
			}
		});
	}
}