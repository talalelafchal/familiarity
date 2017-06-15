private boolean isFirstMessage = true;

private void updateMarker() {
    if (!isFirstMessage) {
        mMapView.removeMarker(mMarker);
    }
    isFirstMessage = false;
    mMarker = new Marker(mMapView, "", "", mLatLng);
    mMapView.addMarker(mMarker);
}