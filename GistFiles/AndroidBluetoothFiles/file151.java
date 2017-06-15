package com.example.myfirstapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.util.Log;
import java.util.Set;


/**
 * This class may be added to your activity
 */
public class BluetoothArduino {

    //The public constructor
    public BluetoothArduino(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice bluetoothDevice = null;
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d("bluetooth" ,device.getName() + "\n" + device.getAddress());
            ///######################################
            // I only have connected by bluetooth the JY_MCU and the mobile 
            // if you have paired more than this two device you'll have to choose
            // the device, I don't need to choose the device because is the only one
            // paired.
            //#########################################
                bluetoothDevice = device;
            }
        }

        ConnectThread connectThread = new ConnectThread(bluetoothDevice,mBluetoothAdapter);
        connectThread.run();
    }
}