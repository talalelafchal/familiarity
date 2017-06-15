package com.example.myfirstapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by user on 10/11/14.
 */
public class ConnectThread extends Thread {
    private  BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothServerSocket serverSocket;
    private ConnectedThread connectedThread;
    private BluetoothAdapter mBluetoothAdapter;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
    this.mBluetoothAdapter = mBluetoothAdapter;
        BluetoothSocket tmp = null;
        mDevice = device;
        ParcelUuid[] uuids = mDevice.getUuids();
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord( uuids[0].getUuid());;
        } catch (IOException e) { }
        mSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
        manageConnectedSocket(mSocket);
    
    }


    private void manageConnectedSocket(BluetoothSocket socket){
        ConnectedThread connectedThread= new ConnectedThread(socket,mBluetoothAdapter);
        connectedThread.run();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) { }
    }
}