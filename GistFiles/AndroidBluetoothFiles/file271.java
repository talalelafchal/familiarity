
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

   private Button bluetoothOn,bluetoothOff,bluetoothVisible,bluetoothlist,L000,L007,bluetoothConnect,bluetoothDisconnect,bluetoothDiscover;
   private BluetoothAdapter mBluetoothAdapter;
   private Set<BluetoothDevice>pairedDevices;
   private ListView listViewDiscovery;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      bluetoothOn = (Button)findViewById(R.id.buttonBluetooth_1);
      bluetoothOff = (Button)findViewById(R.id.buttonBluetooth_0);
      bluetoothDiscover = (Button)findViewById(R.id.buttonDiscover);
      bluetoothVisible = (Button)findViewById(R.id.buttonDiscoverable);
      bluetoothConnect = (Button)findViewById(R.id.buttonConnect);
      //bluetoothConnect = (Button)findViewById(R.id.buttonConnect);
      bluetoothDisconnect = (Button)findViewById(R.id.buttonDisconnect);
      L000 = (Button)findViewById(R.id.buttonL000);
      L007 = (Button)findViewById(R.id.buttonL007);
      listViewDiscovery = (ListView)findViewById(R.id.listViewDiscovery);
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
   }

   public void bluetoothOn(View view){
      if (!mBluetoothAdapter.isEnabled()) {
         Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(turnOn, 0);
         Toast.makeText(getApplicationContext(),"Turned on"
         ,Toast.LENGTH_LONG).show();
      }
      else{
         Toast.makeText(getApplicationContext(),"Already on",
         Toast.LENGTH_LONG).show();
         }
   }
   public void bluetoothDiscover(View view){
      pairedDevices = mBluetoothAdapter.getBondedDevices();

      ArrayList list = new ArrayList();
      for(BluetoothDevice device : pairedDevices)
         list.add(device.getName() + "\n" + device.getAddress());
      Toast.makeText(getApplicationContext(),"Showing Paired Devices",
      Toast.LENGTH_SHORT).show();
      final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
      listViewDiscovery.setAdapter(adapter);

   }
    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        Toast.makeText(getApplicationContext(),"Manage Connection",
        Toast.LENGTH_SHORT).show();
    }

    public void L007(View view){
        Toast.makeText(getApplicationContext(),"[L007]",
        Toast.LENGTH_SHORT).show();
    }

    public void L000(View view){
        Toast.makeText(getApplicationContext(),"[L000]",
        Toast.LENGTH_SHORT).show();
    }

    public void bluetoothConnection(View view){
        //private final BluetoothSocket mmSocket;
        //private final BluetoothDevice mmDevice;
        
        
        
        Toast.makeText(getApplicationContext(),"bluetoothConnection()",
        Toast.LENGTH_SHORT).show();
    }

    public void bluetoothDisonnection(View view){
        Toast.makeText(getApplicationContext(),"bluetoothDisconnection()",
        Toast.LENGTH_SHORT).show();
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                //ArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                //MainActivity.this.listViewDiscovery.getAdapter().add(device.getName() + "\n" + device.getAddress());
                ((ArrayAdapter) MainActivity.this.listViewDiscovery.getAdapter()).add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    //???
    // Register the BroadcastReceiver
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    //registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    ///

   public void bluetoothOff(View view){
      mBluetoothAdapter.disable();
      Toast.makeText(getApplicationContext(),"Turned off" ,
      Toast.LENGTH_LONG).show();
   }
   public void bluetoothDiscoverable(View view){
      Intent getVisible = new Intent(BluetoothAdapter.
      ACTION_REQUEST_DISCOVERABLE);
      startActivityForResult(getVisible, 0);

   }

    @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

    private UUID uuid = UUID.fromString("39f7b019-b0da-4a59-ad73-061d4603b514");

    private class BluetoothConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public BluetoothConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) { }
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
    }
}


