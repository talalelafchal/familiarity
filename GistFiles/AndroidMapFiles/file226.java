public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Add a marker
            LatLng ltlg = new LatLng(35.1837661, 136.9002939);
            mMap.addMarker(new MarkerOptions().position(ltlg).title("Marker"));

            CameraPosition camerapos = new CameraPosition.Builder().target(ltlg).zoom(15).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));

            // Overlay GSI Map Tiles
            setOverlay();
        }
    }

    private void setOverlay() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String urlString = String.format("http://cyberjapandata.gsi.go.jp/xyz/pale/%d/%d/%d.png", zoom, x, y);

                URL url = null;
                try {
                    url = new URL(urlString);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
        TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
    }
}
