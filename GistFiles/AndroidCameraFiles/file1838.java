package com.example.bluemouth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.view.View.OnClickListener;


public class MainActivity extends Activity {

    int REQUEST_ENABLE_BT = 0;
    Button buttonFD; //кнопка поиска устройств
    ListView PairedDevices, FoundedDevices;
    ArrayList<String> pdArray, fdArray;
    ArrayAdapter pAdap,fAdap;
    BluetoothAdapter blueAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PairedDevices = (ListView) findViewById(R.id.PairedDevices);
        FoundedDevices = (ListView) findViewById(R.id.FoundedDevices);
        buttonFD = (Button) findViewById(R.id.bFindD);
        pdArray = new ArrayList<String>();
        fdArray = new ArrayList<String>();
        pAdap = new ArrayAdapter(this,  android.R.layout.simple_list_item_1, pdArray);
        fAdap = new ArrayAdapter(this,  android.R.layout.simple_list_item_1, fdArray);
        PairedDevices.setAdapter(pAdap);
        FoundedDevices.setAdapter(fAdap);
        //подключаем блютуз адаптер
        blueAd = BluetoothAdapter.getDefaultAdapter();
        //проверяем, включен ли блютуз
        if(!blueAd.isEnabled()){
            //если не, то включаем
            Intent enBlueIn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enBlueIn, REQUEST_ENABLE_BT);
        }


        OnClickListener oclButtonFD = new OnClickListener() {
            @Override
            public void onClick(View v) {
                pdArray.clear();
                Set<BluetoothDevice> pairedD = blueAd.getBondedDevices();
                Log.i("Count 1: ", Integer.toString(pairedD.size()));
                if(pairedD.size() > 0) {
                    for (BluetoothDevice device : pairedD) {
                        pdArray.add(device.getName() + "\n" + device.getAddress());
                    }
                }

                if(blueAd.startDiscovery()){
                    // Create a BroadcastReceiver for ACTION_FOUND
                    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            // When discovery finds a device
                            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                // Get the BluetoothDevice object from the Intent
                                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                // Add the name and address to an array adapter to show in a ListView
                                fdArray.add(device.getName() + "\n" + device.getAddress());
                            }
                        }
                    };

                    // Register the BroadcastReceiver
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
                }
                pAdap.notifyDataSetChanged();
                fAdap.notifyDataSetChanged();

            }
        };
        buttonFD.setOnClickListener(oclButtonFD);

        PairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

            }
        });
    }



    private class ConnectThread extends Thread {
        private
            BluetoothSocket mmSocket;
            BluetoothDevice mmDevice;
            InputStream mmInStream;
            OutputStream mmOutStream;
            final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            blueAd.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

        public void manageConnectedSocket(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

    }

    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
