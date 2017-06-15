package com.example.android.searchjsonhttp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<BookList>> {

    //Define the search URL
    private static String BOOKS_URL = null;

    //Define the Custom BookListAdapter
    private BookListAdapter customAdapter;

    //Define the ListView
    private ListView listView;

    //Define the List with Custom BookList Object
    private List<BookList> customLists;

    //Define SearchView
    private SearchView editsearch;

    //Define the ProgressBar
    private ProgressBar progressbar;

    //Set TextView for no earthquakes found notification
    private TextView emptyTextView;


    /**
     * Constant value for the BookList loader ID. We can choose any integer.
     * This is really only comes into play if you're using multiple loaders.
     */
    private static final int BOOKLIST_LOADER_ID = 1;

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the loader.
        initLoader();

        // Locate the listView through ID
        listView = (ListView) findViewById(R.id.list_item);
        //Create new ArrayList with custom object
        customLists = new ArrayList<>();
        //Create new BookListAdapter with customList object
        customAdapter = new BookListAdapter(this, customLists);
        //Set adapter for the listView
        listView.setAdapter(customAdapter);

        // Locate the ProgressBar through ID
        progressbar = (ProgressBar) findViewById(R.id.progress_bar);
        // Initialize the Visibility to be GONE
        progressbar.setVisibility(GONE);
        //Locate the  No books vie
        emptyTextView = (TextView) findViewById(R.id.no_internet);
        // Locate the EditText in list_item_specific.xml
        editsearch = (SearchView) findViewById(R.id.search_view);
        //The default state of searchView will be expanded
        editsearch.setIconifiedByDefault(false);
        // Set the Query Text Listener for editSearch
        editsearch.setOnQueryTextListener(this);

    }

    /**
     * Define the rules when user submit search query
     *
     * @param query the query user input
     * @return true if the query has been handled by the listener
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        // Set the Visibility as Visible when user click query button
        progressbar.setVisibility(View.VISIBLE);
        if (query == null || query.length() == 0) {
            return false;
        }
        //Clear the No books notification
        emptyTextView.setText(null);
        BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=" + query;
        if (getNetworkInfo() != null && getNetworkInfo().isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            if (loaderManager.getLoader(BOOKLIST_LOADER_ID) == null) {
                Log.v("MainActivity", "onQueryTextSubmit, has the network connection " +
                        "but the loader BOOKLIST_LOADER_ID equals null: " + loaderManager.getLoader(BOOKLIST_LOADER_ID));
                initLoader();
            } else {
                Log.v("MainActivity", "onQueryTextSubmit, has the network connection " +
                        "but the loader BOOKLIST_LOADER_ID not null: " + loaderManager.getLoader(BOOKLIST_LOADER_ID));
                restartLoader();
            }
        } else {
            emptyTextView.setText(R.string.no_internet_connection);
            progressbar.setVisibility(GONE);
        }
        return true;
    }

    /**
     * Called when the query text is changed by the user
     *
     * @param newText the new content of the query text filed
     * @return false if the SearchView should perform the default action of showing any suggestions
     * if available
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public Loader<List<BookList>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader called");
        //Create a new loader for the given URL
        //TODO URL may not correct
        return new BookListLoader(this, BOOKS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<BookList>> loader, List<BookList> bookLists) {

        //Gone the Progressbar after load finished
        progressbar.setVisibility(GONE);
        Log.v(LOG_TAG, "onLoadFinished called");
        //Clear the adapter of previous bookList data
        customAdapter.clear();
        //If there is a valid list of {@link BookList}s, then add then to the adapter's
        //data set. This will trigger the listView to update.
        if (bookLists != null && !bookLists.isEmpty()) {
            customAdapter.addAll(bookLists);
        } else if (getInputQuery() != null && !getInputQuery().isEmpty()) {
            emptyTextView.setText(R.string.no_books);
        }
    }

    /**
     * Clear the list view when the loader reset (request the search)
     */

    @Override
    public void onLoaderReset(Loader<List<BookList>> loader) {
        Log.v(LOG_TAG, "onLoaderReset called");
        //Loader rest, so we can clear out our existing data
        customAdapter.clear();
    }

    /**
     * initiate the loader
     */

    private void initLoader() {
        //Get a reference to the LoaderManager, in order to interact with loaders
        LoaderManager loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(BOOKLIST_LOADER_ID, null, this);
    }

    /**
     * Restart the loader
     */
    private void restartLoader() {
        //Get a reference to the LoaderManager, in order to interact with loaders
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(BOOKLIST_LOADER_ID, null, this);
    }

    /**
     * Get the network Information in order to check the network status
     */
    private NetworkInfo getNetworkInfo() {
        //Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        //Return details on the currently active default data network
        return connMgr.getActiveNetworkInfo();
    }

    /**
     * Get the user input in the SearchView
     *
     * @return return user input of String type
     */
    private String getInputQuery() {
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        return searchView.getQuery().toString();
    }
}

