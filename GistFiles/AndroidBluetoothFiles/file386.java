package com.example.nicnowak.bluetoothmagic2;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Toast;

public abstract class MainActivity extends Activity implements OnClickListener {
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private static String address = "XX:XX:XX:XX:XX:XX";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream inStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
    }

    //Check if bluetooth is enabled.
    private void CheckBt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Bluetooth Disabled !", Toast.LENGTH_SHORT).show();
        }

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth null !", Toast.LENGTH_SHORT).show();
        }
    }
    
     //Connect to device.
    public void Connect() {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
        }

        catch (IOException e) {
            try {btSocket.close();}
            catch (IOException e2) {}
        }

        readTextFileFromArduino();
    }

    public void readTextFileFromArduino() {
        try {inStream = btSocket.getInputStream();}
        catch (IOException e) {}

        //Read textfile from btsocket here
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {btSocket.close();} catch (IOException e) {}
    }
}