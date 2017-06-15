package com.example.nicho.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import java.util.UUID;

import android.view.View;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Set;
import android.content.Intent;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import android.widget.Button;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

public class MainActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream inStream = null;
    private OutputStream outStream = null;
    BluetoothDevice iterator;
    Marker.OnMarkerClickListener l;
    private boolean success = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(12);

        GeoPoint startPoint = new GeoPoint(46.573353, -87.41443);
        GeoPoint loc_TC02 = new GeoPoint(46.57355, -87.41553);

        mapController.setCenter(startPoint);

        // build a new marker pin
        Marker TC01 = new Marker(map);
        TC01.setPosition(startPoint);
        TC01.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        TC01.setTitle("TC01");

        Marker TC02 = new Marker(map);
        TC02.setPosition(loc_TC02);
        TC01.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        TC02.setTitle("TC02");


        map.getOverlays().add(TC01);
        map.getOverlays().add(TC02);

        Button btbutton = (Button) findViewById(R.id.bluetooth);

        btbutton.setOnClickListener(new View.OnClickListener() {
            //Run bluetooth code on button click.
            public void onClick(View v) {
                BlueTooth();
            }
        });

        map.invalidate();

    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.a
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void BlueTooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
        }
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth null !", Toast.LENGTH_SHORT).show();
        }
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {

            Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

            if (bondedDevices.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
            } else {
                for (BluetoothDevice iterator : bondedDevices) {
                    if (iterator.getName().equals("HC-05")) {
                        BluetoothDevice device = iterator;
                        try {
                            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                            btSocket.connect();
                            Toast.makeText(getApplicationContext(), "Connected to HC05", Toast.LENGTH_SHORT).show();
                        } catch (IOException ex) {}
                        if (btSocket.isConnected()) {
                            Toast.makeText(getApplicationContext(), "Connected to HC05", Toast.LENGTH_SHORT).show();

                            //Write 'r' to arduino to tell it to send data.
                            try {
                                outStream = btSocket.getOutputStream();
                                outStream.write('r');
                                Toast.makeText(getApplicationContext(), "Supposedly wrote a letter to arduino.", Toast.LENGTH_SHORT).show();
                                success = true;
                            } catch (IOException ex) {
                                Toast.makeText(getApplicationContext(), "Error writing to Arduino.", Toast.LENGTH_SHORT).show();
                            }

                            //Read data from Arduino
                            if (success == true) {
                                    try {
                                        Toast.makeText(getApplicationContext(), "About to get inputstream.", Toast.LENGTH_SHORT).show();
                                        inStream = btSocket.getInputStream();
                                        Toast.makeText(getApplicationContext(), "Got input stream.", Toast.LENGTH_SHORT).show();
                                        byte[] rawData = new byte[1024];

                                        try {
                                            inStream.read(rawData, 0, 12);
                                            Toast.makeText(getApplicationContext(), "Now reading from Arduino.", Toast.LENGTH_SHORT).show();
                                            String data = new String(rawData, 0, 12);
                                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                                        }

                                        catch (IOException ex) {
                                            Toast.makeText(getApplicationContext(), "Error reading from Arduino.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    catch (IOException ex) {
                                        Toast.makeText(getApplicationContext(), "Error reading from Arduino.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
}



