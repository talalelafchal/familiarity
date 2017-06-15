private void fitMarkersOnMap() {
        if (markers.size() == 0) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        
        final LatLngBounds bounds = builder.build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        map.animateCamera(cameraUpdate);
    }