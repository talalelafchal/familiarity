// MapController(Dummy)
package com.google.android.maps;

import android.view.KeyEvent;
import android.view.View;

public final class MapController implements android.view.View.OnKeyListener {

	MapController(MapView mapView) {
	}

	public void animateTo(GeoPoint point){
	}
	
	public void animateTo(GeoPoint point, android.os.Message message){
	}
	
	public void animateTo(GeoPoint point, java.lang.Runnable runnable){
	}
			
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		throw new RuntimeException("stub");
	}

	public void scrollBy(int x, int y){
	}
			
	public void setCenter(GeoPoint point) {
	}

	public int setZoom(int zoomLevel) {
		throw new RuntimeException("stub");
	}

	public void stopAnimation(boolean jumpToFinish){
	}
	
	public void stopPanning(){
	}
	
	public boolean zoomIn() {
		throw new RuntimeException("stub");
	}

	public boolean zoomInFixing(int xPixel, int yPixel) {
		throw new RuntimeException("stub");
	}

	public boolean zoomOut() {
		throw new RuntimeException("stub");
	}

	public boolean zoomOutFixing(int xPixel, int yPixel) {
		throw new RuntimeException("stub");
	}

	public void zoomToSpan(int latSpanE6, int lonSpanE6){
	}
}
