package ramon.app;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Principal extends ListActivity {
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listplaceholder);
        
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        
        JSONObject json = jsonfunctions.getJSONfromURL("http://mobileapp.comuf.com/userdata/test");
        
        try {
        	JSONArray  earthquakes = json.getJSONArray("blogger");
        	
	        for (int i=0;i<earthquakes.length();i++) {						
				HashMap<String, String> map = new HashMap<String, String>();	
				JSONObject e = earthquakes.getJSONObject(i);
				
				map.put("id",  String.valueOf(i));
	        	map.put("name", e.getString("titulo"));
	        	map.put("magnitude", "Fecha: " +  e.getString("fecha"));
	        	mylist.add(map);			
			}		
        } catch(JSONException e) {
        	 Log.e("log_tag", "Error parsing data "+e.toString());
        }
        
        ListAdapter adapter = new SimpleAdapter(this, mylist , R.layout.main, 
                        new String[] { "name", "magnitude" }, 
                        new int[] { R.id.item_title, R.id.item_subtitle });
        
        setListAdapter(adapter);
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);	
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Toast.makeText(Principal.this, "ID '" + /*o.get("id") +*/ "' was clicked.", Toast.LENGTH_SHORT).show();
			}
		});
    }
}