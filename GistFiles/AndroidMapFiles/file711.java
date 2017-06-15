private MapView mMapView;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mbox_view);

    // Get MapView
    mMapView = (MapView) findViewById(R.id.mapview);
    initializePolyline();

    // Start PubNub
    [ . . . ]
}