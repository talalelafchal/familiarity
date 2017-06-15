package com.example.myfirstapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;
/**
 * @author Sai Valluri
 * This is the Main Activity which is the main layout when the application is first launched
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	 /**
     * This is an onClick method for the View Contacts button
     * @return
     */
	public void displayData(View v) {
		Intent intent = new Intent(v.getContext(), DisplayAllContacts.class);
		startActivity(intent);
	}
	
	/**
	 * This is an onClick method for the (+) button
	 * @param v
	 */
	public void addContacts(View v) {
		Intent intent = new Intent(v.getContext(), AddContacts.class);
		startActivity(intent);
	}
	
	/**
	 * This is an onClick method for the Restaurant Locator button 
	 * @param v
	 */
	public void showMap(View v) {
		Intent intent = new Intent(v.getContext(), RestaurantLocator.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        
     // Associate searchable configuration with the SearchView
        SearchManager searchManager =
               (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
		return true;
        
    }

}
