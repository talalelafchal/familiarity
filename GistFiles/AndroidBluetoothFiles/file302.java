package andrej.jelic.attend;

/**
 * Created by Korisnik on 13.6.2015..
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {

    public static final String PREFS_NAME = "PrefsFile";
    SharedPreferences prefs;

    // Debugging
    private static final String TAG = "DeviceListActivity";

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_UUID = "device_uuid";
    // Member fields
    private BluetoothAdapter mBtAdapter;

    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private Button scanButton;
    String noDevices;
    private String noPairDevices;
    private ArrayList<String> arrayList;
    private int devicesCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);
        // Initialize the button to perform device discovery
        noDevices = getResources().getText(R.string.none_found).toString();
        noPairDevices = getResources().getText(R.string.none_paired_found).toString();
        scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                arrayList = new ArrayList<String>();
                arrayList.clear();

                if (!mNewDevicesArrayAdapter.isEmpty()) {
                    mNewDevicesArrayAdapter.clear();
                    mNewDevicesArrayAdapter.remove(noDevices);
                }
                try {
                    mPairedDevicesArrayAdapter.remove(noPairDevices);
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.noString));
                }

                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices

        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name_new);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        this.registerReceiver(mReceiver, filter);


        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            //findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired_found).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.e(TAG, "doDiscovery()");
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();

            Log.e(TAG, "INFOOOOOOOOO " + info);
            //String addresscijela = info.substring(info.length() - 64);
            // String address = addresscijela.substring(0, 17);
            // String uuid = info.substring(info.length() - 36);
            String address = info.substring(info.length() - 17);
            Boolean firstCall;

            prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Adresa", address);
            // editor.putString("UUID", uuid);
            editor.putBoolean("firstCall", true);
            editor.apply();

            Log.e(TAG, "INFOOOOOOOOOO address " + address);
            //  Log.e(TAG, "INFOOOOOOOOOO uuid " + uuid);

            finish();
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // devicesCount++;
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //  boolean uuid = device.fetchUuidsWithSdp();

                // If it's already paired, skip it, because it's been listed already
                // When discovery is finished, change the Activity title
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                }/* else if (BluetoothDevice.ACTION_UUID.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);

                String deviceName = device.getName();
                Log.e(TAG, "Device name " + deviceName);

                boolean isNew = checkName(deviceName);
                Log.e(TAG, "Boolean isNew " + isNew);

                if (isNew) {
                    for (int i = 0; i < devicesCount; i++) {
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            mPairedDevicesArrayAdapter.add("\n  Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());

                        } else
                            mNewDevicesArrayAdapter.add("\n  Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());
                    }
                    devicesCount--;
                }*/


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))

            {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    mNewDevicesArrayAdapter.add(noDevices);
                }
                scanButton.setVisibility(View.VISIBLE);
            }
        }
    };

   /* private boolean checkName(String deviceName) {

        if (arrayList.contains(deviceName)) {
            return false;
        } else {
            arrayList.add(deviceName);
            return true;
        }
    }*/

}
