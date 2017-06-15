
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            mMap.setMyLocationEnabled(true);
            tracker = new LocationTracker(context
            ) {
                @Override
                public void onLocationFound(Location location) {
                    // Do some stuff
                    log(String.valueOf(location.getLatitude()));
                    log(String.valueOf(location.getLongitude()));
                    myLatitude = location.getLatitude();
                    myLongitude = location.getLongitude();

                    LatLng my_location = new LatLng(location.getLatitude(), location.getLongitude());

                    if (myMarker == null) {
                        log("Marker null");
                        myLatitude = location.getLatitude();
                        myLongitude = location.getLongitude();
                        myMarker = mMap.addMarker(new MarkerOptions().position(my_location).icon(iconMe));
                        CameraPosition myPosition = new CameraPosition.Builder()
                                .target(my_location).zoom(15).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
                    } else {
                        log("Marker remove and update!");
                        myMarker.remove();
                        myMarker = mMap.addMarker(new MarkerOptions().position(my_location).icon(iconMe));
                    }
                }

                @Override
                public void onTimeout() {
                    log("Connection timeout!");
                }
            };
            tracker.startListening();
        }
    }