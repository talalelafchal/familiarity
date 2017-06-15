package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * LockScreenReceiver manages BroadcastReceivers for types ACTION_SCREEN_OFF and
 * ACTION_USER_PRESENT and starts or pauses ShakeContactService when the correct
 * intent is received.
 * 
 * @author Diego Mu�oz Callejo
 * 
 */
public class LockScreenReceiver extends BroadcastReceiver {

	private ShakeContactService service;
	private static final String TAG = "LockScreenReceiver";

	/**
	 * Initializes BroadcastReceiver.
	 * @param service ShakeContactService to manage.
	 */
	public LockScreenReceiver(ShakeContactService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		/*
		 * Determines if should start or pause the service.
		 */
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			Log.d(TAG, "SCREEN_OFF");
			service.onPause();
		} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
			Log.d(TAG, "ACTION_USER_PRESENT");
			service.onStart(intent, getResultCode());
		}
	}

}
