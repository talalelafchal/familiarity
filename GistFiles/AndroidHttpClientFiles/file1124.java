package com.example.untitled3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.example.untitled3.pages.mypage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyActivity extends Activity {
    private Context mthis;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ArrayList<Feed> tweets = getFeeds("android", 0);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new FeedItemAdapter(this, R.layout.item, tweets));
        //listView.setClickable(true);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("zemin", "View Click");

                // Launching new Activity on selecting single List Item
                //Intent ix = new Intent(MyActivity.this, mypage.class);
                // sending data to new activity
              //  ix.putExtra("product", "222");
                //startActivity(ix);
            }
        });


    }



    public void myBtn(View v) {

        Intent ix = new Intent(MyActivity.this, mypage.class);
        // sending data to new activity
        ix.putExtra("product", "222435345");
        startActivity(ix);
    }

    public ArrayList<Feed> getFeeds(String searchTerm, int page) {
        ArrayList<Feed> feedArrayList = new ArrayList<Feed>();
        try {
            JSONParser jp = new JSONParser();
            JSONObject json = jp.getJSONFromUrl("http://www.frasb.net/mapi/index.php/profile/feeds/?timestamp=0&id=52482&token=bd236a625770327b0f5b31eaa5f5bb8c0050c7b5&reverse=0");
            JSONArray jarray = json.getJSONArray("data");


            for (int i = 0; i < jarray.length(); i++) {
                JSONObject o = jarray.getJSONObject(i);
                Log.i("zemin", o.getString("content"));

                Feed feed = new Feed(o.getJSONObject("sender_user").getString("fullname"), o.getString("content"), o.getJSONObject("sender_user").getJSONObject("image").getString("large"));
                feedArrayList.add(feed);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return feedArrayList;
    }


}


