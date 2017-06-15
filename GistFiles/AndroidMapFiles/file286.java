package com.service.routefinder;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends BaseActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, LocationSource, OnMarkerClickListener {

	 
	  private GoogleMap mMap;
	  private OnLocationChangedListener mListener;
	  private LocationClient mLocationClient;
	  
	  private static final LocationRequest REQUEST = LocationRequest.create()
	      .setInterval(5000)         // 5 seconds
	      .setFastestInterval(16)    // 16ms = 60fps
	      .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	 	 }
	  
	   @Override
	  protected void onResume() {
	    super.onResume();

	    setUpLocationClientIfNeeded();
	    setUpMapIfNeeded();
	    mLocationClient.connect();
	  }

	  @Override
	  public void onPause() {
	    super.onPause();
	    if (mLocationClient != null) {
	      mLocationClient.disconnect();
	    }
	  }

	  private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	      // Try to obtain the map from the SupportMapFragment
	    	
	      mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)) .getMap();
	      
	      // Check if we were successful in obtaining the map.
	      if (mMap != null) {
	        mMap.setMyLocationEnabled(true);
	        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);	      
	      }
	    }
	  }
	  	
	  private void setUpLocationClientIfNeeded() {
	    if (mLocationClient == null) {
	      mLocationClient = new LocationClient(getApplicationContext(),this,this);// ConnectionCallbacks , OnConnectionFailedListener
	    }
	  }

	  @Override
	  public void onLocationChanged(Location location) {
//	    mMessageView.setText("Location = " + location);
		  if( mListener != null )
		    {
		        mListener.onLocationChanged( location );
		        //Move the camera to the user's location and zoom in!
		        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
		    }
	  }
	  @Override
	  public void onConnected(Bundle connectionHint) {
	    mLocationClient.requestLocationUpdates(
	        REQUEST,
	        this);  // LocationListener
	    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude()), 17.0f));
	    mMap.addMarker(new MarkerOptions()
		.position(new LatLng(mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude()))
		//.position(new LatLng(25.769876,-100.399087))
		.title("Ubicación actual")
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.trans3)));
	  }
	  

	 
	  
	  public void notificacion(String n){
			
			Toast.makeText(getApplicationContext(),n, Toast.LENGTH_SHORT).show();
	  } 
	
	
		   
	


	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
	  // Do nothing
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	  // Do nothing
	}



	}

