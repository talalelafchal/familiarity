package com.example.nostalgia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.xmlpull.v1.XmlSerializer;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.IBinder;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class ExperienceLoggerService extends Service
/* This is an inner class of our main activity, as an inner class makes good use of resources of outer class */
{
	
	protected final String TAG = null;
	private final IBinder mBinder = new LocalBinder();
	private NotificationManager mNM;
	String exp_name = MainActivity.exp_name;
	
	File file;
	FileOutputStream fOut;
	OutputStreamWriter fWrite;
	MyFileObserver observer;
	
	MyLocationListener locationListener;
	private int lastStatus = 0;
	private static boolean showingDebugToast = false;
	LocationManager lm;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 1 minute
	
	private final DateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    ArrayList<Photo> pList = new ArrayList<Photo>();
    ArrayList<Plot> gList = new ArrayList<Plot>();
	
    Runnable uploader;
    
	/** Called when the activity is first created. */
	private void startLoggerService() 
	{
		try
		{
			Log.d("here", "4");
			this.file = new File("/sdcard/nostalgia/"+exp_name+"/record.txt");
			fOut = new FileOutputStream(file);
			fWrite = new OutputStreamWriter(fOut);
			fWrite.append("This is Nostalgia <br/>");
			//fWrite.append("This is Position Logger <br/>");
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationListener = new MyLocationListener(getApplicationContext());
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
			
		}
		catch(Exception e)
		{
			System.out.println("Error in logging data, blame navjot");
		}
		observer = new MyFileObserver("/sdcard/DCIM/100ANDRO", fWrite);
		observer.startWatching(); // start the observer
	}

	@Override
	public void onCreate()
	{
		Log.i("here", "3");
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		startLoggerService();

		// Display a notification about us starting. We put an icon in the
		// status bar.
		showNotification();
	}
	
	public void onDestroy() 
	{
		try
		{
			//close the file, file o/p stream and out writer.
			fWrite.flush();
			fWrite.close();	
			fOut.close();
			
			photo2Xml();
			plot2Xml();
			/*
			uploader = new Uploader("/sdcard/nostalgia/"+exp_name+"/photo.xml");
			Thread t=new Thread(uploader);
            t.start();
            */
			/***** move all of this to onPause *****
			String fileName = "/sdcard/nostalgia/"+exp_name+"/searialized_photo";
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(pList);
			oos.close();
			fileName = "/sdcard/nostalgia/"+exp_name+"/searialized_gps";
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(gList);
			oos.close();
			*********************/
			
		}
		
		catch(Exception e)
		{
		}
		
		super.onDestroy();
		shutdownLoggerService();
		
		// Cancel the persistent notification.
		mNM.cancel(R.string.local_service_started);
	}
	
	@SuppressWarnings("deprecation")
	private void showNotification() 
	{

		CharSequence text = getText(R.string.local_service_started);

		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_launcher,
				text, System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ExperienceLoggerService.class), 0);

		notification.setLatestEventInfo(this, getText(R.string.service_name),
				text, contentIntent);

		mNM.notify(R.string.local_service_started, notification);
	}
	
	public void photo2Xml()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream("/sdcard/nostalgia/"+exp_name+"/photos.xml");
		    XmlSerializer serializer = Xml.newSerializer();
		    serializer.setOutput(fos, "UTF-8");
		    serializer.startDocument(null, Boolean.valueOf(true));
		    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
	
		    serializer.startTag(null, "Gallery");
		    int size = pList.size();
		    for(int i = 0 ; i< size ; i++)
		    {
	
		        serializer.startTag(null, "file");
		        serializer.attribute(null, "type", "photo");
		        serializer.startTag(null, "source");
		        serializer.text(pList.get(i).uri);
		        serializer.endTag(null, "source");
		        serializer.startTag(null, "lat");
		        serializer.text(pList.get(i).lat);
		        serializer.endTag(null, "lat");
		        serializer.startTag(null, "lon");
		        serializer.text(pList.get(i).lon);
		        serializer.endTag(null, "lon");
		        serializer.startTag(null, "time");
		        serializer.text(timestampFormat.format(pList.get(i).date));
		        serializer.endTag(null, "time");
		        serializer.endTag(null, "file");
		    }
		    
	        serializer.endTag(null, "Gallery");
		    serializer.endDocument();
		    serializer.flush();
		    fos.close();
		}
		catch(Exception anything)
		{
			
		}
	}
	
	public void plot2Xml()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream("/sdcard/nostalgia/"+exp_name+"/plot.xml");
		    XmlSerializer serializer = Xml.newSerializer();
		    serializer.setOutput(fos, "UTF-8");
		    serializer.startDocument(null, Boolean.valueOf(true));
		    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
	
		    serializer.startTag(null, "gList");
		    int size = gList.size();
		    for(int i = 0 ; i< size ; i++)
		    {
	
		        serializer.startTag(null, "plot");
		        serializer.attribute(null, "lat", gList.get(i).lat);
		        serializer.attribute(null, "lon", gList.get(i).lon);
		        serializer.attribute(null, "timestamp", timestampFormat.format(gList.get(i).date));
		        serializer.endTag(null, "plot");
		    }
		    
	        serializer.endTag(null, "gList");
		    serializer.endDocument();
		    serializer.flush();
		    fos.close();
		}
		catch(Exception anything)
		{
			
		}
	}
	
	private void shutdownLoggerService() {
	}
	
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}
	
	public class LocalBinder extends Binder 
	{
		ExperienceLoggerService getService() 
		{
			return ExperienceLoggerService.this;
		}
	}
	
	public class MyLocationListener implements LocationListener
	{
		
		private final Context mContext;
		
		public MyLocationListener(Context context) 
		{
			mContext = context;
		}
		
		@Override
		public void onLocationChanged(Location loc) 
		{
			GregorianCalendar greg = new GregorianCalendar();
			Date time = greg.getTime();
			
			loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Plot p  = new Plot(Double.toString(loc.getLatitude()), Double.toString(loc.getLongitude()), time);
			gList.add(p);
			if (loc != null)
			{
				try 
				{
					fWrite.append("Loc: lat "+loc.getLatitude()+" lon "+ loc.getLatitude() +"<br/>");
				} 
				
				catch (IOException e) 
				{
					
				}
			}
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras)
        {
			String showStatus = null;
			if (status == LocationProvider.AVAILABLE)
				showStatus = "Available";
			if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
				showStatus = "Temporarily Unavailable";
			if (status == LocationProvider.OUT_OF_SERVICE)
				showStatus = "Out of Service";
			if (status != lastStatus && showingDebugToast) 
			{
				Toast.makeText(getBaseContext(),"new status: " + showStatus, Toast.LENGTH_SHORT).show();
			}
			lastStatus = status;
		}
		
		public void onProviderDisabled(String provider) 
		{
			if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
					Toast.LENGTH_SHORT).show();

		}

		public void onProviderEnabled(String provider) 
		{
			if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider,
					Toast.LENGTH_SHORT).show();

		}
		
		public Location getLocation() 
		{
			Location location = null;
			try 
			{
				LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

				// getting GPS status
				boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

				Log.v("isGPSEnabled", "=" + isGPSEnabled);

				// getting network status
				boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

				if (isGPSEnabled == false && isNetworkEnabled == false) 
				{
					// no network provider is enabled
				}
				else 
				{
					boolean canGetLocation = true;
					if (isNetworkEnabled) 
					{
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, this);
						Log.d("Network", "Network");
						if (locationManager != null) 
						{
							location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							if (location != null) 
							{
								return location;
							}
						}
					}
					// if GPS Enabled get lat/long using GPS Services
					if (isGPSEnabled) 
					{
						if (location == null) 
						{
							locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
							Log.d("GPS Enabled", "GPS Enabled");
							if (locationManager != null) 
							{
								location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								
								return location;
								
							}
						}
					}
				}

			}
			catch (Exception e) 
			{
				e.printStackTrace();
				
			}

			return null;
		}
	}
	
	class MyFileObserver extends FileObserver
	{
		OutputStreamWriter fWrite;
		String route;
		public MyFileObserver(String path, OutputStreamWriter fWrite) 
		{
			super(path);
			route = path;
			this.fWrite = fWrite;
		}

		public void onEvent(int event, String file) 
	    {
	    	if(event == FileObserver.CREATE && !file.equals(".probe"))
	    	{ // check if its a "create" and not equal to .probe because thats created every time camera is launched
	    		GregorianCalendar greg = new GregorianCalendar();
				Date time = greg.getTime();
				Location l = locationListener.getLocation();
				Photo p;
				if (l != null)
				{
					p = new Photo("/sdcard/DCIM/100ANDRO/"+file, Double.toString( l.getLatitude()), Double.toString(l.getLongitude()),time);
				}
				else
				{
					p = new Photo("/sdcard/DCIM/100ANDRO/"+file, "", "",time);
				}
	    		pList.add(p);
	    		String fileSaved = "New photo Saved: /sdcard/DCIM/100ANDRO/" + file +"<br/>";
	    		try
	    		{
	    			fWrite.append(fileSaved);
	    		}
	    		catch(Exception e)
	    		{
	    			System.out.println("Problem in writing to file");
	    		}
	    	}
	    }
	}
	
	class Uploader implements Runnable
	{
		public String filepath;
		public Uploader(String path)
		{
			filepath = path;
		}
		public void run()
		{
			try
		    {
		     HttpClient httpclient = new DefaultHttpClient();
		     httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		     HttpPost httppost = new HttpPost("http://10.0.0.2/upload.php");
		     File file = new File(filepath);
		     //Log.i("DBG","Calling Header const");
		     //Header testHeader = new Header();
		     //Log.i("DBG","Passed Header const");
		     MultipartEntity mpEntity = new MultipartEntity();
		     //Log.i("DBG","Passed MPE const");
		     ContentBody cbFile = new FileBody(file, "data/xml");
		     mpEntity.addPart("userfile", cbFile); 
		     httppost.setEntity(mpEntity);
		     System.out.println("executing request " + httppost.getRequestLine());
		     HttpResponse response = httpclient.execute(httppost);
		     HttpEntity resEntity = response.getEntity();
		             // check the response and do what is required
		      }
			catch(Exception e)
			{
				
			}
		}
	}
	
}