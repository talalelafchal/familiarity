 package com.mapexample.mapexample;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

  
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	public MyItemizedOverlay(Drawable defaultMarker, Context ctx) {
		super(boundCenterBottom(defaultMarker));

	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	public void clear() {

		mOverlays.clear();
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {

		return false;
	}

}