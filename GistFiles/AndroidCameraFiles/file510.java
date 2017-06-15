package project.thibaulpoignonec.selectri;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.w3c.dom.Document;

import java.util.Vector;

import Cartographie.Position;
import DataBase.CompilateurXML;
import DataBase.DataBaseXml;
import PointDeCollecte.*;

/*
 * Affiche une carte puis ajoute des bacs dessus (Bitmap) en fonction du type passé au
 * constructeur.
 *
 * Peux renvoyer vers un itineraire
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {//}, LocationListener {
    private LocationManager locationManager;


    public GoogleMap mMap;
    private String typeDebac;
    protected DataBaseXml dataBaseXml;
    private boolean isMarked;
    private BitmapDescriptor myPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        typeDebac = bundle.getString("TYPE");
        myPosition = BitmapDescriptorFactory.fromResource(R.drawable.ma_position);
        isMarked = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // TODO : faire un toast...
        }

        showMap(typeDebac);
        Log.d("showMap()", "fini !");

        setRadius();

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                setRadius();
                return true;
            }
        });


    }

    public void setRadius() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location myLocation = null;
        if (locationManager != null) {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            // Récuperer la position du telephone

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
            }
            else myLocation = locationManager.getLastKnownLocation(provider);
        }
        double latitude;
        double longitude;
        if(myLocation != null)
        {
            // latitude de la position actuelle
            latitude= myLocation.getLatitude();
            // longitude de la position actuelle
            longitude= myLocation.getLongitude();
            // Create a LatLng object for the current location
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Veuillez activer la localisation et la data",Toast.LENGTH_SHORT).show();
            latitude = 48.363480;
            longitude = -4.566542;
        }

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Vous êtes ici !")
                .icon(myPosition));
        // Show the current location in Google Map

        int radius = dataBaseXml.getRadius(new Position(latitude,longitude),1);

        // Zoom dans Google Map
        setZoom(radius,latLng);
    }
    private void setZoom(int radius, LatLng point)
    {

        int mapHeightInDP = 200;
        Resources r = getResources();
        int mapSideInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mapHeightInDP, r.getDisplayMetrics());

        LatLngBounds latLngBounds = calculateBounds(point, radius);
        if(latLngBounds != null){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, mapSideInPixels, mapSideInPixels, 1);
            if(mMap != null)
                mMap.animateCamera(cameraUpdate);
        }
    }

    private LatLngBounds calculateBounds(LatLng center, double radius) {
        return new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(center, radius, 0)).
                include(SphericalUtil.computeOffset(center, radius, 90)).
                include(SphericalUtil.computeOffset(center, radius, 180)).
                include(SphericalUtil.computeOffset(center, radius, 270)).build();
    }


    public void drawBacs(String type)
    {
        CompilateurXML compilateurXML = new CompilateurXML(this);
        Document bdd = null;
        try {
        if(type.equals("dmr") | type.equals("recyclable") | type.equals("verre"))
             bdd = compilateurXML.getBddInXml(getResources().openRawResource(R.raw.bdd_brute_pays_de_brest));
        else{
            if(type.equals("textile"))
                    bdd = compilateurXML.loadXMLFromString(getResources().openRawResource(R.raw.bdd_brest_textile));
            else{
                if(type.equals("pile"))
                    bdd = compilateurXML.loadXMLFromString(getResources().openRawResource(R.raw.bdd_brest_pile));
                else{
                    if(type.equals("decheterie"))
                        bdd = compilateurXML.loadXMLFromString(getResources().openRawResource(R.raw.bdd_brest_decheterie));
                }
            }
        }
        } catch (Exception e) {e.printStackTrace();}

        Log.d("CARTE", "DOM bdd crée");
        Log.d("CARTE", CompilateurXML.toString(bdd));


        dataBaseXml = new DataBaseXml(this,bdd, "brest");
        Log.d("CARTE", "bdd crée");
        if(isMarked == false) {isMarked = true; drawMarkers(dataBaseXml.getBacs(typeDebac), typeDebac); }

    }

    public void showMap(String type) {
        drawBacs(type);
    }

    public boolean drawMarkers(Vector<PointDeCollecte> pdts, String type) {
        BitmapDescriptor icon = pdts.firstElement().addMarker(this);

        for (PointDeCollecte pdt : pdts) {
            Log.d("CARTE : drawMarkers())", "AJOUT PUSH-PIN");
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(pdt.getPosition().getLongitude(), pdt.getPosition().getLatitude()))
                    .title(type)
                    .icon(icon));
        }
        return true;
    }

}
