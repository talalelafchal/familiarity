private void updatePolyline() {
    mMapView.removeOverlay(mLine);
    mLine.addPoint(mLatLng);
    mMapView.getOverlays().add(mLine);
}