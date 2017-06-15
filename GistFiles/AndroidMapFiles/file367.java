package com.example.liveinfowindow;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/*
 * This code and the idea behind it was derived from this StackOverflow
 * answer: http://stackoverflow.com/a/15040761/3153792
 */
public class MapViewWrapper extends FrameLayout {
	private static final int REFRESH_BUFFER_INTERVAL = 150;
	private static final Matrix matrix = new Matrix();
	private GoogleMap map;
	private InfoWindowAdapter infoWindowAdapter, infoWindowAdapterWrapper;
	private Map<Marker, Point> markerInfoWindowAnchorOffsetMap;
	private Marker marker;
	private Point infoWindowAnchorOffset;
	private View infoView;
	private boolean useCachedInfoView;

	public MapViewWrapper(Context context) {
		super(context);
	}

	public MapViewWrapper(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MapViewWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setInfoWindowAdapter(GoogleMap map, InfoWindowAdapter adapter) {
		if (this.map != null && this.map != map) {
			map.setInfoWindowAdapter(null);
			clearMarkers();
		}
		this.map = map;
		infoWindowAdapter = adapter;
		if (infoWindowAdapterWrapper == null)
			infoWindowAdapterWrapper = new InfoWindowAdapterWrapper();
		map.setInfoWindowAdapter(infoWindowAdapterWrapper);
	}

	public void removeInfoWindowAdapter() {
		infoWindowAdapter = null;
		infoWindowAdapterWrapper = null;
		map.setInfoWindowAdapter(null);
	}

	public void addMarker(Marker marker, MarkerOptions options, int iconResId) {
		Drawable icon = getContext().getResources().getDrawable(iconResId);
		addMarker(marker, options, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
	}

	public void addMarker(Marker marker, MarkerOptions options, Bitmap icon) {
		addMarker(marker, options, icon.getWidth(), icon.getHeight());
	}

	public void addMarker(Marker marker, MarkerOptions options, int iconWidth, int iconHeight) {
		if (map == null)
			throw new IllegalStateException("GoogleMap not initialized");
		if (markerInfoWindowAnchorOffsetMap == null)
			markerInfoWindowAnchorOffsetMap = new HashMap<Marker, Point>();
		Point infoWindowAnchorOffset = markerInfoWindowAnchorOffsetMap.get(marker);
		if (infoWindowAnchorOffset == null) markerInfoWindowAnchorOffsetMap.put(
				marker, infoWindowAnchorOffset = new Point());
		float[] anchorPoints = new float[] {
				iconWidth * options.getAnchorU(),
				iconHeight * options.getAnchorV(),
				iconWidth * options.getInfoWindowAnchorU(),
				iconHeight * options.getInfoWindowAnchorV()
		};
		matrix.setRotate(options.getRotation(), anchorPoints[0], anchorPoints[1]);
		matrix.mapPoints(anchorPoints);
		infoWindowAnchorOffset.set(
				Math.round(anchorPoints[2]) - Math.round(anchorPoints[0]),
				Math.round(anchorPoints[3]) - Math.round(anchorPoints[1])
		);
	}

	public void removeMarker(Marker marker) {
		markerInfoWindowAnchorOffsetMap.remove(marker);
		if (this.marker == marker) {
			this.marker = null;
			infoWindowAnchorOffset = null;
			infoView = null;
			removeInfoWindow();
		}
	}

	public void clearMarkers() {
		markerInfoWindowAnchorOffsetMap.clear();
		marker = null;
		infoWindowAnchorOffset = null;
		infoView = null;
		removeInfoWindow();
	}

	private void removeInfoWindow() {
		for (int i = 0, count = getChildCount(); i < count; i++) {
			if (getChildAt(i) instanceof InfoViewWrapper) {
				removeViewAt(i);
				break;
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (map != null && marker != null && infoView != null && marker.isInfoWindowShown()) {
			Point infoViewCoords = map.getProjection().toScreenLocation(marker.getPosition());
			infoViewCoords.offset(infoWindowAnchorOffset.x - (infoView.getWidth() / 2),
					infoWindowAnchorOffset.y - infoView.getHeight());
			ev.offsetLocation(-infoViewCoords.x, -infoViewCoords.y);
			boolean handled = infoView.dispatchTouchEvent(ev);
			ev.offsetLocation(infoViewCoords.x, infoViewCoords.y);
			if (handled) return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	private class InfoWindowAdapterWrapper implements InfoWindowAdapter {
		private View infoWindow, infoContents;

		@Override
		public View getInfoWindow(Marker marker) {
			if (useCachedInfoView) useCachedInfoView = false;
			else {
				View infoWindow = infoWindowAdapter.getInfoWindow(marker);
				if (infoWindow == null) return null;
				infoWindowAnchorOffset = markerInfoWindowAnchorOffsetMap.get(marker);
				if (infoWindowAnchorOffset == null) return infoWindow;
				new InfoViewWrapper(infoWindow);
				this.infoWindow = infoWindow;
			}
			MapViewWrapper.this.marker = marker;
			return infoWindow;
		}

		@Override
		public View getInfoContents(Marker marker) {
			if (useCachedInfoView) useCachedInfoView = false;
			else {
				View infoContents = infoWindowAdapter.getInfoContents(marker);
				if (infoContents == null) return null;
				infoWindowAnchorOffset = markerInfoWindowAnchorOffsetMap.get(marker);
				if (infoWindowAnchorOffset == null) return infoContents;
				new InfoViewWrapper(infoContents);
				this.infoContents = infoContents;
			}
			MapViewWrapper.this.marker = marker;
			return infoContents;
		}
	}

	private class InfoViewWrapper extends FrameLayout {
		private long lastRefreshTime;
		private boolean isRefreshing;

		public InfoViewWrapper(View infoView) {
			super(infoView.getContext());
			addView(infoView, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
			setVisibility(GONE);
			removeInfoWindow();
			MapViewWrapper.this.addView(this,
					new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
			MapViewWrapper.this.infoView = infoView;
		}

		@Override
		public void requestLayout() {
			refreshInfoWindow();
		}

		@Override
		public void forceLayout() {
			refreshInfoWindow();
		}

		@Override
		public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
			refreshInfoWindow();
			return null;
		}

		@Override
		public void invalidate() {
			if (marker == null || !marker.isInfoWindowShown()) return;
			useCachedInfoView = true;
			marker.showInfoWindow();
			isRefreshing = false;
			lastRefreshTime = SystemClock.uptimeMillis();
		}

		private void refreshInfoWindow() {
			if (marker == null || !marker.isInfoWindowShown() || isRefreshing) return;
			postInvalidateDelayed(REFRESH_BUFFER_INTERVAL -
					(SystemClock.uptimeMillis() - lastRefreshTime));
			isRefreshing = true;
		}
	}
}