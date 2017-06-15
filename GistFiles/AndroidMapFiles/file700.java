package jp.kickhost.eventnavi;

import java.util.ArrayList;

import jp.kickhost.localsearch.model.Event;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventMapFragment extends Fragment {
        private GoogleMap mMap;
        private static View view;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                // ここでハマった
                // 困ったときのstackoverflow
                // http://stackoverflow.com/questions/14083950/duplicate-id-tag-null-or-parent-id-with-another-fragment-for-com-google-androi
                if (view != null) {
                        ViewGroup parent = (ViewGroup) view.getParent();
                        if (parent != null)
                                parent.removeView(view);
                }
                try {
                        view = inflater.inflate(R.layout.fragment_event_map, container, false);
                } catch (InflateException e) {
                        /* map is already there, just return view as it is */
                }
                return view;
        }

        @Override
        public void onResume() {
                super.onResume();
                setUpMapIfNeeded();
        }

        private void setUpMapIfNeeded() {
                if (mMap == null) {
                        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
                        if (mMap != null) {
                                setUpMap();
                        }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.87365, 151.20689), 14.0f));
        }

        //　とりあえず適当にピンたててみたり。    
        private void setUpMap() {
                mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(-33.87365, 151.20689)).title("Marker"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(-33.57365, 151.20689)).title("Marker"));
                // 現在位置表示の有効化
                mMap.setMyLocationEnabled(true);
                // 設定の取得
                UiSettings settings = mMap.getUiSettings();
                // コンパスの有効化
                settings.setCompassEnabled(true);
                // 現在位置に移動するボタンの有効化
                settings.setMyLocationButtonEnabled(true);
                // ズームイン・アウトボタンの有効化
                settings.setZoomControlsEnabled(true);
                // 回転ジェスチャーの有効化
                settings.setRotateGesturesEnabled(false);
                // スクロールジェスチャーの有効化
                settings.setScrollGesturesEnabled(true);
                // Tlitジェスチャー(立体表示)の有効化
                settings.setTiltGesturesEnabled(false);
                // ズームジェスチャー(ピンチイン・アウト)の有効化
                settings.setZoomGesturesEnabled(true);
                mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition position) {
                                Log.d("TEST", "zoop:" + position.zoom);
                        }
                });
        }

}