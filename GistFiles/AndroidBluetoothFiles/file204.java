package service;

//package service;

//import java.util.Iterator;
//import java.util.List;

import linz.jku.GetMp3List;
import linz.jku.Mp3Data;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

//import android.app.ActivityManager;
//import android.app.ActivityManager.RunningAppProcessInfo;
//import android.app.ActivityManager.RunningServiceInfo;
//import android.app.Service;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * ShakeContact Service. Runs on background and listens to Accelerator changes
 * in order to launch ShakeContact. It has implemented methods to save energy in
 * case of phone idle.
 * 
 * @author Diego Mu�oz Callejo
 * 
 */
public class ShakeContactService extends Service implements
		SensorEventListener,
		org.openintents.sensorsimulator.hardware.SensorEventListener {

	/*
	 * Current track detection
	 */
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	public String artist;
	public String album;
	String track;
	private static List<Mp3Data> songList;

	
	/**
	 * Broadcast receiver to set the current track and artist played in MediaPlayer.
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);

			// We have to save this information, when the mobile is being
			// shaked, we have to get the file from the hard drive -something
			// that remains to do .
			artist = intent.getStringExtra("artist");
			album = intent.getStringExtra("album");
			track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);
		}
	};

	/*
	 * Current track detection
	 */
	private static Context CONTEXT;
	private long lastUpdate; // sensor last time update.
	private SensorManagerSimulator sensorManagerSim; // Only for testing.
	private SensorManager sensorManager; // accelerometer Manager.
	private boolean isSimulator; // defines if current device is a simulator o a
									// real device.
	private LockScreenReceiver powerSave; // Intent receiver for power save.
	private static final String TAG = "ShakeContactService";

	// Used to know which app is running in foreground
	ActivityManager mActivityManager = null;

	Cursor musiccursor;
	int music_column_index;
	int count;
	MediaPlayer mMediaPlayer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "Service Created");
		CONTEXT = this;
		this.isSimulator = isSimulator(); // Determines if system is a simulator
											// or a real device.
		/*
		 * Initializes LockScreenReceiver to save battery when phone is idle.
		 */
		this.powerSave = new LockScreenReceiver(this);
		IntentFilter filterAUP = new IntentFilter(Intent.ACTION_USER_PRESENT);
		IntentFilter filterScreenOff = new IntentFilter(
				Intent.ACTION_SCREEN_OFF);
		registerReceiver(powerSave, filterAUP); // If using SCREEN_ON it will
												// turn on when user press power
												// button.
		registerReceiver(powerSave, filterScreenOff);
		/*
		 * Initializes Accelerator Managers for real device or simulator.
		 */
		if (isSimulator) {
			Log.d(TAG, "Running in simulator mode");
			sensorManagerSim = (SensorManagerSimulator) SensorManagerSimulator
					.getSystemService(this, SENSOR_SERVICE);
			sensorManagerSim.connectSimulator();
		} else {
			Log.d(TAG, "Running in real device mode");
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		}

		
		//We register the filter to get MediaPlayer state changed.
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");

		registerReceiver(mReceiver, iF);

		// Implemented for mp3 support. NOT SUPPORTED YET
		/*if (ShakeContactService.songList == null) {
			getMp3List();
		}*/

	}

	/** Import all mp3 data in the device
	 * 
	 */
	private void getMp3List() {

		// AsyncTask to perform the task
		new GetMp3List().execute(CONTEXT);
	}

	public static void setMp3List(List<Mp3Data> list) {
		ShakeContactService.songList = list;
	}

	/**
	 * We destroy the service unregistering listeners.
	 */
	@Override
	public void onDestroy() {
		unregisterListener();
		unregisterReceiver(powerSave); // Unregisters BroadcastReceiver.
		unregisterReceiver(mReceiver);
		Log.i(TAG, "Service destroyed");
	}

	/**
	 * When the service is started we register our listeners.
	 */
	@Override
	public void onStart(Intent intent, int startid) {
		registerListener();
		Log.i(TAG, "Service started");
	}

	/**
	 * Pauses accelerometer service (useful to save energy).
	 */
	public void onPause() {
		/*
		 * Unregisters Accelerator listeners.
		 */
		unregisterListener();
		Log.i(TAG, "Service paused");
	}

	/**
	 * Returns current context.
	 * 
	 * @return context.
	 */
	public static Context getContext() {
		return CONTEXT;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * When the virtual sensor changes, we get the values to launch or not Shaking activity.
	 */
	public void onSensorChanged(SensorEvent event) {
		if (event.type == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
			// Movement
			float x = 1;
			float y = values[1];
			float z = 1;

			float accelationSquareRoot = (x * x + y * y + z * z)
					/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
			long actualTime = System.currentTimeMillis();
			if (accelationSquareRoot >= 2) //
			{
				if (actualTime - lastUpdate < 200) {
					return;
				}
				lastUpdate = actualTime;
				onShuff();
			}

		}

	}
	
	
	/**
	 * When the real sensor changes, we get the values to launch or not Shaking activity.
	 */
	public void onSensorChanged(android.hardware.SensorEvent event) {

		float[] values = event.values;
		// Movement
		float x = 1;
		float y = values[1];
		float z = 1;

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 2) //
		{
			if (actualTime - lastUpdate < 200) {
				return;
			}
			lastUpdate = actualTime;
			onShuff();
		}
	}

	/**
	 * Actions to do when device is shuffed.
	 */
	private void onShuff() {
		boolean found = false;

		unregisterListener();
		RunningAppProcessInfo rai = getForegroundApp(getApplicationContext());

		/*if (rai != null) {
			Log.i(TAG, "Device was shuffed in App: " + rai.processName);

			if (rai.processName.equals("android.process.media")) {

				String artist2, song2, album2;

				if (Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {

					if (songList != null) {

						Log.i(TAG, "SONGLIST NOT NULL");
						Iterator<Mp3Data> itr = songList.iterator();

						Mp3Data mp3file;

						while (itr.hasNext() && !found) {
							mp3file = itr.next();

							artist2 = mp3file.getName();
							song2 = mp3file.getTrack();
							album2 = mp3file.getAlbum();

							if (artist.contains(artist2)
									&& track.contains(song2)
									&& album.contains(album2)) {
								found = true;
								Toast.makeText(
										this,
										"Device was shuffed in App: "
												+ rai.processName + "  track: "
												+ artist + "    DATA2 -"
												+ artist2, Toast.LENGTH_SHORT)
										.show();
								Log.i(TAG,
										"FOOOOOOOOUUUUUUUUUUUUUUUUUNDDDDDDDDDDDD!!!!!!!!!!!!!!!!!");
								found = true;
								Intent i = new Intent(CONTEXT,
										linz.jku.Shaking.class)
										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								Shaking.setCode(2);
								Shaking.setUri(mp3file.getUri());
								startActivity(i);
							}
						}
					}
				} else {
					Toast.makeText(this, "External SD card not mounted",
							Toast.LENGTH_LONG).show();
				}
			}*/
			if (found == false) {
				Intent i = new Intent(CONTEXT, linz.jku.Shaking.class)
						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);				
				startActivity(i);
			//}

		}

	}

	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
	}

	/**
	 * Determines if service is running on a real device or simulator.
	 * 
	 * @return true if it's a simulator
	 */
	private boolean isSimulator() {
		return Build.MODEL.contains("sdk");
	}

	private RunningAppProcessInfo getForegroundApp(Context c) {
		RunningAppProcessInfo result = null, info = null;

		if (mActivityManager == null)
			mActivityManager = (ActivityManager) c
					.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> l = mActivityManager
				.getRunningAppProcesses();
		Iterator<RunningAppProcessInfo> i = l.iterator();
		while (i.hasNext()) {
			info = i.next();
			if (info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
					&& !isRunningService(info.processName, c)) {
				result = info;
				break;
			}
		}
		return result;
	}

	private void unregisterListener() {
		if (isSimulator) {
			sensorManagerSim.unregisterListener(this);
		} else {
			sensorManager.unregisterListener(this);
		}
	}

	private void registerListener() {
		/*
		 * Registers and starts Accelerator listeners.
		 */
		if (isSimulator) {
			sensorManagerSim.registerListener(this, sensorManagerSim
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_FASTEST);
		} else {
			sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
		}

	}

	/**
	 * Check if the service with processname is running.
	 * @param processname Set the process name to check
	 * @param c The context where we are going to check the services
	 * @return true/false if it is running or not.
	 */
	private boolean isRunningService(String processname, Context c) {
		if (processname == null || processname.equals(""))
			return false;

		RunningServiceInfo service;

		if (mActivityManager == null)
			mActivityManager = (ActivityManager) c
					.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
		Iterator<RunningServiceInfo> i = l.iterator();
		while (i.hasNext()) {
			service = i.next();
			if (service.process.equals(processname))
				return true;
		}

		return false;
	}
}