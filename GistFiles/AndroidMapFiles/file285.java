package org.anon.OSMclick;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.*;


public class OSMclickActivity extends Activity {
    /** Called when the activity is first created. */
	private final int zoomInic = 18;
	private MapView mOsmv;
    private MapController mOsmvController;
    private GeoPoint locActual;
    private LinearLayout li;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        li = (LinearLayout)findViewById( R.id.main);
        li.setBackgroundColor(Color.YELLOW);
        locActual = new GeoPoint(19.356694,-99.140961);
        this.mOsmv = (MapView)findViewById(R.id.map);
        this.mOsmv.setBuiltInZoomControls(true);
        this.mOsmv.setMultiTouchControls(true);
        this.mOsmvController = this.mOsmv.getController();
        this.mOsmvController.setZoom(zoomInic);
        this.mOsmvController.setCenter(locActual); 
    }
}