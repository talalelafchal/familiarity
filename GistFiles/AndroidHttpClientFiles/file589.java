package com.map.odl_testing;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.model.utility.ConfigurationManager;
import com.model.utility.HttpConnectionUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{

	private GoogleMap googleMap;
	private SharedPreferences pref;
	private JSONArray odls; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        //search button
        findViewById(R.id.search_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent search_view = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(search_view);
			}
		});
        
        //clear cache
        findViewById(R.id.clear_cache).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				findViewById(R.id.search_btn).setEnabled(false);
	    		ConfigurationManager.write_Cache(null);
			}
		});
        
        try {
            // Loading map
            initilizeMap();
            
            //Get ODL data from Server or Cache
            pref = this.getSharedPreferences(
    				ConfigurationManager.mConfigFileName, this.MODE_PRIVATE);
    		ConfigurationManager.setSharedPreference(pref);
    		
            if(ConfigurationManager.read_Cache() == null ){//If there is no data in Cache
            	new GetODL_Data().execute();
            }else{
            	String odls_str = ConfigurationManager.read_Cache();
            	odls = new JSONArray(odls_str);
            	getODL_location(odls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    /**
     * function to load map. If map is not created it will create it for you
     * */
    @SuppressLint("NewApi") 
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            
            //Info Window of Marker
            googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
				
				@Override
				public View getInfoWindow(Marker marker) {
					return null;
				}
				
				@Override
				public View getInfoContents(Marker marker) {
					View v = getLayoutInflater().inflate(R.layout.marker_info_view, null);
					
					TextView odl_name = (TextView)v.findViewById(R.id.odl_name);
					TextView city_name = (TextView)v.findViewById(R.id.city_name);
					TextView country_name = (TextView)v.findViewById(R.id.country_name);
					TextView device_count = (TextView)v.findViewById(R.id.device_count);
					TextView open_device = (TextView)v.findViewById(R.id.open_device);
					RatingBar rating = (RatingBar)v.findViewById(R.id.rating);
					
					
					String id_str = marker.getSnippet();
					int id = Integer.valueOf(id_str);
					
					try {
						JSONObject odl = odls.getJSONObject(id);
						
						odl_name.setText(odl.getString("name"));
						
						JSONObject location_info = odl.getJSONObject("loc");
						
						city_name.setText(location_info.getString("city"));
						country_name.setText(location_info.getString("country"));
						
						device_count.setText(odl.getInt("number_of_devices")+ " device available");
						open_device.setText(odl.getInt("open")+" found this ODL open");
						
						rating.setRating((float)odl.getInt("rating"));
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return v;
				}
			});
            
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 
    /**
     * A function when a view is shown
     */
    @Override
    protected void onResume() {
        super.onResume();

        if(SearchActivity.odl_id != -1){// If ODLs is selected on search view
        	
        	pref = this.getSharedPreferences(
    				ConfigurationManager.mConfigFileName, this.MODE_PRIVATE);
    		ConfigurationManager.setSharedPreference(pref);
    		String odls_str = ConfigurationManager.read_Cache();
        	
    		try {
				odls = new JSONArray(odls_str);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
        	
        	for (int num=0; num<odls.length(); num++){//finding for Selected ODLs at ALL
        		try {
					JSONObject odl = odls.getJSONObject(num);
					if(odl.getInt("id") == SearchActivity.odl_id){
						JSONArray new_odl = new JSONArray();
						new_odl.put(odl);
						
						googleMap.clear();
						initilizeMap();
						odls = new JSONArray();
						odls = new_odl;
						getODL_location(new_odl); // Redraw mark
						return;
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
        }else{
        	initilizeMap();
        }
        
    }
    
    /**
     * 
     * @author PETR
     * Get data from API
     *
     */
    private class GetODL_Data extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			odls = HttpConnectionUtils.getODL_Data();
			return null;
		}
		
		protected void onPostExecute(String result) {
			if(odls != null){
				
				//Get Location of ODLs and Draw mark on Google Map.
				getODL_location(odls);
				
				//write cache
				ConfigurationManager.write_Cache(odls.toString());	    		
			}else{
				Toast.makeText(getApplicationContext(),
                        "Sorry! unable to access sever", Toast.LENGTH_SHORT)
                        .show();
			}
		}
    }
    
    
    /**
     * Get location from data pulled out
     * @param odls
     */
   private void getODL_location(JSONArray odls){
	   int odls_count  = odls.length();
	   
	   String odl_name = "";
	   
	   for (int num = 0 ; num < odls_count ; num ++){
		   try {
			JSONObject odl = odls.getJSONObject(num);
			odl_name = odl.getString("name");
			
			JSONObject location_info = odl.getJSONObject("loc");
			JSONObject latlng_info = location_info.getJSONObject("latlng");
						
			double lat = latlng_info.getDouble("lat");
			double lng = latlng_info.getDouble("lng");
			
			ArrayList<Double> latlng = new ArrayList<Double>();
			latlng.add(lat); latlng.add(lng);
			
			drawMarker(latlng, num);
			
		   } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
                        "Sorry! There is no Location Information of "+odl_name, Toast.LENGTH_SHORT)
                        .show();
		   }
	   }
	   
   }
    
   /**
    * draw marker
    * @param latlng
    * @param id
    */
   private void drawMarker(ArrayList<Double> latlng, int id){
	   
			double lat = latlng.get(0);
			double lng = latlng.get(1);
			
			LatLng position = new LatLng(lat, lng);
			navigateToPosition(position);
			drawOnMap(position ,id);
   }
   
   /**
    * move screen to marker
    * @param position
    */
   private void navigateToPosition(LatLng position) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(position).zoom(0).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}
 
   /**
    * 
    * @param pos
    * @param id
    */
   private void drawOnMap(LatLng pos, int id){
	   Marker marker = googleMap.addMarker(new MarkerOptions()
		.position(pos)
		.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
		.draggable(true));
	   
	   marker.setSnippet(Integer.toString(id));
	   marker.showInfoWindow();
	   marker.hideInfoWindow();
   }
}
