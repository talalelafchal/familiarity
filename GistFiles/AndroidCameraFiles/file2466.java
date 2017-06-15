package com.example.myfirstapp;

import android.os.Bundle;
import android.view.Menu;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
	 /**
	  * @author Sai Valluri
	  * This class is a service that calculates the current coordinates of the user location by using
	  * the LocationListener and communicating with the LocationManager in order to recieve the coordinates and
	  * animate those results in the Google Map
	  */
	public class GPSTracker extends Service implements LocationListener {
	 
	    private final Context mContext;
	    boolean isGPSEnabled = false;
	    boolean isNetworkEnabled = false;
	    boolean canGetLocation = false;
	    Location location; // location
	    double latitude; // latitude
	    double longitude; // longitude
	    
	    // The minimum distance to change Updates in meters
	    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 8;
	    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	    protected LocationManager locationManager;
	    
	    //Constructor
	    public GPSTracker(Context context) {
	        this.mContext = context;
	        getLocation();
	    }
	 
	    /**
	     * This method initializes the LocationManager using a GPS provider and NETWORK provider and 
	     * based on what type of provider is enabled it retrieves the location using the LocationManager
	     * 
	     * @return Location
	     */
	    public Location getLocation() {
	        try {
	            locationManager = (LocationManager) mContext
	                    .getSystemService(LOCATION_SERVICE);
	 
	            isGPSEnabled = locationManager
	                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
	 
	            isNetworkEnabled = locationManager
	                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	 
	            if (!isGPSEnabled && !isNetworkEnabled) {
	                // no network provider is enabled
	            } else {
	                this.canGetLocation = true;
	                if (isNetworkEnabled) {
	                    locationManager.requestLocationUpdates(
	                            LocationManager.NETWORK_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                    if (locationManager != null) {
	                        location = locationManager
	                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                        if (location != null) {
	                            latitude = location.getLatitude();
	                            longitude = location.getLongitude();
	                            Log.d("Coord", "" + longitude);
	                        }
	                    }
	                }
	                if (isGPSEnabled) {
	                    if (location == null) {
	                        locationManager.requestLocationUpdates(
	                                LocationManager.GPS_PROVIDER,
	                                MIN_TIME_BW_UPDATES,
	                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                        Log.d("GPS Enabled", "GPS Enabled");
	                        if (locationManager != null) {
	                            location = locationManager
	                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                            if (location != null) {
	                                latitude = location.getLatitude();
	                                longitude = location.getLongitude();
	                                Log.d("Coord", "" + longitude);
	                            }
	                        }
	                    }
	                }
	            }
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	 
	        return location;
	    }
	    
	    /**
	     * This method retrieves the Latitude of the user's current location 
	     * @return
	     */
	    public double getLatitude(){
	        if(location != null){
	            latitude = location.getLatitude();
	        }
	        return latitude;
	    }
	    
	    /**
	     * This method retrieves the Longitude of the user's current location 
	     * @return
	     */
	    public double getLongitude(){
	        if(location != null){
	            longitude = location.getLongitude();
	        }
	        return longitude;
	    }
	    
	    /**
	     * This method returns true if the location is accessible 
	     * @return
	     */
	    public boolean canGetLocation() {
	        return this.canGetLocation;
	    }
	 
	    /**
	     * This method checks if GPS or NETWORK is enabled, else it shows a dialog which redirects user to GPS
	     * settings
	     * @return
	     */
	    public void showSettingsAlert(){
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	 
	        // Setting Dialog Title
	        alertDialog.setTitle("GPS is settings");
	 
	        // Setting Dialog Message
	        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
	 
	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                mContext.startActivity(intent);
	            }
	        });
	 
	        // on pressing cancel button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            }
	        });
	 
	        // Showing Alert Message
	        alertDialog.show();
	    }
	    
		@Override
		public IBinder onBind(Intent arg0) {
			//Unimplemented
			return null;
		}

		public void onLocationChanged(Location location) {
			//Unimplemented
			
		}

		public void onProviderDisabled(String provider) {
			//Unimplemented
			
		}

		public void onProviderEnabled(String provider) {
			//Unimplemented
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			//Unimplemented
			
		}
	 
	 
	