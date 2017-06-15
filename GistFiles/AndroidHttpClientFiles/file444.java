package com.android.demo.notepad3;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TimetableDisplayAc extends Activity {

    public ArrayList<String> stopIDList = new ArrayList<String>();
    public ArrayList<String> stopNameList = new ArrayList<String>();
    public ArrayList<String> arrivalTimeList = new ArrayList<String>();
    public ArrayList<String> finalList = new ArrayList<String>();
 String passedTripID = "";
    private NotesDbAdapter mDbHelper;
    private Cursor notesCursor;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.timetable);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
                
              Bundle extras = getIntent().getExtras();
               passedTripID = extras != null ? extras.getString("passedTripID")
                                        : null;

               fillTimetable(passedTripID);
   
                   ListView lv1=(ListView)this.findViewById(R.id.ListView01);  
                   ArrayAdapter list1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalList);  
                   lv1.setAdapter(list1);  

    }

   
    private void fillTimetable(String passedTripID) {
    	//Clear lists each time
    	stopIDList.clear();
    	arrivalTimeList.clear();
    	stopNameList.clear();
    	
    	//Get arrival times and store in list
        notesCursor = mDbHelper.fetchStopTimes(passedTripID);
        startManagingCursor(notesCursor);
        notesCursor.moveToFirst();
         while (notesCursor.isAfterLast() == false) {
          	stopIDList.add(notesCursor.getString(2));
          	arrivalTimeList.add(notesCursor.getString(1));
              System.out.println(notesCursor.getString(2) );
               notesCursor.moveToNext();
            }
         
         //get stop Names and store in list
         for (int i = 0; i < stopIDList.size(); i++){ 
        	 notesCursor = mDbHelper.fetchStopName(stopIDList.get(i));
        	 startManagingCursor(notesCursor);
             notesCursor.moveToFirst();
             stopNameList.add(notesCursor.getString(1));
         }
        
         //append both arrival times and stop times into one list
        for (int i = 0; i< arrivalTimeList.size(); i++){
        	finalList.add(arrivalTimeList.get(i) + " - " + stopNameList.get(i));
        	System.out.println(arrivalTimeList.get(i) + " - " + stopNameList.get(i));
        }
        
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //fillData();
    }
    

}

