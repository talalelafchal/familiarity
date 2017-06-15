package putugunation.com.mapsroute.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import putugunation.com.mapsroute.R;
import putugunation.com.mapsroute.apiservices.ApiService;
import putugunation.com.mapsroute.helpers.Constant;
import putugunation.com.mapsroute.helpers.LoggingInterceptorGoogle;
import putugunation.com.mapsroute.helpers.Utils;
import putugunation.com.mapsroute.models.Data;
import putugunation.com.mapsroute.models.DirectionResultsModel;
import putugunation.com.mapsroute.models.Legs;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private GoogleMap googleMap;
    private ApiService serviceGoogleDirection;
    public static List<Data> data;

    private LatLng currentPosition;
    private LocationManager locationManager;
    private Button buttonCheckList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCheckList = (Button) findViewById(R.id.buttonCekList);

        //1. initialization your maps
        //2. set your lat and lang
        try {
            initilizeMap();
            Data d1 = new Data();
            d1.setLatitude(-8.6947 + "");
            d1.setLongitude(115.263 + "");
            d1.setModified("Sanur Beach");

            Data d2 = new Data();
            d2.setLatitude(-8.7208 + "");
            d2.setLongitude(115.1692 + "");
            d2.setModified("Kuta Beach");

            Data d3 = new Data();
            d3.setLatitude(-8.8 + "");
            d3.setLongitude(115.2333 + "");
            d3.setModified("Dreamland Beach");

            Data d4 = new Data();
            d4.setLatitude(-8.8453597 + "");
            d4.setLongitude(115.1110346 + "");
            d4.setModified("Nyang-Nyang Beach");

            Data d5 = new Data();
            d5.setLatitude(-8.1246437 + "");
            d5.setLongitude(115.3133908 + "");
            d5.setModified("Bondalem Village");


            List<Data> addata = new ArrayList<>();
            addata.add(d1);
            addata.add(d2);
            addata.add(d3);
            addata.add(d4);
            addata.add(d5);

            data = addata;
            setDistanceForAll(data);
            addMarker(data);

        }catch (Exception e){e.printStackTrace();}

        buttonCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDestinationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initilizeMap();
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * this method used to get direction and duration between 2 LatLng
     * @param origin
     * @param destination
     * @return
     */
    private Legs getDirectionAndDuration(final LatLng origin, final LatLng destination){
        OkHttpClient clientGoogleApi = new OkHttpClient();
        clientGoogleApi.interceptors().add(new LoggingInterceptorGoogle());
        Retrofit retrofitGoogleApi = new Retrofit.Builder()
                .baseUrl(Constant.GOOGLE_END_POINT)
                .client(clientGoogleApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceGoogleDirection = retrofitGoogleApi.create(ApiService.class);

        final Legs[] distanceDurationModel = new Legs[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Call<DirectionResultsModel> directionResultsCall;
                directionResultsCall = serviceGoogleDirection.getDistanceAndDuration(origin.latitude + "," + origin.longitude , destination.latitude + "," + destination.longitude, "false","driving","true");
                try {
                    DirectionResultsModel results = directionResultsCall.execute().body();
                    distanceDurationModel[0] = results.getRoutes().get(0).getLegs().get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();

            }
        });

        thread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  distanceDurationModel[0];
    }


    /**
     * this method used to set distance of all Data
     * @param listData
     * @return
     */
    private List<Data> setDistanceForAll(List<Data> listData){
        for(Data data : listData){
            double destLat = Double.parseDouble(data.getLatitude());
            double destLng = Double.parseDouble(data.getLongitude());
            LatLng destination = new LatLng(destLat,destLng);
            Legs legs = getDirectionAndDuration(currentPosition, destination);
            data.setLegs(legs);


            Log.d("TAG", "TEST : " + data.getLegs().getDistance().getText());
            Log.d("TAG", "TEST : " + data.getLegs().getDuration().getText());

        }

        return listData;
    }


    /**
     * this method used to show a map
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.maps)).getMap();
            //check if map created successful or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Sorry unable to crated map", Toast.LENGTH_SHORT).show();
            }
        }


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
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria,true);
        //get current location from gps
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider,20000,0,this);


        String latitude = Utils.getStrings(MainActivity.this, Constant.CURRENT_LAT);
        String longitude = Utils.getStrings(MainActivity.this, Constant.CURRENT_LONG);
        Toast.makeText(this, "LOKASIKU : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        currentPosition = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        //creating marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(Utils.getStrings(this, Constant.CURRENT_LAT)), Double.parseDouble(Utils.getStrings(this,Constant.CURRENT_LONG)))).title("Hi You!").snippet("This is your location");
        googleMap.addMarker(marker).showInfoWindow();


//        googleMap.addMarker(new MarkerOptions().position(new LatLng(-8.6947, 115.263)).title("Sanur Beach").snippet("One of Sunrise spot")).showInfoWindow();
//        googleMap.addMarker(new MarkerOptions().position(new LatLng(-8.7208, 115.1692)).title("Kuta Beach").snippet("One of Sunset spot")).showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Utils.getStrings(this, Constant.CURRENT_LAT)), Double.parseDouble(Utils.getStrings(this,Constant.CURRENT_LONG)))).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    /**
     * this method used to draw a marker using LatLng in List
     * @param data
     */
    private void addMarker(List<Data> data){
        for(Data d : data){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(d.getLatitude()), Double.parseDouble(d.getLongitude()))).title(d.getModified()).snippet("Your Destination")).showInfoWindow();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Utils.saveString(MainActivity.this, Constant.CURRENT_LAT, location.getLatitude() + "");
        Utils.saveString(MainActivity.this, Constant.CURRENT_LONG, location.getLongitude() + "");
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
