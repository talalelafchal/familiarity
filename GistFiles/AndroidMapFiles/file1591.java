package org.example.mapapiv2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
	}
	
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
 
    	private GoogleMap _map;
    	
        public PlaceholderFragment() {
        }
 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
        
        @Override
        public void onStart(){
        	super.onStart();
        	
        	if(_map == null) _map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();        	

        	LatLng latLng = new LatLng(35.689488,139.691706);
        	int zoom    = 16;
        	int tilt    = 0;
        	int bearing = 0;

        	if(_map != null){
        		_map.addMarker(new MarkerOptions().position(latLng).title("TOKYO"));
        		_map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, tilt, bearing)));
        	}
        }
    }
}