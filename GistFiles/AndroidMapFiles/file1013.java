package com.app.flickround.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.conn.DefaultClientConnection;

import com.app.flickround.data.FlickrLocationPreferenceManager;
import com.app.flickround.flickr.FlickrPhotoDetails;
import com.app.flickround.flickr.FlickrPhoto;
import com.app.flickround.flickr.FlickrUtils;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.view.View.OnCreateContextMenuListener;

/*
 * Singleton representing the state of the whole application
 * State data is stored only during the Application runtime
 * Helps in avoiding "long" OnCreates() by avoiding unnecessary intents
 */
public class FlickRoundAppState extends Application{

	private static FlickRoundAppState app_state;
	private static Context app_context;
	private int current_flickr_page; //Current page being read by MODE_FLICKR_PHOTO_SEARCH
	private int total_flickr_pages;
	private ArrayList<FlickrPhoto> flickr_list; 
	private Map<String,FlickrPhotoDetails> flickr_details_list;
	private String current_longitude;
	private String current_latitude;
	private String default_latitude;
	private String default_longitude;
	private String default_per_page;
	private String default_radius;
	private int current_selected_image;
	private static int is_running;
	private DownloadManager download_manager;
	private ArrayList<Long> download_queue; 


	public void refreshAppState(){
		Log.i("APP REFRESH", "Refreshing the App state");
		current_flickr_page = 1;
		current_selected_image = 0;
		flickr_details_list.clear();
		flickr_list.clear();
		total_flickr_pages = 1;
	}
	public void addDownLoadEnqueue(Long enqueue){
		download_queue.add(enqueue);
	}
	public ArrayList<Long> getDownloadQueue(){
		return download_queue;
	}
	public boolean isLastFlickrPage(){
		return current_flickr_page == total_flickr_pages;
	}
	public FlickrPhotoDetails getFlickrPhotoDetails(String key){
		return flickr_details_list.get(key);
	}
	public void setNumFlickrpages(int pages){
		total_flickr_pages = pages;
	}
	public void setCurrentLocation(String longitude, String latitude){
		current_latitude = latitude;
		current_longitude = longitude;
	}
	public String getCurrentLongitude(){
		return current_longitude;
	}
	public String getCurrentLatitude(){
		return current_latitude;
	}
	public static FlickRoundAppState getInstance(){
		if(app_state == null){
			app_state= new FlickRoundAppState();
			app_state.onCreate();
		} 
		//is_running++;
		return app_state;
	}
	public static FlickRoundAppState getInstance(Context ctx){
		if(app_state == null){
			app_state= new FlickRoundAppState();
			app_context = ctx;
			app_state.onCreate();			 
		} 

		is_running++;
		return app_state;
	}

	public void addFlickrList(FlickrPhoto photo){
		flickr_list.add(photo);
	}

	public FlickrPhoto getSelectedPhoto(){
		return flickr_list.get(current_selected_image);
	}
	public void addFlickrDetailList(FlickrPhotoDetails photo){
		flickr_details_list.put(photo.getId(), photo);
	}

	public ArrayList<FlickrPhoto> getFlickrList(){
		return flickr_list;
	}

	public Map<String,FlickrPhotoDetails> getFlickrDetailsList(){
		return flickr_details_list;
	}
	public boolean isPhotoDetailsAvailable(String photoId){
		return flickr_details_list.containsKey(photoId);
	}


	public DownloadManager getDownloadManager(DownloadManager dm){
		if(download_manager == null)
			download_manager = dm;
		return download_manager;
	}
	protected FlickRoundAppState()
	{
		is_running = 0;
	}
	public boolean wasRunning(){
		return is_running > 1;
	}

	@Override
	public void onCreate(){
		super.onCreate();

		Log.i("App state", "onCreate called");
		//app_state = this;
		current_flickr_page = 1;
		current_longitude = "";
		current_latitude = "";
		current_selected_image = -1;

		flickr_list = new ArrayList<FlickrPhoto>();
		flickr_details_list = new HashMap<String,FlickrPhotoDetails>();
		default_per_page = "30";
		getAppDefaults();

	}

	public void getAppDefaults(){
		/*Load defaults from raw/flickr_defaults*/
		String[] defaults = new String[4]; 
		FlickrLocationPreferenceManager pref_manager = new FlickrLocationPreferenceManager(app_context);
		defaults = FlickrUtils.getDefaults(app_context);
		/*Detect if its first Run after install*/
		if(!pref_manager.checkFirstRun()){
		//	Log.i("FIRST_RUN","First run dummy");
			
			String default_longitude = defaults[0];
			String default_latitude = defaults[1];
			String default_radius = defaults[3];

			/*Now save them to sharedpreference*/
			pref_manager.setPrefferedRadius(default_radius);
			pref_manager.setPreviousLocation(default_longitude, default_latitude);
			pref_manager.setActionOnLocationChange(true);
		} 

		current_latitude = default_latitude = pref_manager.getPreviousLatitude();
		current_longitude = default_longitude = pref_manager.getPreviousLongitude();
		default_radius = pref_manager.getPreferredRadius();
		
	}

	public void setSelectedFlickrImage(int position){
		current_selected_image = position;
	}

	public int getSelectedIndex(){
		return current_selected_image;
	}
	public String getDefaultLongitude(){
		return default_longitude;
	}
	public String getDefaultLatitude(){
		return default_latitude;
	}
	public String getDefaultsRadius(){
		return default_radius;
	}
	public String getDefaultPerPage(){
		return default_per_page;
	}
	public Context getApplicationContext(){
		return app_context;
	}
	/*
	@Override
	public void onLowMemory(){

	}
	 */

	public int getCurrentFlickrPage(){
		return current_flickr_page;
	}

	public void incrementFlickrPage(){
		++current_flickr_page;
	}
	public void resetFlickrPage(){
		current_flickr_page = 1;
	}

	public int getNumPhotos()
	{
		return flickr_list.size();
	}
}