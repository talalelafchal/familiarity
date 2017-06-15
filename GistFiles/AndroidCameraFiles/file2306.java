package gr.atc.radical.Fragments;

import gr.atc.radical.R;
import gr.atc.radical.Model.POI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author lgiampouras
 *
 */
public class Map extends Fragment implements OnMarkerClickListener, OnInfoWindowClickListener,
OnMarkerDragListener {
	
	private MapView mapView;
    private GoogleMap googleMap;
    private Bundle mBundle;
    
    //for storing more info about the marker (for exampe the POI associated with it).
    HashMap<String, POI> extraMarkerInfo = new HashMap<String, POI>();
    
    //Markers array, where markers will be saved
    private List<Marker> markers = new ArrayList<Marker>();
    
    //Pois array, where pois will be saved
    private List<POI> pois = new ArrayList<POI>();
    
    
	 @Override
	 public void onAttach(Activity activity) {
       super.onAttach(activity);
       Log.d("Map Fragment:", "Fragment attached");
	 }
	 
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     mBundle = savedInstanceState;
	 }
	    
	
    @Override
    //Called when the UI is ready to draw the Fragment
    //this method must return a View that is the root of your fragment's layout.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d("Map Fragment:", "Fragment view ready to be drawn");
        
        View inflatedView = inflater.inflate(R.layout.map_fragment, container, false);

        try 
        {
            MapsInitializer.initialize(getActivity());
        } 
        catch (GooglePlayServicesNotAvailableException e) 
        {
            // TODO handle this situation
        }

        mapView = (MapView) inflatedView.findViewById(R.id.map);
        mapView.onCreate(mBundle);
        
        if (googleMap == null) {	
        	googleMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
        }

        return inflatedView;
    }
    
    

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        
        if (googleMap != null) {
            setUpMap();
            fitMapToPins();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
    	mapView.onDestroy();
        super.onDestroy();
    }
    
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        googleMap = null;
    }
    

    private void setUpMap() {
    	
    	//Show current location
        googleMap.setMyLocationEnabled(true);
        
        //Google maps zoom buttons
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        
        //Zoom gestures
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        
        //Don't set a My Location button on Google Maps
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        //Rotate gestures
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        
        //Compass
        googleMap.getUiSettings().setCompassEnabled(true);
        
        //Set marker click listener as this one
        googleMap.setOnMarkerClickListener(this);
        
        //Set annotation view click listener as this one
        googleMap.setOnInfoWindowClickListener(this);
        
        //Populate map with markers
        addMarkersToMap();
    }
    
    
    public void moveCameraToLocation(double lat, double lng) {
    	
    	CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(12).build();
    	googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    
    
    
	private void addMarkersToMap() {
		
		/******************************************
		 TO BE DELETED
		 Normally pois will come from a service or something
		 */
		POI newPOI = new POI();
		newPOI.setLatitude(38.030715);
		newPOI.setLongitude(23.797138);
		newPOI.setAltitude(0.0);
		newPOI.setTitle("ATC");
		newPOI.setSnippet("Employees: 80");
		
		pois.add(newPOI);
		/******************************************/
			
		//Marker to be added to the map
		Marker marker;
		
		for (POI poi : pois)
		{
			marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(poi.getLatitude(), poi.getLongitude())).title(poi.getTitle()).snippet(poi.getSnippet()));
	    	
	    	// create custom marker
//	    	MarkerOptions marker = new MarkerOptions().position(new LatLng(poi.getLatitude(), poi.getLongitude())).title(poi.getTitle()).snippet(poi.getSnippet());
//	    	marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));
//	    	googleMap.addMarker(marker);
				
			markers.add(marker);
			
			//Add the poi as extra info on a hashmap for the specific marker
			extraMarkerInfo.put(marker.getId(), poi);
		}
	}

	
	
	@Override
	public boolean onMarkerClick(final Marker marker) {
		//This will fire when you click the marker (not the annotation view)
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		//This will fire when you click the annotation view
		
		POI poi = extraMarkerInfo.get(marker.getId());
		
		Toast.makeText(getActivity(), poi.getTitle(), Toast.LENGTH_SHORT).show();
		marker.showInfoWindow();
	}

	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {

	}

	@Override
	public void onMarkerDrag(Marker marker) {

	}
    
    
    public void fitMapToPins() {
    	
		if (mapView.getViewTreeObserver().isAlive()) {
			
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						
						@SuppressWarnings("deprecation")
						@SuppressLint("NewApi")
						
						// We check which build version we are using.
						@Override
						public void onGlobalLayout() {

							LatLngBounds.Builder bc = new LatLngBounds.Builder();

							for (Marker item : markers) {
								bc.include(item.getPosition());
							}

							if (markers.size() > 0) {
								LatLngBounds bounds = bc.build();
							
								if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
									mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
								} 
								else {
									mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
								}
								
								googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
							}
						}
					});
		}
    }
    
}
