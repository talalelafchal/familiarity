public class BackgroundService extends Service {
	public static final String BROADCAST_EVENT_NAME = "BackgroundServiceMessage";

	public static final String ACTION_RECORD = "com.codesector.recorder.play";
	public static final String ACTION_PAUSE = "com.codesector.recorder.pause";
	public static final String ACTION_SAVE = "com.codesector.recorder.save";
	public static final String ACTION_DISCARD = "com.codesector.recorder.discard";
	public static final String ACTION_FINISH = "com.codesector.recorder.finish";

	public static boolean isRunning = false;
	
	private final int NOTIFICATION_ID = R.string.app_name;

	private boolean isIncomingCall = false;

	// This is the object that receives interactions from notification
	private final IBinder binder = new LocalBinder();

	private AudioRecorder audioRecorder;

	private BroadcastReceiver recorderReceiver;
	private BroadcastReceiver callReceiver;

	private NotificationManager notificationManager;
	private Notification.Builder notificationBuilder;
	private boolean isNotificationPresent;
	private PendingIntent pendingIntentRecord;
	private PendingIntent pendingIntentPause;
	private PendingIntent pendingIntentSave;
	private PendingIntent pendingIntentDiscard;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void createNotification() {
		notificationBuilder = new Notification.Builder(this);
		notificationBuilder
				.setSmallIcon(R.drawable.ic_notification_mic)
				.setContentTitle(audioRecorder.getFileName())
				.setContentText(getString(R.string.touch_to_open_app))
				.setOngoing(true)
				.setPriority(Notification.PRIORITY_MAX)
				.setContentIntent(
						PendingIntent.getActivity(this, 0, new Intent(this,
								StartupActivity.class),
								PendingIntent.FLAG_UPDATE_CURRENT));
		if (isPaused())
			notificationBuilder.addAction(R.drawable.ic_notification_record,
					getString(R.string.start), pendingIntentRecord);
		else
			notificationBuilder.addAction(R.drawable.ic_notification_pause,
					getString(R.string.pause), pendingIntentPause);

		notificationBuilder.addAction(R.drawable.ic_notification_save,
				getString(R.string.save), pendingIntentSave);
		notificationBuilder.addAction(R.drawable.ic_notification_discard,
				getString(R.string.discard), pendingIntentDiscard);

		// http://stackoverflow.com/questions/5757997/hide-time-in-android-notification-without-using-custom-layout
		notificationBuilder.setWhen(0);
		notificationBuilder.setProgress(0, 0, (audioRecorder.isPaused() ? false
				: true));
		notificationBuilder.setLights(Color.parseColor("#002D33"), 500, 1500);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// http://stackoverflow.com/questions/24659539/how-are-notification-categories-assigned-in-android-l-preview-api-20
			notificationBuilder.setCategory(Notification.CATEGORY_PROGRESS);
			notificationBuilder.setColor(Color.parseColor("#008C9E"));
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Intent recordReceive = new Intent();
		recordReceive.setAction(ACTION_RECORD);
		pendingIntentRecord = PendingIntent.getBroadcast(this, 12345,
				recordReceive, PendingIntent.FLAG_UPDATE_CURRENT);
		Intent pauseReceive = new Intent();
		pauseReceive.setAction(ACTION_PAUSE);
		pendingIntentPause = PendingIntent.getBroadcast(this, 12345,
				pauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);
		Intent saveReceive = new Intent();
		saveReceive.setAction(ACTION_SAVE);
		pendingIntentSave = PendingIntent.getBroadcast(this, 12345,
				saveReceive, PendingIntent.FLAG_UPDATE_CURRENT);
		Intent discardReceive = new Intent();
		discardReceive.setAction(ACTION_DISCARD);
		pendingIntentDiscard = PendingIntent.getBroadcast(this, 12345,
				discardReceive, PendingIntent.FLAG_UPDATE_CURRENT);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_RECORD);
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_SAVE);
		intentFilter.addAction(ACTION_DISCARD);
		intentFilter.addAction(ACTION_FINISH);
		recorderReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(ACTION_RECORD))
					setPaused(false);
				else if (action.equals(ACTION_PAUSE))
					setPaused(true);
				else if (action.equals(ACTION_SAVE))
					save();
				else if (action.equals(ACTION_DISCARD))
					discard();
				else if (action.equals(ACTION_FINISH))
					stopMe();
			}
		};
		registerReceiver(recorderReceiver, intentFilter);

		intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.PHONE_STATE");
		callReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String state = intent
						.getStringExtra(TelephonyManager.EXTRA_STATE);

				if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
					// Phone is ringing
				} else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
					// Call received
					if (!isIncomingCall) {
						isIncomingCall = true;
						save();
						Notification.Builder builder = new Notification.Builder(
								context);
						builder.setSmallIcon(R.drawable.ic_notification_stop)
								.setContentTitle(
										getString(R.string.recording_has_stopped))
								.setContentText(
										getString(R.string.touch_to_open_app))
								.setPriority(Notification.PRIORITY_MAX)
								.setAutoCancel(true)
								.setContentIntent(
										PendingIntent
												.getActivity(
														context,
														0,
														new Intent(
																context,
																StartupActivity.class),
														PendingIntent.FLAG_UPDATE_CURRENT));
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
							builder.setCategory(Notification.CATEGORY_ERROR);
							builder.setColor(Color.parseColor("#E57373"));
						}
						notificationManager.notify(1, builder.build());
					}
				} else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
					// Call dropped or rejected
					// if (isIncomingCall) {
					// isIncomingCall = false;
					// startRecording();
					// isRunning = true;
					// }
				}
			}
		};
		registerReceiver(callReceiver, intentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (audioRecorder == null) {
			try {
				audioRecorder = new AudioRecorder(this,
						intent.getStringExtra("fileName"),
						intent.getIntExtra("audioCodec", 0),
						intent.getIntExtra("bitRate", 0),
						intent.getIntExtra("sampleRate", 0),
						intent.getIntExtra("bitDepth", 0),
						intent.getIntExtra("categoryId", 0),
						intent.getBooleanExtra("skipSilence", false));
				if (audioRecorder != null)
					startRecording();
				createNotification();
			} catch (Exception e) {
				stopMe();
				return 0;
			}
		}
		isRunning = true;
		isIncomingCall = false;
		// We want this service to continue running
		// until it is explicitly stopped, so return sticky.
		return START_STICKY;
	}

	public void stopMe() {
		audioRecorder = null;
		if (isNotificationPresent) {
			stopForeground(true);
			isNotificationPresent = false;
		}
		stopSelf();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(recorderReceiver);
		unregisterReceiver(callReceiver);
		stopForeground(true);
		isNotificationPresent = false;
		isRunning = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		stopForeground(true);
		isNotificationPresent = false;
		return binder;
	}

	@Override
	public void onRebind(Intent intent) {
		hideNotification();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		showNotification();
		return true;
	}

	public void showNotification() {
		if (isRecording() && !isNotificationPresent) {
			createNotification();
			startForeground(NOTIFICATION_ID, notificationBuilder.build());
			isNotificationPresent = true;
		}
	}

	public void hideNotification() {
		if (isNotificationPresent) {
			stopForeground(true);
			isNotificationPresent = false;
		}
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		BackgroundService getService() {
			return BackgroundService.this;
		}
	}

	/**
	 * AudioRecorder methods
	 */
	public AudioRecorder.State getState() {
		return audioRecorder != null ? audioRecorder.getState() : State.STANDBY;
	}

	public void startRecording() {
		if (audioRecorder == null)
			return;
		audioRecorder.startRecording();
	}

	public boolean isRecording() {
		if (audioRecorder == null)
			return false;
		return audioRecorder.isRecording();
	}

	public void save() {
		if (audioRecorder == null)
			return;
		audioRecorder.save();
	}

	public void discard() {
		if (audioRecorder == null)
			return;
		audioRecorder.discard();
	}

	public void setPaused(boolean paused) {
		if (audioRecorder == null)
			return;
		audioRecorder.setPaused(paused);
		if (isNotificationPresent) {
			createNotification();
			notificationManager.notify(NOTIFICATION_ID,
					notificationBuilder.build());
		}
	}

	public boolean isPaused() {
		if (audioRecorder == null)
			return false;
		return audioRecorder.isPaused();
	}

	public void setSkipSilence(boolean silence) {
		if (audioRecorder == null)
			return;
		audioRecorder.setSkipSilence(silence);
	}

	public boolean isSkipping() {
		if (audioRecorder == null)
			return false;
		return audioRecorder.isSkipping();
	}

	public void setFileName(String newName) {
		if (audioRecorder == null)
			return;
		audioRecorder.setFileName(newName);
		if (isNotificationPresent) {
			createNotification();
			notificationManager.notify(NOTIFICATION_ID,
					notificationBuilder.build());
		}
	}
	
	public void setCategoryId(int newId) {
		if (audioRecorder == null)
			return;
		audioRecorder.setCategoryId(newId);
	}

	public String getFileName() {
		if (audioRecorder == null)
			return "";
		return audioRecorder.getFileName();
	}

	public short getMaxAmplitude() {
		if (audioRecorder == null)
			return 0;
		return audioRecorder.getMaxAmplitude();
	}

	public int getRecordTime() {
		if (audioRecorder == null)
			return 0;
		return audioRecorder.getRecordTime();
	}

	public boolean isFileCreated() {
		if (audioRecorder == null)
			return false;
		return audioRecorder.isFileCreated();
	}
}