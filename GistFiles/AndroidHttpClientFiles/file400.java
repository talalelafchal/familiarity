package org.apache.cordova;

import org.acra.ErrorReporter;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

public class YouTubeUploadPlugin extends Plugin {

	static final String ACTION = "uploadVideo";

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		// perform upload and return status

		PluginResult result = null;
		if (ACTION.equals(action)) {
			try {
				String file = data.getString(0);

				YouTubeUploader ytu = new YouTubeUploader();

				ytu.OtherLogin();

				String url = ytu.UploadVideo(file);

				if (url != null) {
					result = new PluginResult(Status.OK, url);
				} else {
					result = new PluginResult(Status.ERROR);
				}
			} catch (JSONException jsonEx) {
				ErrorReporter.getInstance().handleException(jsonEx);
				result = new PluginResult(Status.JSON_EXCEPTION);
			}
		} else {
			result = new PluginResult(Status.INVALID_ACTION);
		}
		return result;
	}
}