package sudharshanapps.clock;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.HashMap;
import java.util.TimeZone;
import java.util.Calendar;
import java.io.InputStream;
import java.io.Writer;
import java.io.Reader;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.widget.AdapterView;


import static sudharshanapps.clock.Constants.FIRST_COLUMN;
import static sudharshanapps.clock.Constants.SECOND_COLUMN;


// I am extending Activity class to create my own activity of displaying time in different parts of world

public class Time extends Activity {

    // Original hour format counter 0 = 24 hour format; 1 = 12 hour format
    private int hour_counter = 0;

    //Reference to hold current Object of Public class instance Time
    //This is used as a lever to change Adapter
    private Time object;

    //Public class to driver Adapter customization instead of using arrayadapters etc.
    private TwocolumnAdapter listAdapter;

    //Array Lists, which contain two types of data 12 and 24 hour format
    private ArrayList<HashMap<String,String>> stringArrayList = new ArrayList<>();

    // This is mandatory\default function which fill menu of options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem _clockformat = menu.findItem(R.id.search_button);

        if(hour_counter == 1){
            _clockformat.setTitle("Time in 24 Hour Format");
        }else {
            _clockformat.setTitle("Time in 12 Hour Format");
        }

        return super.onPrepareOptionsMenu(menu);
    }

    // Function invoked when About us option is choosed in Menu
    private void aboutUs() {

        // Create an instance of Alert box
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Set a title to Alert box created
        alertDialog.setTitle("About Time across Globe");

        //Prepare list of content to be displayed in list format
        String[] users = {"Version 1.1", "Author:Sudharshan Vaddi", "Contact:feedback@gmail.com"};

        // This function assigns prepared list content to static alert box
        alertDialog.setItems(users, null);

        //This function creates an OK button for instantiated Static alert box
        alertDialog.setPositiveButton("OK", null);

        // Show to user static alert box just now created
        alertDialog.show();
    }

    // Function invoked when settings option is chosen
    private void configure(){

        hour_counter = (hour_counter ==0) ? 1:0;
        //Call Function to include new time format changes
        listAdapter.ChangeTimeFormat(hour_counter);

    }

    // mandatory \default function created for menus
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //Select Item in menu based on ID
        switch (item.getItemId()) {
            // Invoke About Us section
            case R.id.action_settings:
                aboutUs();
                break;

            //Invoke Options section
            case R.id.search_button:
                configure();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //Function used for reading list of timezones from JSON File
    private String readFile(){

        // Select resource(JSON File) using file name
        InputStream is = getResources().openRawResource(R.raw.timezone);

        //Select a handle for writing data as string
        Writer writer = new StringWriter();

        //Read a standard amount each time
        char[] buffer = new char[1024];

        try {
            // Read file using input stream
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e){
            //Exception handling
            e.printStackTrace();
        }

        //Convert all data to string format
        return writer.toString();

    }

    //Function used to read specific data from JSON string created by readFile() function
    private ArrayList<HashMap<String,String>> parseJSONString(String jsonString){


        try {
            // Creating JSONObject from String
            JSONObject jsonObjMain = new JSONObject(jsonString),jsonObj;

            // Creating JSONArray from JSONObject
            JSONArray jsonArray = jsonObjMain.getJSONArray("TimeZones");

            //Strings to hold 24 hour, 12 hour times, AM\PM details, Offset values, Time Zone name and Zone data
            String time24hour, variation,name, zone;
            TimeZone tz;
            Calendar c;
            // HashMaps for twentyfour and twelve hour format
            HashMap<String,String> twentyfourhour;

            // JSONArray has four JSONObject
            for (int i = 0; i < jsonArray.length(); i++) {

                // Creating JSONObject from JSONArray
                jsonObj = jsonArray.getJSONObject(i);

                // Getting data from individual JSONObject OFFSET Value and time zone name

                variation = jsonObj.getString("offset");
                name = jsonObj.getString("value");

                //Formulating complete GMT Zone value
                zone = "GMT" + variation;

                //Get time of current zone
                tz = TimeZone.getTimeZone(zone);

                //Get current calendar instance
                c = Calendar.getInstance(tz);

                time24hour = String.format("%02d" , c.get(Calendar.HOUR_OF_DAY))+":"+
                         String.format("%02d" , c.get(Calendar.MINUTE));


                twentyfourhour=new HashMap<>(); //Prepare Hashmaps with indices
                twentyfourhour.put(FIRST_COLUMN, name);
                twentyfourhour.put(SECOND_COLUMN, time24hour);

                stringArrayList.add(twentyfourhour); //add to arraylist
               }

        } catch (JSONException e) {
            //Exception handling
            e.printStackTrace();
        }

        //returning 24 hour format data by default

        return stringArrayList;
    }




    // This is invoked when ListView is created and loaded to system

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Assigning main activity name for drawing
        setContentView(R.layout.activity_time);

        // Find the ListView resource and assign to ListView Object created
        ListView mainListView = (ListView) findViewById(R.id.mainListView);

        // Initialize adapter with data by parsing JSON
        listAdapter = new TwocolumnAdapter(this, parseJSONString(readFile()));

        //Saving the reference to prepare new adapter when reload is needed
        object = this;

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);

        // Assigning Onclick event to list view rows
        mainListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

                // Creating Alert Box
                AlertDialog.Builder adb = new AlertDialog.Builder(object);

                //adb.setTitle("Added to Favourites");
                adb.setTitle("Time Zone Added to Favourites");

                // Adding Ok Button
                adb.setPositiveButton("OK", null);

                //Displaying Dialog Box
                adb.show();
            }
        });

    }

}
