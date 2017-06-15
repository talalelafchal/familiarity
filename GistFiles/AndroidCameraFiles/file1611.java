package campusmovil.eafit.edu.co.campusmovil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import campusmovil.eafit.edu.co.campusmovil.models.ModelManager;
import campusmovil.eafit.edu.co.campusmovil.models.Place;

/**
 * Created by alejocram on 23/06/16.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {


    private MapView mapView;
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return mapView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng eafit = new LatLng(6.200696D, -75.578433D);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(eafit));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

        for (Place place : ModelManager.getInstance().places) {
            LatLng geoposition = new LatLng(place.getLatitude(), place.getLongitute());
            mMap.addMarker(new MarkerOptions().title(place.getName()).snippet(place.getDetails()).position(geoposition));
        }
        mMap.setOnInfoWindowClickListener(this);

        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for (int i=0; i<ModelManager.getInstance().places.size(); i++){
            if (marker.getTitle().equals(ModelManager.getInstance().places.get(i).getName())){
                Context context = getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_POSITION, i);
                context.startActivity(intent);
            }
        }
    }
}
