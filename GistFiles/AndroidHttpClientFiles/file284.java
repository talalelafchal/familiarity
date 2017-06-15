package com.example.joona.movies;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity
        implements      UpperFragment.OnFragmentInteractionListener,
        /*implements*/  LowerListFragment.OnFragmentInteractionListener,
        /*implements*/  LowerFragmentMovieDefinition.OnFragmentInteractionListener{

    MovieContent movieContent;
    protected MovieContent.Movie selectedMovie;
    private LowerListFragment lowerListFragment;
    private ASyncTastJSON async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieContent = new MovieContent(getApplicationContext());

        async = new ASyncTastJSON();
        async.execute("Matrix");
       /* MyJSONDataMiner miner = new MyJSONDataMiner();
        HashMap<String, String> movieInfo = new HashMap<>();
        miner.execute("Matrix");*/

        //movieInfo.put(MovieDataAdapter.TITLE, "Batman");
        //movieInfo.put(MovieDataAdapter.YEAR, "1989");
        //movieInfo.put(MovieDataAdapter.POSTER, "http://ia.media-imdb.com/images/M*//MV5BMTYwNjAyODIyMF5BMl5BanBnXkFtZTYwNDMwMDk2._V1_SX300.jpg");
        //MovieContent.Movie newMovie = new MovieContent.Movie(movieInfo);
        //ovieContent.addNewCreatedMovie(newMovie);
              /*Search search = new Search();search.execute("");*/
        lowerListFragment = new LowerListFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.upper_main_frame, new UpperFragment());
        fragmentTransaction.add(R.id.lower_main_frame, lowerListFragment);
        fragmentTransaction.commit();
        //TODO implement JSON ?s='PARTIAL-movie-name' search function
    }

    public class ASyncTastJSON extends AsyncTask<String, Void, HashMap<String, String>> {

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

        public ASyncTastJSON() {
            jsonKeys = new ArrayList<>();
            jsonKeys.add(TITLE);
            jsonKeys.add(YEAR);
            jsonKeys.add(RATED);
            jsonKeys.add(RELEASED);
            jsonKeys.add(RUNTIME);
            jsonKeys.add(GENRE);
            jsonKeys.add(DIRECTOR);
            jsonKeys.add(ACTOR);
            jsonKeys.add(PLOT);
            jsonKeys.add(LANGUAGE);
            jsonKeys.add(COUNTRY);
            jsonKeys.add(AWARDS);
            jsonKeys.add(POSTER);
            jsonKeys.add(METASCORE);
            jsonKeys.add(IMDB_RATING);
            jsonKeys.add(IMDB_VOTES);
            jsonKeys.add(IMDB_ID);
            jsonKeys.add(TYPE);
            jsonKeys.add(RESPONCE);
        }

        @Override
        protected HashMap<String, String> doInBackground(String... params) {

            String movieName = params[0].trim();
            movieName = movieName.replace(" ", "%20");
            //url: get movie by title
            String stringUri = "http://www.omdbapi.com/?t=" + movieName;
            Log.i("info", "page: " + stringUri);
            HashMap<String, String> movieData = new HashMap<>();

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(stringUri));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = in.readLine()) != null)
                    sb.append(line);
                in.close();
                JSONObject jsonObject = new JSONObject(sb.toString());
                if (jsonObject.getBoolean("Response")) {
                    for (String currentKey : jsonKeys)
                        movieData.put(currentKey, jsonObject.getString(currentKey));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return movieData;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
            super.onPostExecute(stringStringHashMap);
            movieContent.addNewCreatedMovie(new MovieContent.Movie(stringStringHashMap));
        }
    }

    private void onListMovieClick(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.lower_main_frame, new LowerFragmentMovieDefinition());
        fragmentTransaction.addToBackStack(null);//addBackStack takes String as parameter?
        fragmentTransaction.commit();
        /*try {
            ((TextView) movieDefinition.getView().findViewById(R.id.fra)).setText("hei");
        }catch (Exception e){Log.i("info", "ERROR INFO " + e.getCause().toString());}*/

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("info", uri.toString());
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.i("info", "Listfragment interraction id = "  + id);
        Log.i("info", MovieContent.ITEM_MAP.get(id).toString());
        selectedMovie = MovieContent.ITEM_MAP.get(id);
        onListMovieClick();
    }
}
