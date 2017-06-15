//Main method of app that uses Google Maps API to fetch and display data from arduino chips via bluetooth.
//More or less just a sketch, an outline of the general process

package com.example.nicnowak.okeydokey1;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //  1. Get map coordinates somehow
        //  2. Add markers with corresponding coordinates
        //  3. Marker will point to a formatted table of data
        //  4. Marker will change color when the user is in bluetooth range.
        //  5. The user connects to the chip by clicking on said color-coded marker.

        mMap = googleMap;

        // The coordinates of chips go here
        LatLng mqt1 = new LatLng(46, -87);
        LatLng mqt2 = new LatLng(46.001, -87.001);

        //Add some markers
        chip1 = mMap.addMarker(new MarkerOptions().position(mqt1).title("Your Science Here"));
        chip2 = mMap.addMarker(new MarkerOptions().position(mqt2).title("Another Marker"));

        //Set the markers to point to 0, will eventually point to data.
        chip1.setTag(0); chip2.setTag(0);

        //Center map on markers
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mqt1));

        //Wait for marker click.
        mMap.setOnMarkerClickListener((OnMarkerClickListener) this);

    }
    
    @Override
    public boolean onMarkerClick(final Marker marker) {
        //This method is called when somebody clicks the marker.
        //It will display a formatted table of data.
        //This is probably where we want to read the data from the chip.

        //Turn on bluetooth if disabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        //Connect to arduino and download file


            //*************    YOUR CODE HERE??!!   ******************


        //Open file from arduino and create HTML table.

            //***********     MY CODE HERE?!?!      ***********

        //Just n sample HTML table.
        String example_table = "<table border=1>" +
                "<tr>" +
                "<td>Time goes here</td>" +
                "<td>Hiker Count goes here</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Time goes here</td>" +
                "<td>Hiker count goes here</td>" +
                "</tr>" +
                "</table>";

        //Load table into Webview object
        Webview arduinoTable; //do something here
        arduinoTable.loadDataWithBaseURL(null, example_table, "text/html", "utf-8", null);
        
        //Marker points to arduinotable
        marker.settag(arduinoTable);
        
        //Change marker color to green (successfully received data)

        //At this point the user has clicked on the marker, connected to bluetooth, downloaded file, the marker has changed color to green,
        //and the user sees a formatted table of the data they received. The user can choose to press
        //"send data" button or continue fetching data from other nodes.
        return false;
    }
        //Now write something that sends downloaded files.
    

