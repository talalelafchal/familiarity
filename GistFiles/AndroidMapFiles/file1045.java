package com.widetech.mobile.coltaxis.ui.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.cyrilmottier.polaris2.maps.CameraUpdate;
import com.cyrilmottier.polaris2.maps.CameraUpdateFactory;
import com.cyrilmottier.polaris2.maps.GoogleMap;
import com.cyrilmottier.polaris2.maps.GoogleMap.CancelableCallback;
import com.cyrilmottier.polaris2.maps.GoogleMap.OnCameraChangeListener;
import com.cyrilmottier.polaris2.maps.SupportMapFragment;
import com.cyrilmottier.polaris2.maps.UiSettings;
import com.cyrilmottier.polaris2.maps.model.CameraPosition;
import com.cyrilmottier.polaris2.maps.model.LatLng;
import com.cyrilmottier.polaris2.maps.model.VisibleRegion;
import com.widetech.mobile.android.googlemaps.api.SmallAddress;
import com.widetech.mobile.android.task.RequestGeoReverseEncondeTask;
import com.widetech.mobile.android.task.RequestGeoReverseEncondeTask.RequestGeoReverseEncodeResponder;
import com.widetech.mobile.coltaxis.passenger.BaseActivity;
import com.widetech.mobile.coltaxis.passenger.R;
import com.widetech.mobile.coltaxis.passenger.RequestTaxiActivity;
import com.widetech.mobile.coltaxis.util.Utils;
import com.widetech.mobile.mitaxi.log.WidetechLogger;
import com.widetech.mobile.mitaxi.util.Util;

public class MapFragment extends SherlockFragment implements OnCameraChangeListener {

	private GoogleMap mMap;
	private SupportMapFragment mMapFragment;
	private UiSettings mUiSettings;
	private final static float DEFAULT_ZOOM = 17.0f;
	private CameraPosition mCameraPosition;

	public MapFragment {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_fragment,
				container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FragmentManager fm = getChildFragmentManager();
		mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

		if (mMapFragment == null) {
			mMapFragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.content_map, mMapFragment)
					.commit();
		}

		setUpMapIfNeeded();
		
		int arg = getArguments().getInt(BaseActivity.ARG_ITEM_NUMBER);
		String title = getResources().getStringArray(R.array.menu_array)[arg];
		getActivity().setTitle(title);
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = mMapFragment.getPolarisMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mUiSettings = mMap.getUiSettings();
		mUiSettings.setAllGesturesEnabled(true);
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setMyLocationButtonEnabled(false);
		mUiSettings.setZoomControlsEnabled(false);
		mMap.setOnCameraChangeListener(this);
		locateMe(DEFAULT_ZOOM);
	}

	private void locateMe(float zoom) {
		LatLng mLoc = Utils.getlocation(getActivity());
		if (mLoc != null) {
			mCameraPosition = buildCamera(mLoc, zoom);
			changeCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
		}
	}

	private CameraPosition buildCamera(LatLng mLocation, float zoom) {
		return new CameraPosition.Builder()
				.target(new LatLng(mLocation.latitude, mLocation.longitude))
				.zoom(zoom).bearing(45.0f).tilt(25).build();
	}

	private void changeCamera(CameraUpdate update) {
		changeCamera(update, null);
	}

	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		mMap.animateCamera(update, callback);
	}
	
	@Override
	public void onCameraChange(CameraPosition position) {
		centerMarker(position.target);
	}

	private void centerMarker(LatLng target) {
		WidetechLogger.d("lat: " + target.latitude
				+ "lon: " + target.longitude);
		VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
		Point x = mMap.getProjection().toScreenLocation(visibleRegion.farRight);
		Point y = mMap.getProjection().toScreenLocation(visibleRegion.nearLeft);
		Point centerPoint = new Point(x.x / 2, y.y / 2);
		LatLng centerFromPoint = mMap.getProjection().fromScreenLocation(
				centerPoint);
		WidetechLogger.d("center point: " + centerFromPoint.latitude
				+ " " + centerFromPoint.longitude);
	}
}
