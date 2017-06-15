/*
 * Copyright (C) 2010, 2011, 2012 Herbert von Broeuschmeul
 * Copyright (C) 2010, 2011, 2012 BluetoothGPS4Droid Project
 * 
 * This file is part of BluetoothGPS4Droid.
 *
 * BluetoothGPS4Droid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BluetoothGPS4Droid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with BluetoothGPS4Droid. If not, see <http://www.gnu.org/licenses/>.
 */

package org.broeuschmeul.android.gps.bluetooth.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.os.SystemClock;
import android.util.Log;

/**
 * This class is used to provide Mock GPS provider for clients.
 * 
 * @author Herbert von Broeuschmeul
 *
 */
public class BluetoothGpsMockProvider {

	/**
	 * Tag used for log messages
	 */
	private static final String LOG_TAG = "BlueGPS";

 
	private LocationManager lm;
	private SharedPreferences sharedPreferences;
	private Context appContext;
	private boolean mockGpsAutoEnabled = false;
	private boolean mockGpsEnabled = false;
	private String mockLocationProvider = null;
	

	public BluetoothGpsMockProvider() {
		this.appContext = callingService.getApplicationContext();
		lm = (LocationManager)callingService.getSystemService(Context.LOCATION_SERVICE);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(callingService);
	}

	/**
	 * Enables the Mock GPS Location Provider used for the bluetooth GPS.
	 * 
	 * @see NmeaParser#enableMockLocationProvider(java.lang.String)
	 */
	public void enableMockLocationProvider(){
	    Log.d(LOG_TAG, "enabling mock locations provider.");
	    boolean force = sharedPreferences.getBoolean(BluetoothGpsProviderService.PREF_FORCE_ENABLE_PROVIDER, false);
		enableMockLocationProvider(force);
	}

	public void enableMockLocationProvider(boolean force){
		try {
			LocationProvider prov;
			disableMockLocationProvider();
			try {
				lm.removeTestProvider(mockLocationProvider);
			} catch (IllegalArgumentException e){
				Log.d(LOG_TAG, "unable to remove current provider Mock gps provider.");
			}
		}

			prov = lm.getProvider(mockLocationProvider);
			lm.addTestProvider(mockLocationProvider, false, true,false, false, true, true, true, Criteria.POWER_MEDIUM, Criteria.ACCURACY_FINE);
			if ( force || (prov == null)){
				Log.d(LOG_TAG, "enabling Mock provider: "+mockLocationProvider);
				lm.setTestProviderEnabled(mockLocationProvider, true);
				mockGpsAutoEnabled = true;
			}
			mockGpsEnabled = true;
		} catch (SecurityException e){
			Log.e(LOG_TAG, "Error while enabling Mock Mocations Provider", e);
			disableMockLocationProvider();
		}
	}

	public void disableMockLocationProvider(){
		try {
			LocationProvider prov;
			if (mockLocationProvider != null && mockLocationProvider != "" && mockGpsEnabled){
				prov = lm.getProvider(mockLocationProvider);
				if (prov != null){
					Log.v(LOG_TAG, "Mock provider: "+prov.getName()+" "+prov.getPowerRequirement()+" "+prov.getAccuracy()+" "+lm.isProviderEnabled(mockLocationProvider));
				}
				mockGpsEnabled = false;
				if ( mockGpsAutoEnabled )  { 
					Log.d(LOG_TAG, "disabling Mock provider: "+mockLocationProvider);
					lm.setTestProviderEnabled(mockLocationProvider, false);
				}
				prov = lm.getProvider(mockLocationProvider);
				if (prov != null){
					Log.v(LOG_TAG, "Mock provider: "+prov.getName()+" "+prov.getPowerRequirement()+" "+prov.getAccuracy()+" "+lm.isProviderEnabled(mockLocationProvider));
				}
				lm.clearTestProviderEnabled(mockLocationProvider);
				prov = lm.getProvider(mockLocationProvider);
				if (prov != null){
					Log.v(LOG_TAG, "Mock provider: "+prov.getName()+" "+prov.getPowerRequirement()+" "+prov.getAccuracy()+" "+lm.isProviderEnabled(mockLocationProvider));
				}
				lm.clearTestProviderStatus(mockLocationProvider);
				lm.removeTestProvider(mockLocationProvider);
				prov = lm.getProvider(mockLocationProvider);
				if (prov != null){
					Log.v(LOG_TAG, "Mock provider: "+prov.getName()+" "+prov.getPowerRequirement()+" "+prov.getAccuracy()+" "+lm.isProviderEnabled(mockLocationProvider));
				}
				Log.d(LOG_TAG, "removed mock GPS");
			} else {
				Log.d(LOG_TAG, "Mock provider already disabled: "+mockLocationProvider);			
			}
		} catch (SecurityException e){
			Log.e(LOG_TAG, "Error while enabling Mock Mocations Provider", e);
		} finally {
			mockLocationProvider = null;
			mockGpsEnabled = false;
			mockGpsAutoEnabled = false;
			mockStatus = LocationProvider.OUT_OF_SERVICE;
		}
	}

	/**
	 * @return the mockGpsEnabled
	 */
	public boolean isMockGpsEnabled() {
		return mockGpsEnabled;
	}
	
	public void setMockLocationProviderOutOfService(){
		notifyStatusChanged(LocationProvider.OUT_OF_SERVICE, null, System.currentTimeMillis());
	}

	/**
	 * @return the mockLocationProvider
	 */
	public String getMockLocationProvider() {
		return mockLocationProvider;
	}

	public void notifyFix(Location fix) throws SecurityException {
		if (fix != null){
			Log.v(LOG_TAG, "New Fix: "+System.currentTimeMillis()+" "+fix);
			if (lm != null && mockGpsEnabled){
				lm.setTestProviderLocation(mockLocationProvider, fix);
				Log.v(LOG_TAG, "New Fix notified to Location Manager: "+mockLocationProvider);
			}
		}
	}

	public void notifyStatusChanged(int status, Bundle extras, long updateTime){
		Log.d(LOG_TAG, "New mockStatus: "+System.currentTimeMillis()+" "+status);
		if (lm != null && mockGpsEnabled){
			lm.setTestProviderStatus(mockLocationProvider, status, extras, updateTime);
			Log.v(LOG_TAG, "New mockStatus notified to Location Manager: " + status + " "+mockLocationProvider);
		}
	}
}