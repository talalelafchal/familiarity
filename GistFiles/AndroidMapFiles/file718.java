public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //static final LatLng latLong = new LatLng(21 , 57);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        
        Marker TP = googleMap.addMarker(new MarkerOptions()
        .position(new LatLng(0, 0))
        .title("Marker"));
        
        // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        
        // map.setTrafficEnabled(true); 
        
        // map.setIndoorEnabled(true);
        
        // map.setBuildingsEnabled(true);*/
        
        // googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
