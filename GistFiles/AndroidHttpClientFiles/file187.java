package com.blogspot.hongthaiit.swipetorefreshexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetDataTask extends AsyncTask<String, Void, String> {

	private SwipeActivity activity;
	private String url;
	private InputStream inputStream = null;

	public GetDataTask(SwipeActivity activity, String url) {
		this.activity = activity;
		this.url = url;
		Log.i("AsyncTask", "Init");
	}

	@Override
	protected String doInBackground(String... params) {

		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		try {
			// Set up HTTP post

			// HttpClient is more then less deprecated. Need to change to
			// URLConnection
			HttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(param));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			// Read content & Log
			inputStream = httpEntity.getContent();
		} catch (UnsupportedEncodingException e1) {
			Log.e("UnsupportedEncodingException", e1.toString());
			e1.printStackTrace();
		} catch (ClientProtocolException e2) {
			Log.e("ClientProtocolException", e2.toString());
			e2.printStackTrace();
		} catch (IllegalStateException e3) {
			Log.e("IllegalStateException", e3.toString());
			e3.printStackTrace();
		} catch (IOException e4) {
			Log.e("IOException", e4.toString());
			e4.printStackTrace();
		}
		// Convert response to string using String Builder
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(inputStream, "utf-8"), 8);
			StringBuilder sBuilder = new StringBuilder();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				sBuilder.append(line + "\n");
			}

			inputStream.close();
			String result = sBuilder.toString();
			return result;

		} catch (Exception e) {
			Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
			return null;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		Log.i("Asynctask", result);
		ArrayList<Student> students = new ArrayList<Student>();
		
		// parse JSON data
		try {
			JSONObject jObj = new JSONObject(result);
			JSONArray jArray = jObj.getJSONArray("student");

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject jObject = jArray.getJSONObject(i);
				boolean gender = false;
				
				String name = jObject.getString("name");
				if (jObject.getString("gender").equals("male")) {
					gender = true;
				}
				int age = jObject.getInt("age");

				Student student = new Student(name, age, gender);
				students.add(student);
			}
			activity.callBackData(students);
			Log.i("Asynctask", "size: " + students.size());
			
		} catch (JSONException e) {
			Log.e("JSONException", "Error: " + e.toString());
		}
	}
}
