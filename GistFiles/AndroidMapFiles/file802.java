package com.example.nayra.mapas;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private Realm realm;

    private LinearLayout bottomSheet;

    private TextView nombreid;
    private TextView direccionid;
    private TextView telefonoTextView;
    private TextView tipoTextView;
    private TextView nivelTextView;
    private TextView zonaTextView;
    private ArrayList<String> ids;

    public LocationManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        realm = Realm.getDefaultInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        System.out.println("*****hola createeee");
        ids = new ArrayList<String>();

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        LatLng bolivia=new LatLng(-16.499 ,-68.10);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bolivia,6));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);


        final RealmResults<Lugar> result;

        if (getIntent().getExtras() == null) {
            result = realm.where(Lugar.class).findAll();

        } else {
            String nombre = getIntent().getStringExtra("idlugar");
            result = realm.where(Lugar.class).equalTo("nombre", nombre).findAll();
            System.out.println("<<<< NOMBRE" + nombre);
        }
        System.out.println("*****4");

        //RealmResults<Lugar> results = realm.where(Lugar.class).equalTo("tipo", "HOS").findAll();

        System.out.println("**** size Mostrar" + result.size());

        bottomSheet = (LinearLayout) findViewById(R.id.bottomSheet);

        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

        nombreid = (TextView) findViewById(R.id.nombreid);
        direccionid = (TextView) findViewById(R.id.direccionid);
        telefonoTextView = (TextView) findViewById(R.id.telefonoTextView);
        tipoTextView = (TextView) findViewById(R.id.tipoTextView);
        nivelTextView = (TextView) findViewById(R.id.nivelTextView);
        zonaTextView = (TextView) findViewById(R.id.zonaid);


        for (int i = 0; i < result.size(); i++) {

            Lugar l = result.get(i);

            //float hue;
//            if (l.getNivel()== "1er NIVEL"){
  //              hue = BitmapDescriptorFactory.HUE_BLUE;
      //      } else {
        //        hue = BitmapDescriptorFactory.HUE_BLUE;
           // }


            System.out.println(">>>>>" + l.getNombre() + " " + l.getTipo());

            LatLng sydney = new LatLng(l.getLatitud(), l.getLongitud());

                MarkerOptions markerOptions = new MarkerOptions().position(sydney)
                        .title(l.getNombre())
                        .snippet(l.getDireccion())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_marcador_rojo)

                        );
                Marker marker = mMap.addMarker(markerOptions);
                ids.add(marker.getId());
            System.out.println(">>>>>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!tipo" + l.getNivel()+ l.getNombre());

                System.out.println(">>>>>" + l.getNivel());

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    System.out.println("se presiono el globo");

                    bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                    String titulo = marker.getTitle();
                    nombreid.setText(titulo);
                    String titulo1 = marker.getSnippet();
                    direccionid.setText(titulo1);

                    Lugar l = null;
                    for (int i = 0; i < ids.size(); i++) {
                        if (marker.getId().equals(ids.get(i))) {
                            l = result.get(i);
                            break;
                        }
                    }
                    // en l esta toda info
                    System.out.println(l.getTelefono());
                    System.out.println(l.getTipo());

                    telefonoTextView.setText(l.getTelefono());
                    tipoTextView.setText(l.getTipo());
                    nivelTextView.setText(l.getNivel());
                    zonaTextView.setText(l.getZona());
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    System.out.println("se presiono el marcador");
                    String titulo = marker.getTitle();
                    nombreid.setText(titulo);
                    return false;
                }
            });
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }



    }



    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    public void abrirBuscador(View view) {
        Intent intent = new Intent(this, BuscadorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        lm.getAllProviders();
        location.getLatitude();
        location.getLongitude();
    }

}