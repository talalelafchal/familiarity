package com.example.joona.movies;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends Activity implements UpperFragment.OnFragmentInteractionListener, LowerListFragment.OnFragmentInteractionListener, LowerFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyJSONDataMiner miner = new MyJSONDataMiner();
        miner.execute("Matrix");
        /*Search search = new Search();
        search.execute("");*/
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.upper_main_frame, new UpperFragment());
        fragmentTransaction.add(R.id.lower_main_frame, new LowerListFragment());
        fragmentTransaction.commit();
    }

    /*public class Search extends AsyncTask<String, Void, HashMap<String, ArrayList<String>>> {
        @Override
        protected HashMap<String, ArrayList<String>> doInBackground(String... params) {


            *//*Double[] coordinates = new Double[2];

            String stringHTTP1 =
                    "http://maps.googleapis.com/maps/api/geocode/json?address=";
            String stringHTTP2 = "&sensor=false";
            String stringHTTP = stringHTTP1 + params[0] + stringHTTP2;
            stringHTTP = deleteBlanks(stringHTTP);*//*

            *//*System.out.println(stringHTTP);*//*

            *//*Introduction to Android programming
            Prof. Antonio García Cabot
            Prof. Eva García López
            9 / 10*//*
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI("http://www.omdbapi.com/?t=fantastic%20four"));
                HttpResponse response = client.execute(request);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null)
                {
                    sb.append(line);
                }

                in.close();

                JSONObject jsonObject = new
                        JSONObject(sb.toString());
                String ratedStr = jsonObject.getString("Rated");

                Log.i("info", "rated = " + ratedStr);


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }*/

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("info", uri.toString());
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.i("info", "Listfragment interraction id = "  + id);
    }
}
