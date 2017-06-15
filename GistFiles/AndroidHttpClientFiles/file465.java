package com.shareobj.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	GPSTracker gps;
	List<String> phones = new ArrayList<String>();
	ListView search;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// create class object
		gps = new GPSTracker(MainActivity.this);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			// \n is for new line
			// Toast.makeText(getApplicationContext(),
			// "Your Location is - \nLat: " + latitude + "\nLong: " + longitude,
			// Toast.LENGTH_LONG).show();
			initList(latitude, longitude);
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	ArrayList<RowItem> objList = new ArrayList<RowItem>();

	private void initList(double latitude, double longitude) {

		search = (ListView) findViewById(R.id.list_item);

		String str = "";
		HttpResponse response;
		HttpClient myClient = new DefaultHttpClient();

		String domain = "com";
		String locale = Locale.getDefault().getLanguage();
		if (locale.equals("ru")) {
			domain = "ru";
		} else if (locale.equals("es")) {
			domain = "es";
		} else if (locale.equals("de")) {
			domain = "de";
		} else if (locale.equals("fr")) {
			domain = "fr";
		} else if (locale.equals("by")) { // or "be"?
			domain = "by";
		}

		HttpPost myConnection = new HttpPost("http://shareobj." + domain
				+ "/transporters/near/" + latitude + "/" + longitude
				+ "/10000.json");

		try {
			response = myClient.execute(myConnection);
			str = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(), "Error: " + e.toString(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Error: " + e.toString(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		try {
			JSONArray jsonMainNode = new JSONArray(str);

			if (jsonMainNode.length() > 0) {

				// search = (ListView) findViewById(R.id.list_item);
				CustomListViewAdapter adapter;

				for (int i = 0; i < jsonMainNode.length(); i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String km = getApplicationContext().getString(R.string.km);
					String title = jsonChildNode.optString("title");
					String type = jsonChildNode.optString("type");
					String distance = jsonChildNode.optString("distance") + " "
							+ km;
					String phone = "+" + jsonChildNode.optString("phone");

					String itemFilename = jsonChildNode
							.optString("itemFilename");
					String isTrusted = jsonChildNode.optString("isTrusted");
					String newbie = getApplicationContext().getString(
							R.string.newbie);
					String trusted = getApplicationContext().getString(
							R.string.trusted);
					String desc = isTrusted.equals("true") ? trusted : newbie;
					String url = "http://shareobj.com/media/cache/marker/media/"
							+ itemFilename;
					RowItem item = new RowItem(url, phone, title, distance,
							type, desc);
					objList.add(item);
					phones.add(phone);

				}

				adapter = new CustomListViewAdapter(this, R.layout.list_item,
						objList);
				search.setAdapter(adapter);

				search.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> a, View v, int i,
							long l) {
						try {

							Toast.makeText(getApplicationContext(), "OnClick... " + i,
									Toast.LENGTH_SHORT).show();
							
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), "... " + i,
									Toast.LENGTH_SHORT).show();
						}
					}

				});

			} else {
				Toast.makeText(getApplicationContext(),
						getApplicationContext().getString(R.string.not_found),
						Toast.LENGTH_SHORT).show();
				objList.clear();
				phones.clear();
			}

		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_SHORT).show();
		}
	}
	

}
