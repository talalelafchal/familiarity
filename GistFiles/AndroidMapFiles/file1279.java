 
//Done in a MapFragment
@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setCompassEnabled(false);
        mapUiSettings.setZoomControlsEnabled(false);
        mapUiSettings.setMapToolbarEnabled(false);
    }


//The same done in MapView.java during init

void someInitMethod(){
       GoogleMapOptions options = new GoogleMapOptions()
                .liteMode(true)
                .mapToolbarEnabled(false);
        mMapView = new MapView(mContext, options);
        mMapView.onCreate(null);
}
