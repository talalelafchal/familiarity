package com.android.demo.notepad3;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TripDisplayAc extends ListActivity {
    private static final int ACTIVITY_CREATE=0;


    public ArrayList<String> tripIDList = new ArrayList<String>();
 int passedDir = -1;
    private NotesDbAdapter mDbHelper;
    private Cursor notesCursor;
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.notes_list2);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
                
        Long passedRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.trip_ROWID);
        if (passedRowId == null) {
            Bundle extras = getIntent().getExtras();
            passedRowId = extras != null ? extras.getLong(NotesDbAdapter.trip_ROWID)
                                    : null;
            
            
         
              Bundle extras2 = getIntent().getExtras();
               passedDir = extras2 != null ? extras.getInt("myDir")
                                        : null;
        }

     fillTrips(passedRowId,passedDir);
     registerForContextMenu(getListView());
        
    }

   
    private void fillTrips(Long passedRowId, int passedDir) {
        // Get all of the rows from the database and create the item list
        notesCursor = mDbHelper.fetchTrips(passedRowId,passedDir);
    	// notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);


        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.trip_tripID};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
       SimpleCursorAdapter notes =  new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
       setListAdapter(notes);
    }
    


    @Override
   protected void onListItemClick(ListView l, View v, int position, long id) {
      super.onListItemClick(l, v, position, id);
      
      //gets tripID based on what u click on.
      TextView textView = (TextView) v.findViewById(R.id.text1);
      String passedTripID = textView.getText().toString(); 
      
   // Toast.makeText(TripDisplayAc.this, text, Toast.LENGTH_SHORT).show();
      Intent i = new Intent(this, TimetableDisplayAc.class);
      i.putExtra("passedTripID", passedTripID);
      startActivityForResult(i, ACTIVITY_CREATE);
      
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //fillData();
    }
}
