package com.example.mapsforgesample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

/** mapsforge の地図表示サンプル
 *
 *  https://github.com/mapsforge/mapsforge/blob/master/docs/Getting-Started-Android-App.md
 */
public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String MAP_FILE = "japan_multi.map";

    private final static int PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());

        mapView = new MapView(this);
        setContentView(mapView);

        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setZoomLevelMin((byte)10);
        mapView.setZoomLevelMax((byte)20);

        //Log.d(TAG, "externalDir: " + Environment.getExternalStorageDirectory());

        // targetSDK 23 以上の場合、実行時にパーミッション確認を行う必要がある
        // https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
        // https://developer.android.com/training/permissions/requesting.html
        final int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            displayMap();
        }

    }

    @Override
    protected void onDestroy() {
        mapView.destroyAll();;
        AndroidGraphicFactory.clearResourceMemoryCache();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayMap();
                    return;
                }
            default:
        }

        // アプリを終了
        this.finish();
    }

    private void displayMap() {

        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache", mapView.getModel().displayModel.getTileSize(), 1f, mapView.getModel().frameBufferModel.getOverdrawFactor() );

        MapDataStore mds = new MapFile(new File(Environment.getExternalStorageDirectory() + "/Download/", MAP_FILE));
        TileRendererLayer trl = new TileRendererLayer(tileCache, mds, mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE) {
            @Override
            public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
                return setMarker(tapLatLong);
            }
        };

        trl.setXmlRenderTheme(InternalRenderTheme.DEFAULT);

        mapView.getLayerManager().getLayers().add(trl);

        mapView.setCenter(new LatLong(34.491297, 136.709685)); // 伊勢市駅
        mapView.setZoomLevel((byte)12);

    }

    private boolean setMarker(LatLong latlong) {

//        Marker marker = createMarker(latlong, R.drawable.marker_red);
        Marker marker = createBubbleMarker(latlong, R.drawable.marker_red, "ポップアップテスト");
        mapView.getLayerManager().getLayers().add(marker);

        return true;
    }

    private Marker createMarker(LatLong latlong, int resource) {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        return new Marker(latlong, bitmap, 0, -bitmap.getHeight() / 2);
    }

    private Marker createBubbleMarker(LatLong latlong, int resource, String text) {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        return new MarkerWithBubble(latlong, bitmap, 0, -bitmap.getHeight() / 2, this.mapView, text);
    }

}
