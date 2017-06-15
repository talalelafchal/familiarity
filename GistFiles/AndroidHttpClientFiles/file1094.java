package com.mmyuksel.proje;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UserListActivity extends Activity implements
		SearchView.OnQueryTextListener {
	private SearchView mSearchView;
	private TextView mStatusView;
	GridView istanimlistesi; 

	public static String URL = "http://192.168.1.5/testapp/service1.svc/GetUser/?key=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
		kullanicilistesi = (GridView) findViewById(R.id.gvkullanicilistesi);

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
 
 		Common_UserList_Async g = new Common_UserList_Async(UserListActivity.this);
 		g.execute(URL);

		kullanicilistesi.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

					Toast.makeText(
							getApplicationContext(),
							"external tests "
									+ ((TextView) v.findViewById(R.id.txLabel1))
											.getText(), Toast.LENGTH_LONG)
							.show();
  			}
		});
		
	}

	public void setList(List<ListObject> list) {
		kullanicilistesi.setAdapter(new Common_Grid_Adapter(this,
				R.id.gvkullanicilistesi, list));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		getMenuInflater().inflate(R.menu.origin_list, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_view_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();
		setupSearchView(searchItem);

		return true;
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupSearchView(MenuItem searchItem) {
		if (isAlwaysExpanded()) {
			mSearchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		mSearchView.setOnQueryTextListener(this);
	}

	public boolean onQueryTextChange(String newText) {

		if (newText == "") {
			ArrayList<String> a = new ArrayList<String>();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, a);
			gvkullanicilistesi.setAdapter(adapter);
		}
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		query = query.replaceAll(" ","%20");
		Common_UserList_Async g = new Common_UserList_Async(UserListActivity.this);
 		g.execute(URL+query);
 
		return false;
	}

	public boolean onClose() {
		mStatusView.setText("Kapandi");
		return false;
	}

	protected boolean isAlwaysExpanded() {
		return true;
	}

}

