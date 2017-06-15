private GeoPoint center;
private int zoomLevel = -1;
	
@Override
public void onPause() {
    super.onPause();
    center = mapView.getMapCenter();
    zoomLevel = mapView.getZoomLevel();
}

@Override
public void onResume() {
    super.onResume();
    if (zoomLevel >= 0) {
        final MapController mc = mapView.getController();
        mc.setCenter(center);
        mc.setZoom(zoomLevel);
    }
}
