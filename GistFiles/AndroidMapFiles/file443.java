public class MainActivity extends AppCompatActivity {
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment));
        mapFragment.getMapAsync(this);
    }
    
    @Override
    public void onMapReady(GoogleMap map) {
      
      map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
      map.getUiSettings().setZoomControlsEnabled(true);
      
      String title = "This is Title";
      String subTitle = "This is \nSubtitle";
      
      //Marker
      MarkerOptions markerOpt = new MarkerOptions();
      markerOpt.position(new LatLng(Double.valueOf(strToLat), Double.valueOf(strToLng)))
                .title(title)
                .snippet(subTitle)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
  
      //Set Custom InfoWindow Adapter
      CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MainActivity.this);
      map.setInfoWindowAdapter(adapter);
      
      map.addMarker(markerOpt).showInfoWindow();
        
    }
}