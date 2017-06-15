package com.cs4720.drinkengine_android.dummy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DrinkContent {

    public static class Drink {

        public String name;
        public int missing;
        public String description;
        public int calories;
        public int strength;
        public List<String> units;
        public List<String> ingredients;
		
        public Drink(String name) {
			super();
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
    }
    
    public static String url = "http://drinkengine.appspot.com/view";

    public static List<Drink> ITEMS = new ArrayList<Drink>();
    public static Map<String, Drink> ITEM_MAP = new HashMap<String, Drink>();

	static{
		new GetDrinksTask().execute(url);
	}
    
    private static void addItem(Drink item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }
    
public static String getJSONfromURL(String url) {

	// initialize
	InputStream is = null;
	String result = "";

	// http post
	try {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		is = entity.getContent();

	} catch (Exception e) {
		Log.e("DrinkEngine", "Error in http connection " + e.toString());
	}

	// convert response to string
	try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				is, "iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		result = sb.toString();
	} catch (Exception e) {
		Log.e("DrinkEngine", "Error converting result " + e.toString());
	}

	return result;
}

// The definition of our task class
public static class GetDrinksTask extends AsyncTask<String, Integer, String> {
	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		ArrayList<Drink> lcs = new ArrayList<Drink>();
		try {

			String webJSON = getJSONfromURL(url);
			JSONArray drinks = new JSONArray(webJSON);
			
			for (int i = 0; i < drinks.length(); i++) {
				JSONObject jo = drinks.getJSONObject(i);
				Drink current = new Drink(jo.getString("name"));
				current.missing = Integer.parseInt(jo.getString("missing"));
				current.description = jo.getString("description");
				current.calories = Integer.parseInt(jo.getString("calories"));
				current.strength = Integer.parseInt(jo.getString("strength"));
				JSONArray units = jo.getJSONArray("units");
				for(int j = 0; j < units.length(); j++){
					current.units = new ArrayList<String>();
					current.units.add(units.getString(j));
				}
				
				JSONArray ingredients = jo.getJSONArray("ingredients");
				for(int j = 0; j < ingredients.length(); j++){
					current.ingredients = new ArrayList<String>();
					current.ingredients.add(ingredients.getString(j));
				}
				
				addItem(current);
			}

		} catch (Exception e) {
			Log.e("DrinkEngine", "JSONPARSE:" + e.toString());
		}

		return "Done!";
	}

	@Override
	protected void onProgressUpdate(Integer... ints) {

	}

	@Override
	protected void onPostExecute(String result) {
		// tells the adapter that the underlying data has changed and it
		// needs to update the view
		
	}
}
}
