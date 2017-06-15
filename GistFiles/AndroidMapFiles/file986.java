package id.firman.app.wisata;

/**
 * Created by Firman on 5/23/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import id.firman.app.wisata.databases.Location;


public class AddRouteActivity extends BaseActivity implements GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public static final int ADD_MAP_KEY = 1;

    @InjectView(R.id.etFrom)
    EditText etFrom;
    @InjectView(R.id.spinnerDestination)
    Spinner spDestination;

    private ArrayAdapter<Location> locationArrayAdapter;
    private List<Location> locations;
    private double lat, lng;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private android.location.Location location;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        ButterKnife.inject(this);

        getSupportActionBar().setTitle("Rute Wisata");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        locations = new ArrayList<>();
        generateLocationDummy();
        locationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations);
        spDestination.setAdapter(locationArrayAdapter);

        setUpMapIfNeeded();


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this) // connection callback
                .addOnConnectionFailedListener(this) // when connection failed
                .addApi(LocationServices.API) // called api
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .title("Lokasi awal")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
        );
        lat = latLng.latitude;
        lng = latLng.longitude;

        Bundle data = new Bundle();
        data.putDouble("lat", lat);
        data.putDouble("lng", lng);
        Intent resultIntent = new Intent();
        resultIntent.putExtras(data);
        setResult(RESULT_OK, resultIntent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (location == null) {
            // get last location device
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("Lokasi awal")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                );
                lat = location.getLatitude();
                lng = location.getLongitude();

            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private void generateLocationDummy() {
        locations.add(new Location("Istana Maimun", 3.575211, 98.683716));
        locations.add(new Location("Air Terjun Sipiso-piso", 2.916060, 98.522249));
        locations.add(new Location("Masjid Raya Medan", 3.575090, 98.687246));
        locations.add(new Location("Danau Toba", 2.804936, 98.496713));
        locations.add(new Location("Pengangkaran Buaya Asam Kumbang", 3.566447, 98.619549));
        locations.add(new Location("Danau Linting", 3.229641, 98.725797));
        locations.add(new Location("Salju Panas Dolok Tinggi Raja", 2.835352, 99.547868));
        locations.add(new Location("Aek Sijornih", 1.187119, 99.391585));
        locations.add(new Location("Hillpark Sibolangit", 3.280059, 98.556396));
        locations.add(new Location("Tangkahan", 3.675673, 98.705891));
        locations.add(new Location("Taman Nasional Gunung Leuser", 3.907085, 97.403467));
        locations.add(new Location("Danau Lau Kawar", 3.101963, 98.507806));
        locations.add(new Location("Cagar Alam Sibolangit", 3.581138, 98.690722));
        locations.add(new Location("Gunung Sibayak", 3.240668, 98.503693));
        locations.add(new Location("Rahmat International Wildlife Museum & Gallery", 3.579174, 98.667548));
        locations.add(new Location("Air Terjun Telaga Dwi Warna Sibolangit", 3.581126, 98.690728));
        locations.add(new Location("Bukit Lawang", 3.555512, 98.144613));
        locations.add(new Location("Rumah Tjong A Fie", 3.585407, 98.680246));
        locations.add(new Location("Menara Air Tirtanadi", 3.582430, 98.684960));
        locations.add(new Location("Pulau Samosir", 2.629181, 98.792150));
        locations.add(new Location("Bukit Gundaling", 3.192726, 98.501250));
        locations.add(new Location("Danau Siombak", 3.726690, 98.659821));
        locations.add(new Location("Air Terjun Sigura-gura", 2.554584, 99.305085));
    }



    @OnClick(R.id.btnAddMap)
    public void bntAddMap() {
        startActivityForResult(new Intent(this, id.firman.app.wisata.AddMapActivity.class), ADD_MAP_KEY);
    }
    @OnClick(R.id.mps)
    public void mps() {
        startActivityForResult(new Intent(this, id.firman.app.wisata.AddMapActivity.class), ADD_MAP_KEY);
    }
    @OnClick(R.id.etFrom)
    public void etFrom() {
        startActivityForResult(new Intent(this, id.firman.app.wisata.AddMapActivity.class), ADD_MAP_KEY);
    }


    @OnClick(R.id.btnGetRoute)
    public void getRoute() {
        if (!TextUtils.isEmpty(etFrom.getText())) {
            Location loc = locations.get(spDestination.getSelectedItemPosition());
            Bundle data = new Bundle();
            data.putDouble("latAwal", lat);
            data.putDouble("lngAwal", lng);
            data.putDouble("latTujuan", loc.getLat());
            data.putDouble("lngTujuan", loc.getLng());
            data.putInt("status", History.FROM_NET);
            data.putString("tujuan", loc.getName());

            startActivity(new Intent(this, id.firman.app.wisata.ViewRouteActivity.class).putExtras(data));
        } else {
            Toast.makeText(this, "Tentukan lokasi anda!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MAP_KEY) {
            if (resultCode == RESULT_OK) {
                lat = data.getExtras().getDouble("lat");
                lng = data.getExtras().getDouble("lng");
                etFrom.setText("" + lat + "," + lng);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
