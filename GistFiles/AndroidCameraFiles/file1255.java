package com.example.kaushal28.locationbasedreminder;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.database.Cursor;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileOutputStream;


public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback{
    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView mName;
    private TextView mAddress;
    private TextView mAttributions;
    private GoogleApiClient mGoogleApiClient;
    public TextView tv4;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private Toolbar toolbar;
    private GoogleMap mMap;
    private boolean flag = false;
    DatabaseHelper myDb;
    EditText newevent;
    Button submit;
    Button viewremainders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main2);
        myDb =new DatabaseHelper(this);
        newevent=(EditText)findViewById(R.id.newEvent);
        submit=(Button)findViewById(R.id.submit);
        viewremainders=(Button)findViewById(R.id.view);

        toolbar = (Toolbar)findViewById(R.id.app_bar0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);          //for back button to main activity.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button pickerButton = (Button) findViewById(R.id.pickerButton);
        tv4 = (TextView)findViewById(R.id.textView4);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(Main2Activity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        AddData();
        viewremainders();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }



    //method for adding data in Database.
    public void AddData(){
        submit.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        boolean isInserted =myDb.insertData(newevent.getText().toString(),tv4.getText().toString());
                        if(isInserted==true)
                            Toast.makeText(Main2Activity.this,"Data Inserted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Main2Activity.this,"Data not Inserted",Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    //Method for view all data from database.
    public void viewremainders(){
        viewremainders.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Cursor res= myDb.getAllData();
                        if(res.getCount()==0)
                        {
                            Showmessage("Error","No remainders found");
                            return;
                        }
                        StringBuffer buffer=new StringBuffer();
                        while(res.moveToNext())
                        {
                            buffer.append("Id : " +res.getString(0)+"\n");
                            buffer.append("Event : " +res.getString(1)+"\n");
                            buffer.append("Location : " +res.getString(2)+"\n");
                        }
                        Showmessage("Data",buffer.toString());

                    }



                }
        );
    }

    public void Showmessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    //opening place picker activity.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();



            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
         //   tv4.setText(place.getLatLng().toString()+"\n"+name+"\n"+address+"\n"+attributions);  To get latitide and longitudes.
            tv4.setText(address+"\n"+attributions);

            LatLngBounds selectedPlaceBounds = PlacePicker.getLatLngBounds(data);
            // move camera to selected bounds
            CameraUpdate camera = CameraUpdateFactory.newLatLngBounds(selectedPlaceBounds,0);
            mMap.moveCamera(camera);

            // take snapshot and implement the snapshot ready callback
            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                Bitmap bitmap=null;
                public void onSnapshotReady(Bitmap snapshot) {
                    // handle snapshot here
                    bitmap = snapshot;
                    try {
                        FileOutputStream out = new FileOutputStream("/mnt/sdcard/Download/bounty.png");
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Methods for toolbar


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}


Stack trace:


08-30 18:44:20.630 15611-15611/com.example.kaushal28.locationbasedreminder E/test: Exception
08-30 18:44:20.637 15611-15611/com.example.kaushal28.locationbasedreminder E/AndroidRuntime: FATAL EXCEPTION: main
                                                                                             java.lang.RuntimeException: Failure delivering result ResultInfo{who=null, request=1, result=-1, data=Intent { (has extras) }} to activity {com.example.kaushal28.locationbasedreminder/com.example.kaushal28.locationbasedreminder.Main2Activity}: java.lang.NullPointerException
                                                                                                 at android.app.ActivityThread.deliverResults(ActivityThread.java:3488)
                                                                                                 at android.app.ActivityThread.handleSendResult(ActivityThread.java:3531)
                                                                                                 at android.app.ActivityThread.access$1100(ActivityThread.java:156)
                                                                                                 at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1388)
                                                                                                 at android.os.Handler.dispatchMessage(Handler.java:99)
                                                                                                 at android.os.Looper.loop(Looper.java:153)
                                                                                                 at android.app.ActivityThread.main(ActivityThread.java:5299)
                                                                                                 at java.lang.reflect.Method.invokeNative(Native Method)
                                                                                                 at java.lang.reflect.Method.invoke(Method.java:511)
                                                                                                 at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:833)
                                                                                                 at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:600)
                                                                                                 at dalvik.system.NativeStart.main(Native Method)
                                                                                              Caused by: java.lang.NullPointerException
                                                                                                 at com.example.kaushal28.locationbasedreminder.Main2Activity.onActivityResult(Main2Activity.java:185)
                                                                                                 at android.app.Activity.dispatchActivityResult(Activity.java:5371)
                                                                                                 at android.app.ActivityThread.deliverResults(ActivityThread.java:3484)
                                                                                                 at android.app.ActivityThread.handleSendResult(ActivityThread.java:3531) 
                                                                                                 at android.app.ActivityThread.access$1100(ActivityThread.java:156) 
                                                                                                 at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1388) 
                                                                                                 at android.os.Handler.dispatchMessage(Handler.java:99) 
                                                                                                 at android.os.Looper.loop(Looper.java:153) 
                                                                                                 at android.app.ActivityThread.main(ActivityThread.java:5299) 
                                                                                                 at java.lang.reflect.Method.invokeNative(Native Method) 
                                                                                                 at java.lang.reflect.Method.invoke(Method.java:511) 
                                                                                                 at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:833) 
                                                                                                 at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:600) 
                                                                                                 at dalvik.system.NativeStart.main(Native Method) 