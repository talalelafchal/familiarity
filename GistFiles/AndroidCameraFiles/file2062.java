package com.example.delle4310.wsepinm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ActivityThree extends AppCompatActivity implements OnMapReadyCallback{
    public static int ID = 3;

    static final LatLng WSEPiNM = new LatLng(50.8669817,20.6021006);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        XmlPullParserFactory xmlFactoryObject = null;
        XmlPullParser myParser = null;
        FileInputStream input = null;
        //It is the content useful
        String content = null;

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();
            File file = new File(getExternalFilesDir("dir_for_me"), "document.xml");
            if(!file.exists()) {
                Toast.makeText(this, "The content is not available", Toast.LENGTH_LONG).show();
            } else {
                input = new FileInputStream(file);
                myParser.setInput(input, null);
                int event = myParser.getEventType();
                boolean findValue = true;
                //Scan the XML document
                String nameFind = "activity";
                String nameFound = "";
                String attributeFind = "3";
                String attributeFound = "";
                int countdown = 4;
                TextView tv = null;
                boolean now = false;
                boolean nextStep = false;
                while (event != XmlPullParser.END_DOCUMENT && findValue) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            nameFound = myParser.getName();
                            attributeFound = myParser.getAttributeValue(null, "number");
                            if(attributeFound == null) attributeFound="";
                            if(!now && !nextStep && nameFound.equals(nameFind) && attributeFound.equals(attributeFind)){ nameFind = "item"; nextStep = true; break;}
                            if(!now && nextStep && nameFound.equals(nameFind)){ nameFind = "content"; now = true; break;}
                            break;
                        case XmlPullParser.TEXT:
                            if(now) {
                                if(nameFound.equals("phone")) { nameFound=""; tv = (TextView)findViewById(R.id.phone); tv.setText(myParser.getText()); countdown--;}
                                if(nameFound.equals("site")) { nameFound=""; tv = (TextView)findViewById(R.id.site); tv.setText(myParser.getText()); countdown--;}
                                if(nameFound.equals("email")) { nameFound=""; tv = (TextView)findViewById(R.id.email); tv.setText(myParser.getText()); countdown--;}
                                if(nameFound.equals("address")) { nameFound=""; tv = (TextView)findViewById(R.id.address); tv.setText(myParser.getText()); countdown--;}
                            }
                            if(countdown == 0) findValue = false;
                            break;
                    }
                    event = myParser.next();
                }
                input.close();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //DO WHATEVER YOU WANT WITH GOOGLEMAP
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.setMyLocationEnabled(true);

        //Focus map
        map.addMarker(new MarkerOptions().position(WSEPiNM)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.logo))
                .anchor(0.5f, 0.5f) // Anchors the marker on the bottom left
                .visible(true));
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(WSEPiNM, 17));
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Returning the view containing InfoWindow contents
                return v;
            }
        });
    }
}
