package com.example.myfirstapp;

import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.app.SearchManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * @author Sai Valluri
 * This class is an activity that lets the user search for contacts that have been stored in the SQLite Database.
 * It displays the contacts who have the same name as the search query in the ListView
 */
public class SearchResultsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private ListView list;
	private DatabaseTable db;
	private SimpleCursorAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        list = (ListView)findViewById(android.R.id.list);
        db = new DatabaseTable(this);
        handleIntent(getIntent());
    }
    
    public void onNewIntent(Intent intent) {
    	setIntent(intent);
    	handleIntent(intent);
    }

    /**
     * This methods retrieves the  user query and calls the showResults() method to display the results
     * @param intent
     */
    private void handleIntent(Intent intent) {
    	 if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    	
    }
    /**
     * This method retrieves a cursor which has the rows that matched the query and displays the results in a list
     * with an onItemClickListener. It then sends the URI (path to database of the item selected) to the Contact Activity
     * to display the info
     * @param query
     */
	private void showResults(String query) {
		db = new DatabaseTable(this);
		db.open();
		Cursor cursor = db.getContactMatches(query, null);
		boolean b = cursor.moveToFirst();
		Log.d("cursor empty?", "" + b);
		int name = cursor.getColumnIndex(DatabaseTable.COL_NAME);
		Log.d("Name", "" + name);
		mAdapter = new SimpleCursorAdapter(this, R.layout.activity_search_results, 
				cursor, new String[] {DatabaseTable.COL_NAME}, new int[] {android.R.id.text1}, 0);
		list.setAdapter(mAdapter);
		Log.d("Cursor", "" + cursor.getCount());
		Log.d("Cursor name", "" + cursor.getString(name));
		getLoaderManager().initLoader(0, null, this);
		list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent contactIntent = new Intent(view.getContext(), ContactActivity.class);
                Uri uri2 = Uri.withAppendedPath(MyContactProvider.CONTENT_URI, String.valueOf(id));
                contactIntent.setData(uri2);
                startActivity(contactIntent);
            }
        });
		db.close();
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
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.search:
	                onSearchRequested();
	                return true;
	            default:
	                return false;
	        }
	    }

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}

