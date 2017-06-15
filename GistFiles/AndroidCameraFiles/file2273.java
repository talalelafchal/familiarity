package app.akexorcist.googledapsample;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import app.akexorcist.gdaplibrary.GooglePlaceSearch;
import app.akexorcist.gdaplibrary.GooglePlaceSearch.OnPlaceResponseListener;

public class PlaceActivity extends Activity {
	
	final String ApiKey = "your_api_key";
	
    double latitude = 13.730354;
	double longitude = 100.569701;
	int radius = 1000;
	String type = "food";
	String language = "th";
	String keyword = "japan restaurant food";
	
	TextView textStatus;
	ListView listView;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        textStatus = (TextView)findViewById(R.id.textStatus);
        
        listView = (ListView)findViewById(R.id.listView);
        
        GooglePlaceSearch gp = new GooglePlaceSearch(ApiKey);
		gp.setOnPlaceResponseListener(new OnPlaceResponseListener() {
			public void onResponse(String status, ArrayList<ContentValues> arr_data,
					Document doc) {
				textStatus.setText("Status : " + status);
				Log.i("CHeck", "AAA");
				
				if(status.equals(GooglePlaceSearch.STATUS_OK)) {
					ArrayList<String> array = new ArrayList<String>();
					
					for(int i = 0 ; i < arr_data.size() ; i++) {
						array.add("Name : " + arr_data.get(i).getAsString(GooglePlaceSearch.PLACE_NAME) + "\n"
								+ "Address : " + arr_data.get(i).getAsString(GooglePlaceSearch.PLACE_ADDRESS) + "\n"
								+ "Latitude : " + arr_data.get(i).getAsString(GooglePlaceSearch.PLACE_LATITUDE) + "\n"
								+ "Longitude : " + arr_data.get(i).getAsString(GooglePlaceSearch.PLACE_LONGITUDE) + "\n"
								+ "Phone Number : " + arr_data.get(i).getAsString(GooglePlaceSearch.PLACE_PHONENUMBER));
					}

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(PlaceActivity.this
							, R.layout.listview_text, array);
					listView.setAdapter(adapter);
				}
			}
		});
		
        gp.getNearBy(latitude, longitude, radius, type, language, keyword);
		//gp.getTextSearch(keyword, type, false, language);
        //gp.getRadarSearch(latitude, longitude, radius, type, language, false, keyword);
	}
}
