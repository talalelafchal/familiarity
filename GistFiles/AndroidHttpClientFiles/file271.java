package net.androidsensei.moviesensei;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LoadMoreMoviesTask extends 
    AsyncTask<Integer, Void, Boolean> {

    private Activity activity;
    private MovieListAdapter adapter;
    private List<Movie> movies = new ArrayList<Movie>();
    private String URL = "http://yts.re/api/list.json?limit=10&set=";
    HttpClient httpclient = new DefaultHttpClient();
    HttpResponse response;
    private ProgressBar progressBar;

    public LoadMoreMoviesTask(ProgressBar progressBar, MainActivity activity, 
                              MovieListAdapter adapter){
        this.progressBar = progressBar;
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Integer... parameters) {
        int npagina = parameters[0];
        String url_api = URL + String.valueOf(npagina);
        try {
            response = httpclient.execute(new HttpGet(url_api));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                JSONObject jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
                JSONArray jArray = jsonObj.getJSONArray("MovieList");
                for(int i=0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String name = jObject.getString("MovieTitle");
                    String image = jObject.getString("CoverImage");
                    Movie p = new Movie();
                    p.setName(name);
                    p.setImage(image);
                    this.movies.add(p);
                }
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result){
            adapter.setData(movies);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}