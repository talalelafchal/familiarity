public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TouchSupportMapFragment mapFragment;

    private FrameLayout frameLayout;

    boolean Is_MAP_Moveable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (TouchSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        frameLayout = (FrameLayout) findViewById(R.id.map_fl);

        ((SwitchCompat) findViewById(R.id.btn_draw_State)).setChecked(!Is_MAP_Moveable);
        ((SwitchCompat) findViewById(R.id.btn_draw_State)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Is_MAP_Moveable = !b;
            }
        });
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(37.35, -122.0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("HERE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //loadPolygon();
        attachTouchInterface();
    }

    Projection projection;

    private List<LatLng> val = new ArrayList<>();

    public double latitude;
    public double longitude;

    private void attachTouchInterface() {
        mapFragment.setOnDragListener(new MapWrapperLayout.OnDragListener() {@Override
            public void onDrag(MotionEvent motionEvent) {
                Log.i("ON_DRAG", "X:" + String.valueOf(motionEvent.getX()));
                Log.i("ON_DRAG", "Y:" + String.valueOf(motionEvent.getY()));

                float x = motionEvent.getX(); // get screen x position or coordinate
                float y = motionEvent.getY();  // get screen y position or coordinate

                int x_co = Integer.parseInt(String.valueOf(Math.round(x))); // casting float to int
                int y_co = Integer.parseInt(String.valueOf(Math.round(y))); // casting float to int

                projection = mMap.getProjection(); // Will convert your x,y to LatLng
                Point x_y_points = new Point(x_co, y_co);// accept int x,y value
                LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points); // convert x,y to LatLng
                latitude = latLng.latitude; // your latitude
                longitude = latLng.longitude; // your longitude

                Log.i("ON_DRAG", "lat:" + latitude);
                Log.i("ON_DRAG", "long:" + longitude);

                // Handle motion event:
            }
        });

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Is_MAP_Moveable)
                    return false;

                float x = event.getX();
                float y = event.getY();

                int x_co = Math.round(x);
                int y_co = Math.round(y);

                projection = mMap.getProjection();
                Point x_y_points = new Point(x_co, y_co);

                LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
                latitude = latLng.latitude;

                longitude = latLng.longitude;

                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        // finger touches the screen
                        val.add(new LatLng(latitude, longitude));

                    case MotionEvent.ACTION_MOVE:
                        // finger moves on the screen
                        val.add(new LatLng(latitude, longitude));

                    case MotionEvent.ACTION_UP:
                        // finger leaves the screen
                        Draw_Map();
                        break;
                }

                return !Is_MAP_Moveable;
            }
        });
    }

    public void Draw_Map() {
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(Color.BLACK);
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(Color.RED);
        mMap.addPolygon(rectOptions);
    }

    private void loadPolygon() {
        // Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(37.35, -122.0),
                        new LatLng(37.45, -122.0),
                        new LatLng(37.45, -122.2),
                        new LatLng(37.35, -122.2),
                        new LatLng(37.35, -122.0));

// Get back the mutable Polygon
        Polygon polygon = mMap.addPolygon(rectOptions);
    }
}
