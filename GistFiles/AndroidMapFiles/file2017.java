package com.example.android.apis.view;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.util.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.app.*;
import android.os.*;
import android.database.*;
import android.net.*;
import android.opengl.*;
import android.graphics.*;
import android.view.animation.*;

import java.util.*;

import org.json.*;


/**
 * A list view example where the 
 * data for the list comes from an array of strings.
 */
public class List1 extends ListActivity implements AdapterView.OnItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
        getListView().setTextFilterEnabled(true);
    }

    //@Override
    //public void onListItemClick(ListView aView, View view, int pos, long id) {
        //System.out.println("++++++++++++++++++++pos" + pos + "++++++++++++++++++++");
    //}
    @Override
    public void onItemClick(AdapterView aView, View view, int pos, long id) {
        System.out.println("++++++++++++++++++++posaaaaaa" + pos + "++++++++++++++++++++");
    }

    private String[] mStrings = Cheeses.sCheeseStrings;
}
