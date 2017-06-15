package com.test.REDACTED.bluetoothtest;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.os.Handler;
import com.test.REDACTED.bluetoothtest.util.ListviewAdapter_btdevices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

import com.test.REDACTED.bluetoothtest.util.ListviewAdapter_messages;
import com.test.REDACTED.bluetoothtest.util.bt_message;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private static final int REQUEST_ENABLE_BT = 901;
    private BluetoothAdapter mBluetoothAdapter;

    private ListView lv_devices, lv_messages;
    private Button btn_connect, btn_acceptConnection;
    private TextView tv_status;

    private ArrayList<BluetoothDevice> devices;
    private ArrayList<bt_message> messages;
    private ListviewAdapter_btdevices lva_devices;
    private ListviewAdapter_messages lva_messages;

    private AcceptThread server;
    private final UUID uuid = UUID.fromString("73d3af9f-0fad-492d-913d-871140879ca9");

    private static final int RECEIVED_MESSAGE = 698;

    private Handler messageHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message inputMessage) {
            // Gets the image task from the incoming Message object.
            if (inputMessage.what == RECEIVED_MESSAGE){
                newMessage((bt_message)inputMessage.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Status", "Starting onCreate");
        Log.v("UUID", uuid.toString());
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        Log.v("Status", "Configuring LV");
        devices = new ArrayList<BluetoothDevice>();
        messages = new ArrayList<bt_message>();

        lva_devices = new ListviewAdapter_btdevices(context, devices);
        lv_devices = (ListView)findViewById(R.id.lv_devices);
        lv_devices.setAdapter(lva_devices);

        lva_messages = new ListviewAdapter_messages(context, messages);
        lv_messages = (ListView)findViewById(R.id.lv_messages);
        lv_messages.setAdapter(lva_messages);

        btn_acceptConnection = (Button)findViewById(R.id.btn_receive);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        tv_status = (TextView)findViewById(R.id.tv_status);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.v("Status", "Checking BT adapter");

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else {
                buildUI();
            }

        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Bluetooth unavailable")
                    .setMessage("You do not have bluetooth available on this device. This application will not function properly.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        Log.v("Status", "Done");
        server = new AcceptThread();

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText("Status: connecting to master...");
                createNetwork();
            }
        });

        btn_acceptConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!server.isAlive()){
                    tv_status.setText("Status: Listening for connections");
                    server.start();
                } else {
                    server.cancel();
                }
            }
        });

        newMessage("Me", "Hello world!");
    }

    private void newMessage(String sender, String message){

        messages.add(new bt_message(sender, message));

        lv_messages.setAdapter(new ListviewAdapter_messages(context, messages));
        lv_messages.postInvalidate();
    }

    private void newMessage(bt_message message){
        messages.add(message);

        lv_messages.setAdapter(new ListviewAdapter_messages(context, messages));
        lv_messages.postInvalidate();
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

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode < 0 && data != null) {
            if (requestCode == REQUEST_ENABLE_BT) {
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(context, "Bluetooth enabled!", Toast.LENGTH_SHORT).show();
                        buildUI();
                        break;
                    case RESULT_CANCELED:
                        new AlertDialog.Builder(context)
                                .setTitle("Bluetooth unavailable")
                                .setMessage("You do not have bluetooth available on this device. This application will not function properly.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        break;
                    default:
                        new AlertDialog.Builder(context)
                                .setTitle("Bluetooth unavailable")
                                .setMessage("You do not have bluetooth available on this device. This application will not function properly.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        // TODO solve bluetooth restart necessity
    }

    private void buildUI(){

        Log.v("Status", "Building UI");

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {

            Log.v("Status", "Found paired devices: " + pairedDevices.size());

            devices.clear();
            devices.addAll(pairedDevices);

            lv_devices.setAdapter(new ListviewAdapter_btdevices(context, devices));
            lv_devices.postInvalidate();
        }
    }

    private void createNetwork(){
        for (BluetoothDevice bt_device : devices){
            Log.d("Status", "Starting thread for bt device: " + bt_device.getName());
            new ConnectThread(bt_device).start();
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {

            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Bluetoothtest", uuid);
            } catch (IOException e) { e.printStackTrace(); }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d("Status", "Starting bluetooth listener");
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    Log.d("Status", "Trying to accept connection...");
                    socket = mmServerSocket.accept();
                    Log.d("Status", "Accept ended...");
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    // manageConnectedSocket(socket);
                    Log.d("Status", "New connection thread started");
                    new ConnectedThread(socket).start();

                    try {
                        mmServerSocket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                        break;
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { e.printStackTrace();}
            Log.d("Status", "Stopping bluetooth listener.");
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { e.printStackTrace();}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity

                    Log.d("BT_RECEIVE", "Message: " + new String(buffer, 0, bytes) + " - " + bytes);
                    messageHandler.obtainMessage(RECEIVED_MESSAGE, new bt_message(mmSocket.getRemoteDevice().getName(), new String(buffer, 0, bytes))).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }

            Log.d("Status", "receive loop ended");
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { e.printStackTrace();}
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            Log.d("Status", "Trying to connect to device: " + device.getName());
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {e.printStackTrace(); }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.d("Status", "Connection try failed: " + connectException.getMessage());
                connectException.printStackTrace();
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { closeException.printStackTrace();}
                return;
            }

            // Do work to manage the connection (in a separate thread)
            new ConnectedClientThread(mmSocket, mmDevice).start();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {e.printStackTrace(); }
        }
    }

    private class ConnectedClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothDevice mmDevice;

        public ConnectedClientThread(BluetoothSocket socket, BluetoothDevice device) {
            Log.d("Status", "Incoming connection: thread started");
            mmSocket = socket;
            mmDevice = device;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            try {
                mmOutStream.write("TEST!".getBytes());

                messageHandler.obtainMessage(RECEIVED_MESSAGE, new bt_message("Me", "TEST!")).sendToTarget();
            } catch (java.io.IOException e) {
                Log.d("Status", "Error while sending data");
                e.printStackTrace();
            }
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity

                    Log.d("BT_RECEIVE", "Message: " + new String(buffer, 0, bytes) + " - " + bytes);

                    messageHandler.obtainMessage(RECEIVED_MESSAGE, new bt_message(mmDevice.getName(), new String(buffer, 0, bytes))).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }

            Log.d("Status", "receive loop ended");
        }
    }

}
