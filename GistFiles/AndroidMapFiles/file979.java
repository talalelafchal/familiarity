package com.map.marker;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class MapMarker extends MapActivity {

	private MapView mapView;

	private static final int latitude1 = -7824269;
	private static final int longitude1 = 110364532;

	private static final int latitude2 = -7822269;
	private static final int longitude2 = 110424532;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();

		Drawable drawable = this.getResources().getDrawable(R.drawable.marker);

		GeoPoint point = new GeoPoint(latitude1, longitude1);
		GeoPoint point2 = new GeoPoint(latitude2, longitude2);

		OverlayItem overlayitem = new OverlayItem(point, "Hai..", "Saya omayib");
		OverlayItem overlayitem2 = new OverlayItem(point2, "Hai..",
				"Saya Fitri");

		CustomOverlay itemizedOverlay = new CustomOverlay(drawable, this);

		itemizedOverlay.addOverlay(overlayitem);
		itemizedOverlay.addOverlay(overlayitem2);
		mapOverlays.add(itemizedOverlay);

		MapController mapController = mapView.getController();
		mapController.animateTo(point2);
		mapController.setZoom(15);

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}