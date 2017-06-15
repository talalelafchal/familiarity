package com.example.ContactForm;

import android.app.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AvatarListActivity extends Activity {

    // All static variables
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_THUMB_URL = "thumb_url";

    ListView list;
    AvatarListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        ArrayList<HashMap<String, String>> avatarList = new ArrayList<HashMap<String, String>>();

        for (int i = 1; i <= 13; i++) {

            String img;
            if(i<10)img = "a0"+i+".png";
            else img = "a"+i+".png";
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(KEY_ID, "key"+i);
            map.put(KEY_TITLE, "Avatar Name");
            map.put(KEY_THUMB_URL, "http://onoapps.com/Dev/avatars/"+img);

            // adding HashList to ArrayList
            avatarList.add(map);
        }

        list=(ListView)findViewById(R.id.list);

        // Getting adapter by passing data ArrayList
        adapter=new AvatarListAdapter(this, avatarList);
        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });
    }


}