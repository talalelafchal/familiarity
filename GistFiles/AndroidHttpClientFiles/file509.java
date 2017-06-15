package com.example.joona.movies;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WinNabuska on 6.8.2015.
 */
public class MovieContent {

    private MovieDataAdapter dbAdapter;
    public static List<Movie> ITEMS = new ArrayList<Movie>();
    public static Map<String, Movie> ITEM_MAP = new HashMap<String, Movie>();

    public MovieContent(Context context){
        dbAdapter = new MovieDataAdapter(context);
        dbAdapter.open();
        for(HashMap<String,String> current : dbAdapter.getAllContent()){
            addListItem(new Movie(current));
        }
        dbAdapter.close();
    }
    protected static void addListItem(Movie movie){
        ITEMS.add(movie);
        ITEM_MAP.put(movie.id, movie);
    }

    public void addNewCreatedMovie(Movie newMovie){
        dbAdapter.open();
        Log.i("info", "try insert");
        if(dbAdapter.insertEntry(newMovie.movieInfo)){
            addListItem(newMovie);
            Log.i("info", "movie inserted");
        }
        else{
            Log.i("info", "insert failed");
        }
        dbAdapter.close();

    }

    public static class Movie {
        public final String id;
        private static int count = 0;
        public final HashMap<String, String> movieInfo;


        public Movie(HashMap<String, String> movieInfo) {
            this.id = String.valueOf(++count);
            this.movieInfo = movieInfo;
        }

        @Override
        public String toString() {
            return movieInfo.get(MovieDataAdapter.TITLE) + " (" + movieInfo.get(MovieDataAdapter.YEAR) + ")";
        }
    }
}
