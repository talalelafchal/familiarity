package biz.mindforth.checkincmu;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tyczj.mapnavigator.Directions;
import com.tyczj.mapnavigator.Navigator;
import com.tyczj.mapnavigator.Navigator.OnPathSetListener;
import com.tyczj.mapnavigator.Route;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements
		OnMarkerClickListener, OnPathSetListener, LocationListener {

	private MapView mMapView;
	private GoogleMap mMap;
	private Bundle mBundle;

	private Location location;
	private LocationManager mLocationManager;

	private LatLng myLocation;
	private LatLng Destination;
	private double longitude;
	private double latitude;

	private Route route;
	private Navigator nav;

	static final LatLng CMU = new LatLng(18.796494, 98.957654);

	private Marker cmu;
	private Marker eng_cmu;
	private Marker std_cmu;
	private Marker lib_cmu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inflatedView = inflater.inflate(R.layout.fragment_community,
				container, false);

		MapsInitializer.initialize(getActivity());

		mMapView = (MapView) inflatedView.findViewById(R.id.map);
		mMapView.onCreate(mBundle);
		
		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		myLocation = new LatLng(latitude, longitude);
		
		setUpMapIfNeeded(inflatedView);

		return inflatedView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBundle = savedInstanceState;
	}

	private void setUpMapIfNeeded(View inflatedView) {
		if (mMap == null) {
			mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
		// Move the camera instantly to CMU with a zoom of 15.
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CMU, 10));
		// Zoom in, animating the camera.
		mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

	}

	private void setUpMap() {

		mMap = mMapView.getMap();

		// Set My Current Location
		mMap.setMyLocationEnabled(true);

		// Set Current Location Button
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

		mMap.setOnMarkerClickListener(this);
		// Add Marker On Map
		cmu = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(18.796494, 98.957654))
				.title("มหาวิทยาลัยเชียงใหม่")
				.snippet("Chiang Mai University")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_launcher)));
		eng_cmu = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(18.795387, 98.951646))
				.title("คณะวิศวกรรมศาสตร์ มหาวิทยาลัยเชียงใหม่")
				.snippet("Faculty of Engineering, Chiang Mai University"));
		std_cmu = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(18.804009, 98.955444))
				.title("กองพัฒนานักศึกษา มหาวิทยาลัยเชียงใหม่")
				.snippet("Student Development Division, CMU"));
		lib_cmu = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(18.801196, 98.951914))
				.title("หอสมุด มหาวิทยาลัยเชียงใหม่")
				.snippet("Main Library, CMU"));
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		if (marker.equals(cmu)) {
			// handle click here
			mMap.clear();
			setUpMap();
			Destination = new LatLng(18.796494, 98.957654);
			
			// Navigator map
			nav = new Navigator(mMap, myLocation, Destination);
			nav.findDirections(true);
			nav.setOnPathSetListener(this);

			return true;
		} else if (marker.equals(eng_cmu)) {
			// handle click here
			mMap.clear();
			setUpMap();
			return true;
		}
		return false;
	}

	@Override
	public void onPathSetListener(Directions directions) {
		// TODO Auto-generated method stub
		route = directions.getRoutes().get(0);

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
}
