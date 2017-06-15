private void updateCamera() {
    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
}