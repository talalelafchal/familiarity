package com.example.joona.movies;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joona on 8/6/2015.
 */
public class MyJSONDataMiner extends AsyncTask<String, Void, HashMap<String, ArrayList<String>>> {

    public final static String TITLE = "Title";
    public final static String YEAR = "Year";
    public final static String RATED = "Rated";
    public final static String RELEASED = "Released";
    public final static String RUNTIME = "Runtime";
    public final static String GENRE = "Genre";
    public final static String DIRECTOR = "Director";
    public final static String ACTOR = "Actors";
    public final static String PLOT = "Plot";
    public final static String LANGUAGE = "Language";
    public final static String COUNTRY = "Country";
    public final static String AWARDS = "Awards";
    public final static String POSTER = "Poster";// url jpg image url
    public final static String METASCORE = "Metascore";
    public final static String IMDB_RATING = "imdbRating";
    public final static String IMDB_VOTES = "imdbVotes";
    public final static String IMDB_ID = "imdbID";
    public final static String TYPE = "Type"; //movie, vide game, book... etc.
    public final static String RESPONCE = "Response";//type boolean
    public final ArrayList<String> jsonKeys;


    public MyJSONDataMiner(){
        jsonKeys = new ArrayList<>();
        jsonKeys.add(TITLE);    jsonKeys.add(YEAR);     jsonKeys.add(RATED);        jsonKeys.add(RELEASED);
        jsonKeys.add(RUNTIME);  jsonKeys.add(GENRE);    jsonKeys.add(DIRECTOR);     jsonKeys.add(ACTOR);
        jsonKeys.add(PLOT);     jsonKeys.add(LANGUAGE); jsonKeys.add(COUNTRY);      jsonKeys.add(AWARDS);
        jsonKeys.add(POSTER);   jsonKeys.add(METASCORE);jsonKeys.add(IMDB_RATING);  jsonKeys.add(IMDB_VOTES);
        jsonKeys.add(IMDB_ID);  jsonKeys.add(TYPE);     jsonKeys.add(RESPONCE);
    }

    @Override
    protected HashMap<String, ArrayList<String>> doInBackground(String... params) {

        String movieName = params[0].trim();
        movieName = movieName.replace(" ", "%20");
        //url: get movie by title
        String stringUri = "http://www.omdbapi.com/?t=" + movieName;
        Log.i("info", "page: " + stringUri);
        HashMap<String, ArrayList<String>> movieData = new HashMap<>();

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(stringUri));
            HttpResponse response = client.execute(request);

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            Log.i("info", sb.toString());

            JSONObject jsonObject = new JSONObject(sb.toString());

            if (jsonObject.getBoolean("Response")) {

                String ratedStr = jsonObject.getString("Rated");

                Log.i("info", "Rated == " + ratedStr);
                //TODO status?

                //JSONArray array = jsonObject.getJSONArray("results");
                //TODO results?
                //JSONObject item = array.getJSONObject(0);

                //JSONObject rated = resp.getJSONObject("Rated");

                //String ratedstr = rated.getString("")

                //coordinates[0] = point.getDouble("lat");
                //coordinates [1] = point.getDouble("lng");
            }
        }catch(Exception e){e.printStackTrace();}
        return movieData;
    }
}
