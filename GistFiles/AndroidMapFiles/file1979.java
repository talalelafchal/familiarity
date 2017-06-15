
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "tag";
    //implements OnMapReadyCallback
    TextView name;
    TextView vicinity;
    TextView welcome;
    private GoogleApiClient mGoogleApiClient;

    final LatLng KHARKOV = new LatLng(49.9944422, 36.2368201);
    ArrayList<String> listForAd = new ArrayList<>();
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.name_id);
        vicinity = (TextView) findViewById(R.id.vicinity_id);
        welcome = (TextView) findViewById(R.id.welcome_id);
        ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(this, R.layout.list_item, listForAd);
        listView.setAdapter(adapter);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
//                            получаю список названий
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));

                    listForAd.add(String.valueOf(placeLikelihood.getPlace().getName()));
                    Log.i(TAG, "123333" + listForAd.size());

                }

                adapter.notifyDataSetChanged();
                likelyPlaces.release();
            }
        });


//        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

//        Retrofit.getBars(new Callback<PlaceResponse>() {
//            @Override
//            public void success(PlaceResponse placeResponse, Response response) {
////                listView.setAdapter(new MyAdapter(MainActivity.this, placeResponse));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();


        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_map_but:
                Intent intent = new Intent(MainActivity.this, ActivityMap.class);
                startActivity(intent);

                return true;
            case R.id.action_listOfMap_but:

                return true;
            case R.id.action_busy_id:

                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    class MyAdapter extends ArrayAdapter<Result> {

        public MyAdapter(Context context, List<Result> objects) {
            super(context, R.layout.list_item, objects);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                holder.textNameOfBar = (TextView) rowView.findViewById(R.id.name_id);
                holder.textVicinity = (TextView) rowView.findViewById(R.id.vicinity_id);

                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            Result result = getItem(position);
            holder.textNameOfBar.setText(result.getName());
            holder.textVicinity.setText(result.getVicinity());


            return rowView;
        }

        class ViewHolder {

            public TextView textNameOfBar;
            public TextView textVicinity;

        }


    }


//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//            Marker marker = googleMap.addMarker(new MarkerOptions().position(KHARKOV).title("Kharkov")); //create marker
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KHARKOV, 15)); // Move the camera instantly to Kharkov with a zoom of 15.
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null); // Zoom in, animating the camera.
//        }


}

