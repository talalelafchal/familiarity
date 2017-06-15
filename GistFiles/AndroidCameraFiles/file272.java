package in.trailblaze.android.activities;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;
import java.util.List;

import in.trailblaze.android.R;
import in.trailblaze.android.TrailBlaze;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

	private GoogleMap mMap;
	private List<LatLng> points = new LinkedList<>();

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finishAffinity();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setPopupTheme(R.style.AppTheme_Light_PopupOverlay);
			toolbar.setTitle("Map");
		}

		setSupportActionBar(toolbar);

		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		final ImageView callButton = (ImageView) findViewById(R.id.callButton);
		final ImageView smsButton = (ImageView) findViewById(R.id.smsButton);
		final String phoneNumber = "";

		callButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					String[] permissions = new String[]{
							Manifest.permission.CALL_PHONE,
					};
					// Request permission in Marshmallow style
					ActivityCompat.requestPermissions(MapsActivity.this, permissions, TrailBlaze.REQUEST_CALL_PHONE);
					return;
				} else {
					final Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + phoneNumber));
					startActivity(callIntent);
				}
			}
		});

		smsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == android.R.id.home) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finishAffinity();
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider overriding
			// public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
			// to handle the case where the user grants the permission.

			// Permissions strings to access user's location
			String[] permissions = new String[]{
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION
			};
			// Request permission in Marshmallow style
			ActivityCompat.requestPermissions(this, permissions, 1);
			return;
		} else {
			mMap.setBuildingsEnabled(true);
			mMap.setIndoorEnabled(true);
			mMap.setTrafficEnabled(true);
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			mMap.setMyLocationEnabled(true);
			mMap.setOnMyLocationChangeListener(this);
			mMap.getMyLocation();
		}
		mMap.setMyLocationEnabled(true);
	}

	@Override
	public void onMyLocationChange(final Location location) {
		final LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

		// Add visited point to list
		points.add(currentLocation);

		// Remove all previous markers
		mMap.clear();
		// Redraw first & last marker
		mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
		mMap.addMarker(new MarkerOptions().position(points.get(0)).title("You started here"));

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

		final Polyline polyline = mMap.addPolyline(new PolylineOptions().add(currentLocation));
		polyline.setWidth(5);
		polyline.setColor(getResources().getColor(R.color.green));
		// Redraw polyline with all visited points
		polyline.setPoints(points);
	}
}