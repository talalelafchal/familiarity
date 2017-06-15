
    // Add MarkerView
    final LatLng markerPosition = new LatLng(38.907298, -77.043478);
    final MarkerView markerView = mapboxMap.addMarker(new MarkerViewOptions()
            .icon(IconFactory.getInstance(GeocoderActivity.this).fromResource(R.drawable.ic_circle))
            .anchor(0.5f, 0.5f)
            .position(markerPosition)
    );

    final float circleDiameterSize = getResources().getDimension(R.dimen.circle_size);
    mapboxMap.setOnCameraChangeListener(new MapboxMap.OnCameraChangeListener() {

        private ObjectAnimator pulseAnimation;

        @Override
        public void onCameraChange(CameraPosition position) {
            View converView = mapboxMap.getMarkerViewManager().getView(markerView);
            if (converView != null) {
                if (pulseAnimation == null && position.target.distanceTo(markerPosition) < 0.5f * circleDiameterSize) {
                    pulseAnimation = ObjectAnimator.ofPropertyValuesHolder(converView,
                            PropertyValuesHolder.ofFloat("scaleX", 1.8f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.8f));
                    pulseAnimation.setDuration(310);
                    pulseAnimation.setRepeatCount(ObjectAnimator.INFINITE);
                    pulseAnimation.setRepeatMode(ObjectAnimator.REVERSE);
                    pulseAnimation.start();
                } else if (pulseAnimation != null && position.target.distanceTo(markerPosition) >= 0.6f * circleDiameterSize) {
                    pulseAnimation.cancel();
                    Animator initialStateAnimator = ObjectAnimator.ofPropertyValuesHolder(converView,
                            PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.0f));
                    initialStateAnimator.start();
                    pulseAnimation = null;
                }
            }
        }
    });
