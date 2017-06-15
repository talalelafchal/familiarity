package com.sgt_tibs.demo.listcolor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lt_tibs on 9/15/15.
 */
public class ActivityTwo extends Activity {

    // Options

    // selected color
    SimpleAdapter adapter;

    List<RowItem> mItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        ListView listView = (ListView) findViewById(R.id.listView);
        Button buttonStartActivityThree = (Button) findViewById(R.id.buttonActivityThree);
        buttonStartActivityThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityThree = new Intent(ActivityTwo.this, ActivityThree.class);
                startActivity(activityThree);
            }
        });

        // if this is the first time create the items
        if(savedInstanceState == null) {
            // Create the simple adapter
            mItems = createItems();
            adapter = new SimpleAdapter(this, mItems);

            listView.setAdapter(adapter);


            // Call to update the selected items
            updateSelectedItems();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Store the listItem and color in the Map
                RowItem item = mItems.get(position);

                // toggle selection
                if (item.selected) {
                    item.selected = false;
                } else {
                    item.selected = true;
                }

                // update the adapter
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the items selected state and text to preferences
        SharedPreferences preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Loop through each item and save it off
        for(int i = 0 ; i < mItems.size();i++){
            editor.putBoolean("selected_" + i, mItems.get(i).selected);
            editor.putString("text_"+ i, mItems.get(i).text);
        }
        editor.apply();
    }

    private void updateSelectedItems(){

        SharedPreferences preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        // Loop through each item and save it off
        for(int i = 0 ; i < mItems.size(); i++){
            RowItem item = mItems.get(i);
            item.selected = preferences.getBoolean("selected_" + i, mItems.get(i).selected);
            item.text = preferences.getString("text_" + i, mItems.get(i).text);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Creates 50 items that are unselected
     * @return
     */
    private ArrayList<RowItem> createItems(){
        List<RowItem> items = new ArrayList<>();
        for(int i = 0; i < 50; i++){
            RowItem item = new RowItem();
            item.text = "Row Item "+ i;
            item.selected = false;
            items.add(item);
        }
        return (ArrayList<RowItem>) items;
    }
}
