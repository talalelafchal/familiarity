package com.example.jesse.gmaps.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.SystemClock;

import android.widget.Toast;
import android.Manifest;
import com.example.jesse.gmaps.R;
import com.example.jesse.gmaps.adapters.BtArrayAdaptor;
import com.example.jesse.gmaps.model.Comment;
import com.example.jesse.gmaps.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;

public class HubConnectActivity extends AppCompatActivity {
    private final static int REQUEST_COARSE_LOCATION = 1;

    private BtArrayAdaptor btArrayAdaptor1;
    private ArrayList<String> btArray1 = new ArrayList<String>();
    private BroadcastReceiver mReceiver;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> Discovereddevices = new ArrayList<BluetoothDevice>();
    private ArrayList<String> myDiscoveredDevicesStringArray = new ArrayList<String>();


    private BluetoothSocket mmSocket = null;
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;
    private boolean Connected = false;

    private AdapterView.OnItemClickListener btClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //TODO: 1. try to connect to BT hub 2. respond by changing colour of icon
            //do something in response to button
            String selectedBtName = (String) parent.getAdapter().getItem(position);

            //position = row number that user touched
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_connect);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_toolbar4);
        setSupportActionBar(myChildToolbar);
        //get corresponding action bar for this tool bar
        ActionBar ab = getSupportActionBar();
        //enable up button
        ab.setDisplayHomeAsUpEnabled(true);

        btArrayAdaptor1 = new BtArrayAdaptor(this, android.R.layout.simple_list_item_2, btArray1); // what is simple_list_item_1?

        // get handle to the list view in the Activity main layout
        ListView HubListView = (ListView) findViewById(R.id.listView1);

        // add action listener for when user click on row
        HubListView.setOnItemClickListener(btClickedHandler);

        //set the adaptor view for list view
        HubListView.setAdapter(btArrayAdaptor1);
        //See if you have devices that you have paired with
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                btArray1.add(deviceName);
                //notify the arrya adaptor that the array contents have changed (redraw)*********************88
                //btArrayAdaptor1.notifyDataSetChanged();
                Toast toast = Toast.makeText(getApplicationContext(),"auto connecting", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        //NEED ACCESS COARSE LOCATION FOR ACTION_FOUND TO WORK IDK
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION);


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        Discovereddevices.add(device);  //add to the list of discovered devices
                        //add new details to our btArray
                        btArray1.add(deviceName);
                        //NOTIFY the arrya adaptor that the array contents have changed (redraw)******************888
                       // btArrayAdaptor1.notifyDataSetChanged();
                    }
                }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedDiscovery(); // --->
                } else {
                    //TODO re-request
                    Toast toast = Toast.makeText(this,"coarse location is required to detect BT devices", LENGTH_LONG);
                    toast.show();
                }
                break;
            }
        }
    }
        protected void proceedDiscovery() {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            registerReceiver(mReceiver, filter);

            if(mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();
            //Look for new devices
            mBluetoothAdapter.startDiscovery();
            Log.v("hubconnectactivity", "in proceedDiscovery");
            Toast toast = Toast.makeText(this, "in proceedDiscovery", LENGTH_LONG);
            toast.show();

            //schtuff
            Set<BluetoothDevice> theThings = mBluetoothAdapter.getBondedDevices();
            if (theThings.size() > 0) {
                Iterator<BluetoothDevice> iter = theThings.iterator();
                Iterator<BluetoothDevice> iterDiscover = Discovereddevices.iterator();
                BluetoothDevice aNewdevice = iter.next();

                while (!aNewdevice.getName().equals("FriendHubID")) {
                    if(iter.hasNext() == true)
                        aNewdevice = iter.next();
                    else {
                        Log.v("ifloop", "FINISHED LOOKING IN PAIRED LIST");
                        break;
                    }
                }

                while(!aNewdevice.getName().equals("FriendHubID")){
                    if(iterDiscover.hasNext() == true)
                        aNewdevice = iterDiscover.next();
                    else {
                        Log.v("ifloop", "FINISHED LOOKING IN DISCOVER LIST");
                        Toast toast2 = Toast.makeText(this, "No hubs nearby", LENGTH_LONG);
                        toast2.show();
                        finish();
                        break;
                    }
                }
                if (aNewdevice.getName().equals("FriendHubID")) {
                    Log.v("ifloop", "when device is friendhubid");

                    if (Connected == true) {
                        closeConnection();
                    }
                    // get the selected bluetooth device based on list view position & connect // to it see pages 24 and 25
                    Log.v("ifloop", "before create serial BT device socket");
                    CreateSerialBluetoothDeviceSocket(aNewdevice);
                    Log.v("ifloop", "after create serial BT device socket");
                    ConnectToSerialBlueToothDevice();
                    Log.v("ifloop", "after connect to serial BT device socket");
                    Integer userId = User.user.getId();
                    SystemClock.sleep(10);
                    WriteToBTDevice("@@" + userId.toString());
                    //WriteToBTDevice("@@1");
                    Log.d("ifloop", "userIdInt: " + userId);
                    Log.v("ifloop", "userIdString: " +userId.toString());

                    String fpgaResponse = new String("");
                    do{
                        SystemClock.sleep(10);
                       WriteToBTDevice("@@" + userId.toString());
                        //WriteToBTDevice("@@1");
                        //SystemClock.sleep(10);
                        fpgaResponse = ReadFromBTDevice();
                        Log.v("dowhile", fpgaResponse);
                    }while(!fpgaResponse.equals( "%") );
                    Log.v("ifloop", "closing connection");
                    Toast toast3 = Toast.makeText(this, "Connection finished", LENGTH_LONG);
                    toast3.show();
                    closeConnection();
                    //TODO: go to the hub wall
                }
            }
        }
        // Add buttons from 'menu.appbar' to toolbar when the activity is created
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.appbar, menu);
            return true;
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }


    public void CreateSerialBluetoothDeviceSocket(BluetoothDevice device) {
        mmSocket = null;
        // universal UUID for a serial profile RFCOMM blue tooth device
        // this is just one of those “things” that you have to do and just works
        UUID MY_UUID = UUID.fromString ("00001101-0000-1000-8000-00805F9B34FB");
        // Get a Bluetooth Socket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mmSocket = device.createRfcommSocketToServiceRecord (MY_UUID);
        }
        catch (IOException e) {
            Toast toast =Toast.makeText(this, "Socket Creation Failed", LENGTH_LONG);
            toast.show();
        }
    }

    void closeConnection() {
        try {
            mmInStream.close();
            mmInStream = null;
        }
        catch (IOException e) {}
        try { mmOutStream.close();
            mmOutStream= null;
        } catch (IOException e) {}
        try { mmSocket.close();
            mmSocket = null;
        } catch (IOException e) {}
        Connected = false ;
    }

    public void GetInputOutputStreamsForSocket() { try {
        mmInStream = mmSocket.getInputStream();
        mmOutStream = mmSocket.getOutputStream(); } catch (IOException e) { }
    }

    public void ConnectToSerialBlueToothDevice() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
        Toast toast1 = Toast.makeText(this, "In connectToSerialBTDevice", LENGTH_LONG);
        toast1.show();
        try {
            // Attempt connection to the device through the socket.
            mmSocket.connect();
            Toast toast = Toast.makeText(this, "Connection Made", LENGTH_LONG);
            toast.show();
        }
        catch (IOException connectException) {
            Toast toast =  Toast.makeText(this, "Connection Failed", LENGTH_LONG);
            toast.show();
            return; }
        //create the input/output stream and record fact we have made a connection
        GetInputOutputStreamsForSocket(); // see page 26
        Connected = true ;
    }

    public void WriteToBTDevice (String message) {
        String s = new String("\r\n") ;
        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();
        try {
            mmOutStream.write(msgBuffer) ;
            mmOutStream.write(newline) ;
        } catch (IOException e) {
            Log.v("writeToBTDevice", "fail to write");
        }
    }
    public void WriteIntToBTDevice (Integer integer) {
        Log.v("WriteToBT","integer: " +integer.toString());
        byte[] integerBuffer = ByteBuffer.allocate(4).putInt(integer).array();
        try {
            mmOutStream.write(integerBuffer) ;
        } catch (IOException e) { }
    }

    public String ReadFromBTDevice(){
        byte c;
        String string = new String("");
        try {    //read from the input stream using polling and timeout
            for (int i = 0; i < 200; i++) { // try to read for 2 sec max
                SystemClock.sleep(10);
                if (mmInStream.available() > 0) {
                    if ((c = (byte) mmInStream.read()) != '\r') //'\r' terminator
                        string += (char) c;
                    else
                        return string;
                }
            }
        }catch (IOException e){
            return new String("-- No Response --");
        }
        return string; // should never reach this point
    }
    public static void onCommentResponse(List<Comment> clientComment){

        // do something

    }

}

