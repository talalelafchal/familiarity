package com.map.odl_testing.filterlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.map.odl_testing.R;
import com.map.odl_testing.filterlist.MyLetterView.OnTouchingLetterChangedListener;
import com.model.utility.ConfigurationManager;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public abstract class CustemSectionAbstractActivity extends Activity implements
		OnTouchingLetterChangedListener ,AdapterView.OnItemClickListener{

	public TextView overlay;
	private MyLetterView myView;
	public SectionIndexerListView mIndexerListView;

	private OverlayThread overlayThread = new OverlayThread();
	public SectionIndexerListAdapter mIndexerListAdapter;
	public SharedPreferences pref;
	public Resources res;
	public String[] odl_names;
	public ArrayList<String> odlss_names = new ArrayList<String>();
	public ArrayList<String> filter_odlss_names = new ArrayList<String>();
	
	public JSONArray odls;
	public TextView search_result;
	public static boolean class_stack_flag = true;
	
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		res = getResources();
		
		search_result = (EditText)findViewById(R.id.search_box);
		
		search_result.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				//search_hint.setVisibility(View.INVISIBLE);
				filter_names(); 
    			doSortForStringArray();
				mIndexerListAdapter = new SectionIndexerListAdapter(
						getLayoutInflater(), initPairs(), res);
				mIndexerListView.setAdapter(mIndexerListAdapter);
				mIndexerListView.setTextFilterEnabled(true);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
				
		
		mIndexerListView = (SectionIndexerListView) findViewById(R.id.list);
		pref = this.getSharedPreferences(
				ConfigurationManager.mConfigFileName, this.MODE_PRIVATE);
		ConfigurationManager.setSharedPreference(pref);
		
		try {
			odls = new JSONArray(ConfigurationManager.read_Cache());
			getODLsList(odls);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		mIndexerListView.setOnItemClickListener(this);
		doSortForStringArray();
		mIndexerListAdapter = new SectionIndexerListAdapter(
					getLayoutInflater(), initPairs(), res);
		mIndexerListView.setAdapter(mIndexerListAdapter);
		mIndexerListView.setTextFilterEnabled(true);
	}
	
	private void getODLsList(JSONArray odls){
		odl_names = new String[odls.length()];
		for (int num = 0; num < odls.length() ; num++){
			JSONObject odl;
			try {
				odl = odls.getJSONObject(num);
				String name = odl.getString("name");
				odlss_names.add(name);
				filter_odlss_names.add(name);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private  void filter_names(){
		String search_txt = search_result.getText().toString().toLowerCase();
		ArrayList<String> temp = new ArrayList<String>();
		int j = 0;
		int flag;
		for(int i=0;i<odlss_names.size();i++){
			String i_txt = odlss_names.get(i).toLowerCase();
			flag = i_txt.indexOf(search_txt);
			if(flag == 0){
				temp.add(odlss_names.get(i));
			}
		}
		
		if(temp.size()!=0){
			filter_odlss_names = temp;
		}
	}
		
	private boolean needSort = true;
	
	public ListView getSectionListview(){
		return mIndexerListView;
	}
	
	/**
	 *
	 * 
	 * @param needSort
	 */
	public void setNeedSort(boolean needSort) {
		this.needSort = needSort;
	}

	@SuppressWarnings("unchecked")
	public void doSortForStringArray() {
		mNicks = getListData();
		if (needSort)
			Arrays.sort(mNicks, new PinyinComparator());             /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	/**
	 * 
	 */
	
	public abstract String[] getListData();

	ArrayList<Pair<String, ArrayList<String>>> initPairs() {
		ArrayList<Pair<String, ArrayList<String>>> pairs = new ArrayList<Pair<String, ArrayList<String>>>();

		doCustemSection(pairs);

		for (int j = 65; j <= 90; j++) {
			char c = (char) j;
			ArrayList<String> list = new ArrayList<String>();
			Pair<String, ArrayList<String>> pair = new Pair<String, ArrayList<String>>(
					String.valueOf(c), list);

			for (int i = 0; i < mNicks.length; i++) {
				String catalog = PingYinUtil.getPingYin(mNicks[i])
						.substring(0, 1).toUpperCase();
				if (catalog.startsWith(String.valueOf(c))) {
					list.add(mNicks[i]);
				}
			}
			if (list.size() > 0)
				pairs.add(pair);
		}
		return pairs;
	}

	/**
	 * 
	 */
	private void doCustemSection(
			ArrayList<Pair<String, ArrayList<String>>> pairs) {

		Map<String, String> maps = getCustemSection();
		if (maps != null) {
			Set<String> keySet = maps.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String str = it.next();
				ArrayList<String> list = new ArrayList<String>();
				Pair<String, ArrayList<String>> pair = new Pair<String, ArrayList<String>>(
						maps.get(str), list);
				for (int i = 0; i < mNicks.length; i++) {
					String catalog = PingYinUtil.getPingYin(mNicks[i])
							.substring(0, 1).toUpperCase();
					if (catalog.startsWith(str)) {
						list.add(mNicks[i]);
					}
				}
				if (list.size() > 0)
					pairs.add(pair);
			}
		}
	}

	/**
	 *  
	 * map.put("#", "local");
	 * @return 
	 */
	protected abstract Map<String, String> getCustemSection();

	public void onTouchingLetterChanged(String s) {
		int position = alphaIndexer(s);
		if (position >= 0) {
			mIndexerListView.setSelection(position);

			overlay.setText(s);
			overlay.setVisibility(View.VISIBLE);
			handler.removeCallbacks(overlayThread);
			handler.postDelayed(overlayThread, 1500);
		}
	}

	
	public int alphaIndexer(String s) {
		Map<String, String> maps = getCustemSection();
		if (maps != null) {
			Set<String> keySet = maps.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String str=it.next();
				if(s.startsWith(maps.get(str))){
					s=str;
					break;
				}
			}
		}
	
		int i = 0;
		for (; i < mNicks.length; i++) {
			String catalog = PingYinUtil.getPingYin(mNicks[i]).substring(0, 1)
					.toUpperCase();
			if (catalog.startsWith(s)) {
				return i;
			}
		}

		return -1;
	}

	private Handler handler = new Handler() {
	};

	private class OverlayThread implements Runnable {

		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	private String[] mNicks;

}
