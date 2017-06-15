package com.example.android.searchjsonhttp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2017/2/23.
 */

public class QueryUtils {

    /**
     * Tag fot the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed)
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static List<BookList> fetchBooksData(String url) throws IOException {

        //Create URL object which return new URL object form the given string URL
        URL urls = new URL(url);
        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = makeHttpRequest(urls);

        //Extract relevant fields from the JSON response and create an BookList object
        List<BookList> bookLists = extractValuesFormJSON(jsonResponse);

        //Return the BookList
        return bookLists;
    }

    /**
     * Make an HTTP request to the given URL and return a String as response
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        //If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.v(LOG_TAG, "NetWork Response Code: " + urlConnection.getResponseCode());

            //If the request was successful, then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                //Closing the input stream could throw an IOException, which is why
                //the makeHttpRequest(URL url) method signature specifies than an IOException
                //could be thrown
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the whole JSON response from
     * the server
     */
    private static String readFromStream(InputStream inputSteam) throws IOException {
        StringBuilder output = new StringBuilder();

        InputStreamReader inputStreamReader = new InputStreamReader(inputSteam);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();

        while (line != null) {
            output.append(line);
            line = bufferedReader.readLine();
        }
        return output.toString();

    }


    /**
     * Return a list of BookList objects that has been built up from
     * parsing a JSON response
     */
    private static List<BookList> extractValuesFormJSON(String urls) {

        //If the JSON String is empty or null, then return early
        if (TextUtils.isEmpty(urls)) {
            return null;
        }
        BookList customList = null;
        //Create an empty ArrayList that we can start adding booksTitle
        List<BookList> customArrayList = new ArrayList<>();
        String bookAuthors = null;
        String finalAuthors = null;

        //Try to parse urls. If there's a problem with the way the JSON
        //if formatted, a JSONException exception object will be thrown
        //Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(urls);
            JSONArray items = root.getJSONArray("items");

            for (int i = 0; i <= urls.length(); i++) {
                JSONObject firstObject = items.getJSONObject(i);
                JSONObject volumeInfo = firstObject.getJSONObject("volumeInfo");
                String bookTitle = volumeInfo.getString("title");
                Log.v("MainActivity", "Book Title: " + bookTitle);
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    StringBuilder stringBuilder = new StringBuilder();
                    //The authors may not only one, extract all the authors in the JSONArray and
                    //store them into StringBuilder
                    for (int author = 0; author < authors.length(); author++) {
                        bookAuthors = authors.getString(author);
                        stringBuilder.append(bookAuthors + "   ");
                    }
                    finalAuthors = stringBuilder.toString();
                } else {
                    finalAuthors = "Unknown authors";
                }
                customList = new BookList(bookTitle, finalAuthors);
                customArrayList.add(customList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customArrayList;
    }
}


