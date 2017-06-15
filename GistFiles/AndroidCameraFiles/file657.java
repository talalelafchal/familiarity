package com.dhavaln.mobile.android.plugin;

import java.util.Date;

import org.json.JSONArray;

import android.util.Log;

import com.phonegap.api.PhonegapActivity;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;

public class SyncThread extends Plugin {

	public static final String ACTION_START = "start";

	private String syncCallBackId;
	private Thread t;
	private boolean stop;
	
	public SyncThread() {
	}

	@Override
	public void setContext(PhonegapActivity ctx) {
		super.setContext(ctx);
		this.syncCallBackId = null;
	}
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		Log.i("SyncThread", action + " Action called with callback id "
				+ callbackId );

		PluginResult.Status status = PluginResult.Status.INVALID_ACTION;
		String result = "Unsupported Operation: " + action;
		// either start or stop the listener...
		if (action.equals(ACTION_START)) {
			if (this.syncCallBackId != null) {
				return new PluginResult(PluginResult.Status.ERROR,
						"Thread already running.");
			}
			this.syncCallBackId = callbackId;
			PluginResult pluginResult = new PluginResult(
					PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);

			if (t == null) {
				t = new Thread() {
					@Override
					public void run() {
						while (!stop) {
							try {
								Thread.sleep(15000);
								Log.i("SyncThread",
										"ready to call javascript for sync " + new Date() + SyncThread.this.toString());
								initSync(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				t.start();
			}

			return pluginResult;
		}

		return new PluginResult(status, result); // no valid action called
	}

	private void initSync(boolean keepCallback) {
		if (this.syncCallBackId != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK);
			result.setKeepCallback(keepCallback);
			this.success(result, this.syncCallBackId);
		}
	}

	@Override
	public void onDestroy() {
		stop = true;
		t.stop();
	}
}
