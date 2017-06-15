package com.example.androidlab;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
 
public class MainActivity extends ListActivity {
 
  static final String[] arrOS = new String[] { "Android", "iPhone",
  		"WindowsMobile", "Blackberry", "WebOS", "Ubuntu", "Windows7",
			"Max OS X", "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X",
			"Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
			"Android", "iPhone", "WindowsMobile" };
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Custom ArrayList
		ArrayList<Map> list = new ArrayList<Map>();
		for(int i=0; i<arrOS.length; i++){
			Map map = new HashMap();
	        map.put("Id", i+1);
	        map.put("Value", arrOS[i]);
	        list.add(map);
		}
		
		setListAdapter(new CustomAdapter(this, R.layout.list_item, list));
 
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
 
	}
}