package com.gamblore.test.actionbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gamblore.widget.ActionBarActivity;
import com.gamblore.widget.ActionButton;

public class TestActionBarActivity extends ActionBarActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        ActionButton ab = new ActionButton(this);
        ab.setIcon(android.R.drawable.ic_menu_gallery);
        ab.setTitle("Resource Gallery");
        ab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), ResourceExplorerActivity.class);
				startActivity(i);
			}
		});
        getActionBar().addAction(ab);
        
        ab = new ActionButton(this);
        ab.setIcon(android.R.drawable.ic_menu_mapmode);
        ab.setTitle("Map");
        ab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Map", Toast.LENGTH_SHORT).show();
				
			}
		});
        getActionBar().addAction(ab);
        
        ab = new ActionButton(this);
        ab.setIcon(android.R.drawable.ic_menu_search);
        ab.setTitle("Search");
        ab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Search", Toast.LENGTH_SHORT).show();
				
			}
		});
        getActionBar().addAction(ab);
    }
    
}