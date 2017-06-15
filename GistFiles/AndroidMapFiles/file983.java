package adssgis.adss;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;

public class MapanalyzeActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    LatLng coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapanalyze);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("Type");
        String lat = bundle.getString("Lat");
        String longi = bundle.getString("Long");
        TextView textData = (TextView)findViewById(R.id.textView9);
        TextView textlat = (TextView)findViewById(R.id.textViewlat);
        TextView textlong = (TextView)findViewById(R.id.textViewlong);
        textData.setText(type);
        textlat.setText(lat);
        textlong.setText(longi);

        coordinates = new LatLng(0, 0);

    }

    @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapType(MAP_TYPE_HYBRID);
            mMap.getUiSettings().setRotateGesturesEnabled(false);

            Button button7 = (Button)findViewById(R.id.button7);
            button7.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MapanalyzedisplayActivity.class);
                    TextView type = (TextView)findViewById(R.id.textView9);
                    i.putExtra("Type",type.getText().toString());
                    startActivity(i);

                }
            }
            );
    }
}