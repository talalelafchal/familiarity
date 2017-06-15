package net.matricom.oobe;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by bohregard on 6/28/16.
 */

public class Main extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothDevice btDevice;
    private BluetoothGatt bluetoothGatt;
    private final static String TAG = Main.class.getSimpleName();

    //This is the UUID for the HOGP remote
    private final static ParcelUuid UUID = new ParcelUuid(java.util.UUID.fromString("00001812-0000-1000-8000-00805f9b34fb"));
    private java.util.UUID UUID1 = java.util.UUID.fromString("00001812-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(bluetoothReceiver, intentFilter);

        /**
         * Turn Bluetooth On. Since this is a blocking call, we await the STATE_ON action from
         * the broadcast receiver before we attempt to scan anything.
         */
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            Log.d(TAG, "Starting Scan");
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            startScan();
        } else {
            bluetoothAdapter.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothReceiver);
    }

    /**
     * This scan will run until we tell it to stop
     */
    private void startScan() {
        Log.i(TAG, "Starting Scan");
        //Scan for devices advertising the thermometer service
        ScanFilter beaconFilter = new ScanFilter.Builder()
                .setDeviceName("Matrimote")
                .setServiceUuid(UUID)
                .build();
        ArrayList<ScanFilter> filters = new ArrayList<>();
        filters.add(beaconFilter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        bluetoothLeScanner.startScan(filters, settings, scanCallback);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            bluetoothLeScanner.stopScan(this);
            Log.d(TAG, result.toString());
            if(result.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
                result.getDevice().createBond();
            }

            if(result.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG, "We have a bond but no connection?");
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                Log.d(TAG, result.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "Error: " + errorCode);
        }
    };

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device;
            Log.d(TAG, "Intent: " + intent);
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (state) {
                        case BluetoothDevice.BOND_BONDING:
                            // Bonding...
                            Log.d(TAG, "Bonding");
                            Log.d(TAG, "Device: " + device.getName());
                            break;

                        case BluetoothDevice.BOND_BONDED:
                            // Bonded...
                            Log.d(TAG, "Bonded");
                            Log.d(TAG, "Device: " + device.getName());
                            //bluetoothGatt = device.connectGatt(context, true, bluetoothGattCallback);
                            break;

                        case BluetoothDevice.BOND_NONE:
                            // Not bonded...
                            Log.d(TAG, "No Bond");
                            Log.d(TAG, "Device: " + device.getName());
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d(TAG, "Device: " + intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.toString());
                    Log.d(TAG, "Bond State: " + bluetoothDevice.getBondState());
                    break;
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    final int scanState = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                    Log.d(TAG, "Scan Mode: " + scanState);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:

                    /**
                     * Setup the Bluetooth stuff
                     * Scan for BLE devices once our adapter is on
                     */
                    if (bluetoothAdapter == null) {
                        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                        bluetoothAdapter = manager.getAdapter();
                    }
                    bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

                    final int adapterState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    Log.d(TAG, "State Changed: " + adapterState);
                    switch (adapterState) {
                        case BluetoothAdapter.STATE_ON:
                            startScan();
                    }
                    break;
            }
        }
    };

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange");
            Log.d(TAG, "State: " + newState);
            if(BluetoothProfile.STATE_CONNECTED == newState && bluetoothGatt != null){
                bluetoothGatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered");
            if(bluetoothGatt != null){
                List<BluetoothGattService> gattServices = bluetoothGatt.getServices();
                for (BluetoothGattService gattService : gattServices) {
                    Log.d(TAG, gattService.getUuid().toString());

                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                    // Loops through available Characteristics.
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        Log.d(TAG, gattCharacteristic.getUuid().toString());
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "OnCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "onCharacteristicChanged");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d(TAG, "onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d(TAG, "onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.d(TAG, "onMtuChanged");
        }
    };
}
