public class UpdateLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    CoordinatorLayout coordinatorLayout;

    TextView markerText;
    Button savelocation;
      LatLng center;
    private LinearLayout markerLayout,liner_address;

    private List<android.location.Address> addresses =  new ArrayList<>();
    private TextView Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_updatelocation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        markerText = (TextView) findViewById(R.id.locationMarkertext);
        Address = (TextView) findViewById(R.id.adressText);
        markerLayout = (LinearLayout) findViewById(R.id.locationMarker);
        liner_address = (LinearLayout)findViewById(R.id.liner_address);
        savelocation =  (Button) findViewById(R.id.btn_done);

              setSupportActionBar(toolbar);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        markerLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub



            }
        });

        savelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatelocal();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);


        mMap.setOnMapClickListener(this);

        SmartLocation.with(UpdateLocation.this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {;
                        CameraUpdate center=
                                CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                                        location.getLongitude()));
                        mMap.moveCamera(center);
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                    }
                });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude)).title("MK001")).showInfoWindow();


        mMap.clear();
        markerLayout.setVisibility(View.VISIBLE);

        center = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

        try {
            new GetLocationAsync(center.latitude, center.longitude)
                    .execute();

        } catch (Exception e) {
        }



    }

    @Override
    public void onMapClick(LatLng latLng) {




    }


    public void hudestuff(){

        liner_address.setVisibility(View.GONE);
        savelocation.setVisibility(View.GONE);

    }
    public void showstuff(){

        liner_address.setVisibility(View.VISIBLE);
        savelocation.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_done:

                break;
        }
    }

    public void updatelocal(){

        Log.d("updatelocal","Started");
        WebUtils.postJsonData(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updatelocal","good");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("updatelocal","error");
                error.printStackTrace();
            }
        }, Api.updatermlocation(center,Details.getRM(UpdateLocation.this).getId()),null);

        Log.d("updatelocal","end");
    }

    private class GetLocationAsync extends AsyncTask<String, Void, String> {

        // boolean duplicateResponse;
        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub

            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute() {
            Address.setText(" Getting location ");
            markerText.setText("....");
        }

        @Override
        protected String doInBackground(String... params) {


            try {
                return getaddress();
            } catch (IOException e) {
                e.printStackTrace();
                return  "not found";
            }

        }


        public    String getaddress() throws IOException {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(UpdateLocation.this, Locale.getDefault());

            addresses = geocoder.getFromLocation(center.latitude, center.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.size()>0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                return address+" "+city+" "+state;
            }else{
                return null;
            }



        }

        @Override
        protected void onPostExecute(String result) {

                    Address.setText(result);
            markerText.setText(" Set Location for:"+ Details.getRM(UpdateLocation.this).getId());

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_updatelocation, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mylocation:
                setmylocation();
                return true;
            case R.id.action_toggle_map:
                togglemaptype();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void togglemaptype(){
       switch ( mMap.getMapType()){
           case GoogleMap.MAP_TYPE_NORMAL:
               mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
               break;
           case GoogleMap.MAP_TYPE_SATELLITE:
               mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
               break;
       }
    }

    public   void setmylocation(){
        SmartLocation.with(UpdateLocation.this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {;
                        CameraUpdate center=
                                CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                                        location.getLongitude()));

                        mMap.moveCamera(center);
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                    }
                });
    }
}
