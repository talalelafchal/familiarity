package edu.northwestern.NUShuttle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.northwestern.NUShuttle.models.Location;

public class Locations extends ListActivity {
	
	protected String title = "Select a Stop";
	
	@SuppressWarnings("unused")
	private static final String TAG = "Locations Activity";
	
	//private JSONArray locations;
	private ArrayList<Location> locations = new ArrayList<Location>();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		((TextView)findViewById(R.id.title)).setText(title);
		((TextView)findViewById(R.id.virtual_time)).setText(Utilities.getVirtualDateDescription());
		
		new ListTask(this).execute();
	}
	
	class ListTask extends AsyncTask<Void,Void,Void> {
		protected ArrayList<Map<String,String>> listItems = new ArrayList<Map<String,String>>();
		protected Context mContext;
		protected ProgressDialog pd;
		
		public ListTask(Context context) {
			mContext = context;
		}
		
		public void onPreExecute() {
			pd = ProgressDialog.show(mContext, "", "Loading...", true);
		}
		
		protected Void doInBackground(Void... unused) {
			Map<String, String>item;
			
			try {
				JSONArray data = Utilities.makeRequest("locations").getJSONArray("data");
				
				for ( int i = 0; i < data.length(); i++ ) {
					item = new HashMap<String, String>();
					Location location = new Location(data.getJSONObject(i));
					item.put("name", location.getName());
					item.put("time", "");
					listItems.add(item);
					locations.add(location);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		public void onPostExecute(Void unused) {
			if ( listItems == null || listItems.size() == 0 ) {
				Utilities.showAlert(mContext, "Could not load stop data.");
			}
			
			setListAdapter(new SimpleAdapter(
				mContext,
				listItems,
				R.layout.list_item,
				new String[] {"name", "time"},
				new int[] {R.id.title, R.id.next_time}
			));
			pd.hide();
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.settings: {
			Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
			startActivity(settingsIntent);
			break;
		}
		
		case R.id.view_map: {
			Intent mapIntent = new Intent(getApplicationContext(), ShowMap.class);
			startActivity(mapIntent);
			break;
		}
			
		}
		
		return true;
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Location location = locations.get(position);
		((MyApplication) getApplicationContext()).setLocation(location);
		Intent stopsIntent = new Intent(getApplicationContext(), StopsForLocation.class);
		startActivity(stopsIntent);
	}
}
