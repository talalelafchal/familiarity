package com.map.odl_testing;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.map.odl_testing.filterlist.CustemSectionAbstractActivity;
import android.view.View;
import android.widget.AdapterView;

public class SearchActivity extends CustemSectionAbstractActivity {
	public static int odl_id = -1;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		String odl_name = odl_names[position];
		
		for (int num = 0; num<odls.length() ; num++){
			try {
				JSONObject odl = odls.getJSONObject(num);
				if(odl.getString("name") == odl_name){
					odl_id = odl.getInt("id");
					finish();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String[] getListData() {
		// TODO Auto-generated method stub
		int size = filter_odlss_names.size();
		odl_names = new String[size];
		for(int j=0;j<filter_odlss_names.size();j++){
			odl_names[j] = filter_odlss_names.get(j);
		}
		return odl_names;
	}

	@Override
	protected Map<String, String> getCustemSection() {
		// TODO Auto-generated method stub
		Map<String,String> map=new HashMap<String, String>();
		map.put("#", "local");
		map.put("$", "hot");
		return map;
	}
}
