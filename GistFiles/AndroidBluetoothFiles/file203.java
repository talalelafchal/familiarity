package com.example.jesse.gmaps.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jesse.gmaps.model.Comment;
import com.example.jesse.gmaps.R;
import com.example.jesse.gmaps.adapters.WallArrayAdaptor;

import java.util.ArrayList;

public class BtConnectedHubActivity extends AppCompatActivity {
//TODO add to manifest to test even before BT is implemented
    Comment tempComment = new Comment();

    private WallArrayAdaptor commentArrayAdaptor1;
    private ArrayList<Comment> commentArray1 = new ArrayList<Comment>();

    private AdapterView.OnItemClickListener btClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //TODO: 1. try to connect to BT hub 2. respond by changing colour of icon
            //do something in response to button
            String selectedBtName = (String) parent.getAdapter().getItem(position);

            //position = row number that user touched
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_connect);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myChildToolbar);
        //get corresponding action bar for this tool bar
        ActionBar ab = getSupportActionBar();
        //enable up button
        ab.setDisplayHomeAsUpEnabled(true);

        //create the new adaptors passing important params, such as
        // context, android row style and the array of strings to display
        commentArrayAdaptor1 = new WallArrayAdaptor(this, android.R.layout.simple_list_item_2, commentArray1); // what is simple_list_item_1?

        // get handle to the list view in the Activity main layout
        ListView commentListView = (ListView) findViewById(R.id.wall);

        // add action listener for when user click on row
        commentListView.setOnItemClickListener(btClickedHandler);

        //set the adaptor view for list view
        commentListView.setAdapter(commentArrayAdaptor1);

        //TODO: get 1. Hub name 2. Hub Announcement contents 3. wall posts
        //notify the array adaptor that the arrary contents have changed (redraw)
        commentArrayAdaptor1.notifyDataSetChanged();
    }
    // Add buttons from 'menu.appbar' to toolbar when the activity is created
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    public void postComment(View view){
        //TODO: get time and put it in tempComment
        //TODO: after add a wall post is clicked, textbox pops up, after send button is clicked, take comment and put it in hubwall
        tempComment.setTime("current time");
        //TODO: get the user's initial from user class object
        tempComment.setInitials("JC");
        //TODO: send post to server
    }

}

