import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapsView {

    private GoogleMap mMap;
    private MapsPresenter presenter;
    private Marker currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        presenter = new MapsPresenterImpl(this, this, this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        currentPosition = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        presenter.onMapReady();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void generateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }


    @Override
    public void updateLocationOnMap(Location location) {
        currentPosition.remove();
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        currentPosition = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    @Override
    public void showGeofences(List<CompanyLocation> companyLocationList) {
        for(CompanyLocation companyLocation : companyLocationList) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(companyLocation.getCoordinates())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            CircleOptions circleOptions = new CircleOptions()
                    .center(companyLocation.getCoordinates())
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius( 100.0f );
            mMap.addCircle( circleOptions );
            mMap.addMarker(markerOptions);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.disconnectFromLocationService();
    }
}
