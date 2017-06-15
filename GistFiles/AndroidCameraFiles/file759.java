package com.example.tasneem.googleplacesapi;
import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private int PROXIMITY_RADIUS;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static Context context;
    public static double latitude, longitude;
    Button btnPlaceCategory, btnLog;
    ListView lst;
    LstAdap lstAdap, plsAdap;
    ArrayList<String> arrayList;
    public static ArrayList<String> placesList;
    String categories[];
    public static String sel_category;
    boolean flaglogin, flagMap;
    double latarr[], lngarr[],distance[];
    String placeNames[],vicinity[];
    TextView txtnm, txtlat, txtlng,txtdis,txtvic;
    RelativeLayout layout;
    DBHelper dbHelper;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flaglogin = false;
        flagMap = false;
        PROXIMITY_RADIUS = 5000;
        context = this;
        setContentView(R.layout.splash_layout);

        layout = (RelativeLayout) findViewById(R.id.layout);
        CountDownTimer cdt = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                flaglogin = true;
                layout = (RelativeLayout) findViewById(R.id.loglayout);
                layout.setVisibility(View.VISIBLE);

            }
        };
        cdt.start();

        btnLog = (Button) findViewById(R.id.btnlog);
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String unm, vunm, pwd, vpwd;
                EditText txtunm = (EditText) findViewById(R.id.etxtunm);
                EditText txtpwd = (EditText) findViewById(R.id.etxtpwd);
                unm = txtunm.getText().toString();
                pwd = txtpwd.getText().toString();
                vunm = "user";
                vpwd = "password";
                if (unm.equals(vunm) && pwd.equals(vpwd)) {
                    flagMap = true;
                    setContentView(R.layout.activity_maps);
                    initComp();
                    setListener();
                    setUpMapIfNeeded();
                } else
                    Toast.makeText(context, "Please enter valid username & password", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setListener() {
        if (flagMap == true) {
            btnPlaceCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flag=true;
                    txtnm.setText("");
                    txtlat.setText("");
                    txtlng.setText("");
                    txtdis.setText("");
                    txtvic.setText("");
                    lst.setVisibility(View.VISIBLE);
                    lstAdap = new LstAdap(context, arrayList);
                    lst.setAdapter(lstAdap);
                }
            });
            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LstAdap adap = (LstAdap) lst.getAdapter();
                    if (adap == lstAdap) {
                        switch (position) {
                            case 0:
                                sel_category = categories[0];
                                break;
                            case 1:
                                sel_category = categories[1];
                                break;
                            case 2:
                                sel_category = categories[2];
                                break;
                            case 3:
                                sel_category=categories[3];
                                break;
                            case 4:
                                sel_category=categories[4];
                                break;
                            case 5:
                                sel_category=categories[5];
                                break;
                            case 6:
                                sel_category=categories[6];
                                break;
                            case 7:
                                sel_category=categories[7];
                                break;
                        }
                        flag=false;
                        fillLst(sel_category);
                    } else if (adap == plsAdap) {
                        lst.setVisibility(View.INVISIBLE);
                        txtnm.setText("Place Name=" + placeNames[position]);
                        txtlat.setText("Latitude=" + latarr[position]);
                        txtlng.setText("Longitude=" + lngarr[position]);
                        String result = String.format("%.4f", distance[position]);
                        txtdis.setText("Distance from your location=" + result + "km");
                        txtvic.setText("Address="+vicinity[position]);

                        mMap.clear();
                        setUpMapIfNeeded();
                        MarkerOptions markerOptions=new MarkerOptions();
                        LatLng latLng = new LatLng(latarr[position], lngarr[position]);
                        markerOptions.position(latLng);
                        markerOptions.title(placeNames[position]);
                        mMap.addMarker(markerOptions);
                    }
                }
            });
        }
    }

    private void fillLst(String sel_category){
        try {
        //String key="AIzaSyAFlUboS3cZ4k4d5IZ1fybfCTKjnxWIz6I";
        String key = "AIzaSyB1ZRDcGhscxGu77fxWb5336CGWz6VwyQc";
        String type = sel_category;
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + key);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = mMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);

            Cursor cursor = dbHelper.getData(sel_category);
            cursor.moveToFirst();
            int i = 0;
            int size = cursor.getCount();
            Log.i("size", "count:" + size);
            if(size==0){
                Toast.makeText(context,"Network problem. Please try again.",Toast.LENGTH_SHORT).show();
            }
            else {
                placeNames = new String[size];
                vicinity=new String[size];
                latarr = new double[size];
                lngarr = new double[size];
                distance=new double[size];
                placesList.clear();
                while (!cursor.isAfterLast()) {
                    if (i == size)
                        break;
                    placesList.add(cursor.getString(cursor.getColumnIndex("name")));
                    placeNames[i] = placesList.get(i);
                    vicinity[i]=cursor.getString(cursor.getColumnIndex("vicinity"));
                    latarr[i] = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
                    lngarr[i] = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lng")));
                    distance[i]=distance(latarr[i], lngarr[i]);
                    i++;
                    cursor.moveToNext();
                }
                plsAdap = new LstAdap(context, placesList);
                lst.setAdapter(plsAdap);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public float distance(double lat,double lng)
    {
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude,lat, lng, results);
        results[0]=results[0]/1000;
        return results[0];

    }
    private void initComp() {
        context = this;
        flag=true;
        txtnm = (TextView) findViewById(R.id.txtname);
        txtlat = (TextView) findViewById(R.id.txtlat);
        txtlng = (TextView) findViewById(R.id.txtlng);
        txtdis=(TextView)findViewById(R.id.txtdis);
        txtvic=(TextView)findViewById(R.id.txtvic);

        arrayList = new ArrayList<String>();
        placesList = new ArrayList<String>();

        dbHelper = new DBHelper(context);

        categories = new String[]{"hospital","hotel", "atm", "school", "police", "airport", "parking","cafe"};
        for (int i = 0; i < categories.length; i++) {
            arrayList.add(categories[i]);
        }
        btnPlaceCategory = (Button) findViewById(R.id.btnPlaces);
        lst = (ListView) findViewById(R.id.lst);

    }

    @Override
    protected void onResume() {
        super.onResume();/*
        if (flagMap == true) {
            setContentView(R.layout.activity_maps);
            //setDim();
            initComp();
            setListener();
            setUpMapIfNeeded();
        }
        else if(flaglogin==true){
            layout = (RelativeLayout) findViewById(R.id.loglayout);
            layout.setVisibility(View.VISIBLE);
        }*/
    }


    @Override
    public void onBackPressed() {
       if(!(txtnm.getText()=="")&&(sel_category!=null||!sel_category.isEmpty()))
        {
            txtnm.setText("");
            txtdis.setText("");
            txtlng.setText("");
            txtlat.setText("");
            txtvic.setText("");
            lst.setVisibility(View.VISIBLE);
            plsAdap = new LstAdap(context, placesList);
            lst.setAdapter(plsAdap);
            setUpMapIfNeeded();
            MarkerOptions markerOptions = new MarkerOptions();
            for(int i=0;i<placesList.size();i++) {
                LatLng latLng = new LatLng(latarr[i], lngarr[i]);
                markerOptions.position(latLng);
                markerOptions.title(placeNames[i]);
                mMap.addMarker(markerOptions);
            }

        }
        else if(flag==false)
       {
           flag = true;
           lstAdap = new LstAdap(context, arrayList);
           lst.setAdapter(lstAdap);
           mMap.clear();
           setUpMapIfNeeded();
       }
        else if(flag==true)
            super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 2000, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
