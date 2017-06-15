package ru.ebook.store;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.GridAdapterPublications;
import data.Publication;
import library.API;

/**
 * Created by Artyom on 6/2/13.
 */
public class PublicationActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        GetPublications task=new GetPublications();
        task.genre=getIntent().getExtras().getInt("genre",0);
        task.category=getIntent().getExtras().getInt("category",0);
        task.query=getIntent().getExtras().getString("query", null);
        task.execute();
    }
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Configure the search info and add any event listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                GetPublications task=new GetPublications();
                task.genre=getIntent().getExtras().getInt("genre",0);
                task.category=getIntent().getExtras().getInt("category",0);
                task.query=s;
                task.execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public class GetPublications extends AsyncTask<Void, Void, JSONArray> {
        public int genre=0;
        public int category=0;
        public String query=null;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //TextView textView=(TextView)findViewById(R.id.textView);
            //textView.setText("");
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }
        @Override
        protected JSONArray doInBackground(Void... voids) {
            API api=API.getInstance();
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if(genre!=0){
                params.add(new BasicNameValuePair("genre", String.valueOf(this.genre)));
            }
            if(category!=0){
                params.add(new BasicNameValuePair("category", String.valueOf(this.category)));
            }
            if(query!=null){
                params.add(new BasicNameValuePair("query", this.query));

            }
            try {
                return new JSONArray(api.queryGet("publication", params));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array){
            super.onPostExecute(array);
            //TextView textView=(TextView)findViewById(R.id.textView);
            //textView.setText(object.toString());
            GridView gridViewPublications=(GridView)findViewById(R.id.publicationsLayout);



            gridViewPublications.setAdapter(new GridAdapterPublications(PublicationActivity.this, Publication.fromJSONArray(array)));
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}