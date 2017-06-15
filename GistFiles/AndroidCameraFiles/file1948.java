package com.widetech.mobile.paisaclassic.passenger;

import java.util.List;
import com.cyrilmottier.polaris2.maps.CameraUpdate;
import com.cyrilmottier.polaris2.maps.CameraUpdateFactory;
import com.cyrilmottier.polaris2.maps.GoogleMap;
import com.cyrilmottier.polaris2.maps.SupportMapFragment;
import com.cyrilmottier.polaris2.maps.UiSettings;
import com.cyrilmottier.polaris2.maps.GoogleMap.CancelableCallback;
import com.cyrilmottier.polaris2.maps.GoogleMap.OnMapClickListener;
import com.cyrilmottier.polaris2.maps.model.CameraPosition;
import com.cyrilmottier.polaris2.maps.model.LatLng;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Property;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class MainActivity extends FragmentActivity {

	private SupportMapFragment mMapFragment;
	private FragmentManager fm;
	private GoogleMap mMap;
	private UiSettings mUiSettings;
	private LatLng mLocation;
	private CameraPosition mCameraPosition;
	private boolean isOpenMap = false;
	private View mContent;
	private Interpolator mSmoothInterpolator;

	private static final Property<View, Integer> VIEW_LAYOUT_HEIGHT = new Property<View, Integer>(
			Integer.class, "viewLayoutHeight") {

		@Override
		public void set(View object, Integer value) {
			object.getLayoutParams().height = value.intValue();
			object.requestLayout();
		}

		@Override
		public Integer get(View object) {
			return object.getLayoutParams().height;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLocation = getlocation();
		getActionBar().setIcon(R.drawable.badge_set_icon_default);
		getActionBar().setTitle(R.string.title_main);

		fm = getSupportFragmentManager();
		mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		mContent = findViewById(R.id.info_container);
		if (mMapFragment == null) {
			mMapFragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.mapSpace, mMapFragment).commit();
		}

		findViewById(R.id.bottom).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				trickAnimationCloseMap();
			}
		});
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
		mMap.setMyLocationEnabled(true);
		mMap.setOnMapClickListener(mListenerContainerMap);
		settingsMap();
		if (mLocation != null) {
			mCameraPosition = buildCamera(mLocation);
			changeCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
		}
	}

	private void settingsMap() {
		mUiSettings = mMap.getUiSettings();
		mUiSettings.setAllGesturesEnabled(isOpenMap);
		mUiSettings.setMyLocationButtonEnabled(isOpenMap);
		mUiSettings.setCompassEnabled(isOpenMap);
		mUiSettings.setZoomControlsEnabled(isOpenMap);
	}

	private final OnMapClickListener mListenerContainerMap = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng paramLatLng) {
			trickAnimationOpenMap();
		}
	};

	private void changeCamera(CameraUpdate newCameraPosition) {
		changeCamera(newCameraPosition, null);
	}

	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		mMap.animateCamera(update, callback);
	}

	private CameraPosition buildCamera(LatLng mLocation2) {
		return new CameraPosition.Builder()
				.target(new LatLng(mLocation.latitude, mLocation.longitude))
				.zoom(14.0f).bearing(1.0f).tilt(25).build();
	}

	private LatLng getlocation() throws SecurityException,
			IllegalArgumentException {
		LatLng actualLocation = null;
		Criteria criteria = new Criteria();
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(criteria, true);

		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}
		if (l != null) {
			actualLocation = new LatLng(l.getLatitude(), l.getLongitude());
		}
		return actualLocation;
	}

	private void trickAnimationOpenMap() {
		mMap.setOnMapClickListener(null);
		mSmoothInterpolator = new AccelerateInterpolator();
		ObjectAnimator expandCollapseAnimator = ObjectAnimator.ofInt(mContent,
				VIEW_LAYOUT_HEIGHT, mContent.getHeight(), 0);
		expandCollapseAnimator.setInterpolator(mSmoothInterpolator);
		expandCollapseAnimator
				.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

					public void onAnimationUpdate(ValueAnimator animation) {
					}
				});
		expandCollapseAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				findViewById(R.id.bottom).setVisibility(View.VISIBLE);
				isOpenMap = true;
				settingsMap();
			}
		});
		expandCollapseAnimator.start();
	}

	private void trickAnimationCloseMap() {
		findViewById(R.id.bottom).setVisibility(View.GONE);
		mSmoothInterpolator = new AccelerateInterpolator();
		ObjectAnimator expandCollapseAnimator = ObjectAnimator.ofInt(mContent,
				VIEW_LAYOUT_HEIGHT, mContent.getHeight(), 1000);
		expandCollapseAnimator.setInterpolator(mSmoothInterpolator);
		expandCollapseAnimator
				.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

					public void onAnimationUpdate(ValueAnimator animation) {
					}
				});
		expandCollapseAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mMap.setOnMapClickListener(mListenerContainerMap);
				isOpenMap = false;
				settingsMap();
			}
		});
		expandCollapseAnimator.start();

	}
}
