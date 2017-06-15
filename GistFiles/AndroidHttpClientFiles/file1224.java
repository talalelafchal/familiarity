package com.blogspot.hongthaiit.customlistview.asynctask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.blogspot.hongthaiit.customlistview.ListViewActivity;

public class GetJsonFromUrlTask extends AsyncTask<Void, Void, String> {

	private Activity activity;
	private String url;
	private ProgressDialog dialog;
	private final static String TAG = GetJsonFromUrlTask.class.getSimpleName();
 
	public GetJsonFromUrlTask(Activity activity, String url) {
		super();
		this.activity = activity;
		this.url = url;
	}
 
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Create a progress dialog
		dialog = new ProgressDialog(activity); 
		// Set progress dialog title
		dialog.setTitle("Getting JSON DATA");
		// Set progress dialog message
		dialog.setMessage("Loading...");
		dialog.setIndeterminate(false);
		// Show progress dialog
		dialog.show(); 
	}
 
	@Override
	protected String doInBackground(Void... params) {
 
		// call load JSON from url method
		return loadJSON(this.url).toString();
	}
 
	@Override
	protected void onPostExecute(String result) {
		((ListViewActivity) activity).parseJsonResponse(result);
		dialog.dismiss();
		Log.i(TAG, result);
	}
 
	public JSONObject loadJSON(String url) {
		// Creating JSON Parser instance
		JSONGetter jParser = new JSONGetter();
 
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url);
 
		return json;
	}
 
	private class JSONGetter {
 
		private InputStream is = null;
		private JSONObject jObj = null;
		private String json = "";
 
		// constructor
		public JSONGetter() {
 
		}
 
		public JSONObject getJSONFromUrl(String url) {
 
			// Making HTTP request
			try {
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
 
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
 
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
 
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),
						8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			} 
 
			// try parse the string to a JSON object
			try {
				jObj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
 
			// return JSON String
			return jObj;
 
		}
	}
}
