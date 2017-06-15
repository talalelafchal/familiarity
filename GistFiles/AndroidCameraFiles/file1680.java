import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap = null;
    private Marker mMarker;

    private LatLng initialPoint;

    public MapViewFragment(){
        //REQUIRES EMPTY CONSTRUCTOR
    }

    public static MapViewFragment newInstance( /* Add parameters if you need */ ){
        Bundle arguments = new Bundle();
        /*arguments.putSerializable(KEY, value);*/
        MapViewFragment fragment = new MapViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_model_location, container, false);
        setupMapControlButtons(root);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GET VALUES FROM ARGUMENTS.
        /*valueVariable = (String) getArguments().getSerializable(KEY);*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        initialPoint = /* IMPORTANT!! FIND A WAY TO PARSE A LAT LNG POSITION*/

        if(initialPoint != null){
            setupMarker(initialPoint);
            setupMarkerInfoAction();
        }
    }

    private void setupMarkerInfoAction() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                displayOpenOnGoogleMapsDialog();
            }
        });
    }

    private void displayOpenOnGoogleMapsDialog() {
       //IMPORTANT! Display a Dialog to Open on Google Maps.
    }

    private void zoomToCurrentLatLngPosition(LatLng initialPoint) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(initialPoint)
                .zoom(9)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setupMarker(@NonNull LatLng initialPoint) {
        this.mMarker = this.mMap.addMarker(
                new MarkerOptions()
                        .position(initialPoint)
                        .title(yourCustomTitleHere)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_marker_of_your_choice))
        );
        mMarker.showInfoWindow();
        zoomToCurrentLatLngPosition(initialPoint);
    }

    private void setupMapControlButtons(View root) {
        final ImageButton locationButton = (ImageButton) root.findViewById(R.id.location);
        final ImageButton zoomInButton = (ImageButton) root.findViewById(R.id.zoom_in);
        final ImageButton zoomOutButton = (ImageButton) root.findViewById(R.id.zoom_out);
        locationButton.setVisibility(View.VISIBLE);
        zoomInButton.setVisibility(View.VISIBLE);
        zoomOutButton.setVisibility(View.VISIBLE);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(initialPoint != null){
                    zoomToCurrentLatLngPosition(initialPoint);
                }
            }
        });
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

}
