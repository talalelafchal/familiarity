package com.example.android.searchjsonhttp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Alex on 2017/2/25.
 * Loads a list of booksList by using AsyncTask to perform the network request to the given URL
 */

public class BookListLoader extends AsyncTaskLoader {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = BookListLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;


    /**
     * Constructs a new {@link BookListLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public BookListLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "on StartLoading called");
        forceLoad();
    }

    /**
     * This is on a background thread
     *
     * @return books titles and publishers
     */
    @Override
    public Object loadInBackground() {

        Log.v(LOG_TAG, "loadInBackground called");
        if (mUrl == null) {
            return null;
        }
        //Perform the network request, parse the response, and extract a list of books.
        try {
            List<BookList> bookLists = QueryUtils.fetchBooksData(mUrl);
            return bookLists;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
