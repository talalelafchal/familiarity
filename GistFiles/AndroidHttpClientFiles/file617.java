
package com.android.demo.notepad3;



import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Main extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_UPDATE=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private NotesDbAdapter mDbHelper;
    private Cursor notesCursor;
	private Cursor currentVCursor;
	private Cursor checkVCursor;
	
    private static final String DATABASE_CREATE6 =
        "create table CheckVersions (_id integer primary key autoincrement, "
        + "file text, versionNum text);";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.notes_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
       fillData();
        registerForContextMenu(getListView());
        
        
        /**Comment out if loading data into database for first time.. **/
       getVersions();
       checkVersions();
       
    }
    

    public void checkVersions(){
    	
    
    	
    	  currentVCursor = mDbHelper.fetchCurrentVersions();
    	  checkVCursor = mDbHelper.fetchCheckVersions();
    	boolean uptodate = true;
    	  
          currentVCursor.moveToFirst();
          checkVCursor.moveToFirst();
          int colCount = 0;
          while (colCount <=3 ) {
        	 // System.out.println(currentVCursor.getColumnCount());
        	 // System.out.println(checkVCursor.getColumnCount());
        	 // System.out.println(currentVCursor.getColumnNames().toString());
        	  System.out.println("Old version Num: " + currentVCursor.getString(2) + "Current version Num: " + checkVCursor.getString(2));
              if(!currentVCursor.getString(2).equals(checkVCursor.getString(2))){
            	String table =  currentVCursor.getString(1);
            	uptodate = false;
            	if(table.equals("routes.xml")){
            		mDbHelper.EraseTable("Routes");
            		 Intent i = new Intent(this, HttpRequest.class);
            		 i.putExtra("myPassedType", "Update");
            	       i.putExtra("myPassedTable", "routes");
            	        startActivityForResult(i, ACTIVITY_UPDATE);
            		
            	}
            	else if(table.equals("-------------stop_times.xml")){
            		mDbHelper.EraseTable("StopTimes");
           		 Intent i = new Intent(this, HttpRequest.class);
           		 i.putExtra("myPassedType", "Update");
           	       i.putExtra("myPassedTable", "stop_times");
           	        startActivityForResult(i, ACTIVITY_UPDATE);
            	}
            	
            	else if(table.equals("----------------------stops.xml")){
            		mDbHelper.EraseTable("Stops");
           		 Intent i = new Intent(this, HttpRequest.class);
           		 i.putExtra("myPassedType", "Update");
           	       i.putExtra("myPassedTable", "stops");
           	        startActivityForResult(i, ACTIVITY_UPDATE);
            	}
            	else if(table.equals("trips.xml")){
            		mDbHelper.EraseTable("Trips");
           		 Intent i = new Intent(this, HttpRequest.class);
           		 i.putExtra("myPassedType", "Update");
           	       i.putExtra("myPassedTable", "trips");
           	        startActivityForResult(i, ACTIVITY_UPDATE);
            	}
              }
              colCount ++;
              currentVCursor.moveToNext();
              checkVCursor.moveToNext();
            }
          if (uptodate == true){Toast.makeText(Main.this, "Data already uptodate", Toast.LENGTH_SHORT).show();}
    }

    private void fillData() {
        // Get all of the rows from the database and create the item list
        notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.route_longName, NotesDbAdapter.route_shortName};
 
        // and an array of the fields wewant to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1,R.id.text2};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes =  new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //InsertID is the id of the 'button' and menu_inster is looking up the text value to display.
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID: // does the thing based on the 'button id'
              createNote();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, HttpRequest.class);
        i.putExtra("myPassedType", "Normal");
        startActivityForResult(i, ACTIVITY_CREATE);
        //startActivity(i);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        boolean checked = false;
        int direction = -1;
    	final RadioGroup selectedDirectionGroup  = (RadioGroup) findViewById(R.id.directionRadioGroup);
		for(int i=0; i<selectedDirectionGroup.getChildCount(); i++) {
			RadioButton btn = (RadioButton) selectedDirectionGroup.getChildAt(i);
			if(btn.isChecked()) {
				if(btn.getText().equals("Outbound")){
					checked = true;
					direction = 0;					
				}
				else{ direction = 1;checked = true;}
				
				//Toast.makeText(TabExample.this, btn.getText(), Toast.LENGTH_SHORT).show();
			}
		}
        if(checked == true){
        Intent i = new Intent(this, TripDisplayAc.class);
       i.putExtra(NotesDbAdapter.trip_ROWID, id);
       i.putExtra("myDir", direction);
       startActivityForResult(i, ACTIVITY_CREATE);
        }
        else{Toast.makeText(Main.this, "Please select a direction", Toast.LENGTH_SHORT).show();}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
   public void getVersions(){
	   mDbHelper.EraseTable("checkVersions");
	  // InputStream fileStream = null;
	InputStream stream = null;
	// File file = new File("versions-3.xml");
	// try {
	//	fileStream = new BufferedInputStream( new FileInputStream(file));
	//} catch (FileNotFoundException e1) {
	
	//	e1.printStackTrace();
	//}
	
   	HttpPost post = null;
       try
   	{
   		HttpClient hc = new DefaultHttpClient();
   		

   		post = new HttpPost("http://homepages.ecs.vuw.ac.nz/~ian/nwen304/versions.xml");
   		HttpResponse rp = hc.execute(post);

   		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
   		{
   			HttpEntity entity = rp.getEntity();
       		 stream = entity.getContent();
   		}
   	}catch(IOException e){
   		e.printStackTrace();
   	}  

 	 
 	    try {
 	    	XmlPullParser parser = Xml.newPullParser();
 			System.out.println("Parsing CheckVersion");
 			
			// auto-detect the encoding from the stream
 			parser.setInput(stream, null);
 			int eventType = parser.getEventType();
 			Version currentVersion = null;
 			boolean done = false;

 			while (eventType != XmlPullParser.END_DOCUMENT && !done){
 				
 				String name = null;
 				switch (eventType){
 				case XmlPullParser.START_DOCUMENT:
 			
 					break;
 				case XmlPullParser.START_TAG:
 					name = parser.getName();
 					System.out.println(name);

 					if (name.equalsIgnoreCase("document")){
 						//skip over it
 					} 
 					if (name.equalsIgnoreCase("record")){
 						currentVersion = new Version();
 					} 
 					else if (currentVersion != null){
 						if (name.equalsIgnoreCase("data")){
 							currentVersion.file = parser.nextText();
 							//System.out.println(currentStopTime.tripID);
 						} else if (name.equalsIgnoreCase("version")){
 							currentVersion.version = parser.nextText();
 							//System.out.println(parser.nextText());
 						} 
 				}
 				break;
 			case XmlPullParser.END_TAG:
 				name = parser.getName();
 				if (name.equalsIgnoreCase("record") && currentVersion != null){
 					//System.out.println(currentStopTime.tripID+ " " +  currentStopTime.arrivalTime + " " + currentStopTime.departureTime + " " + currentStopTime.stopID);
 					mDbHelper.insertCheckVersion(currentVersion.file, currentVersion.version);
 					System.out.println("--------------------------------------------------------------Adding a Record");
 				} else if (name.equalsIgnoreCase("document")){
 					//Outties!!
 					System.out.println("--------------------------------------------------------------END");
 					done = true;
 				}
 				break;
 			}

 			eventType = parser.next();
 		}

 	} catch (Exception e) {
 		Log.e("AndroidNews::PullFeedParser", e.getMessage(), e);
 		throw new RuntimeException(e);
 	}


 	}
   
   }
    	
    

