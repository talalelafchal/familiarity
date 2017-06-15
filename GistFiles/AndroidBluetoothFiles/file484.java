package com.zoucher.pxlrt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BluetoothService extends Service {
	
	// variables
    private static final String TAG = "Log in service";
    private final IBinder binder = (IBinder) new LocalBinder();

    private boolean isRunning  = false;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String EXTRA_ADDRESS = "device_address";
    public String strMessageToSend;

    @Override
    public void onCreate() {
        isRunning = true;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
    }

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

	// function to send a message to the connected device
    public void sendToMatrix(String error, String message) {
        if (btSocket != null) {
            try {                btSocket.getOutputStream().write(message.toString().getBytes());
            } catch (IOException e) {
                msg(error);
            }
        }
    }

    // function to send the pixels to the matrix
    public void sendPixelToMatrix(String strX, String strY, String strColor) {
        strMessageToSend = "%dp/" + strX + strY + strColor + "$";

        sendToMatrix("Error, failed to draw the pixel.", strMessageToSend);
    }


    public boolean ConnectSocket(String address) {
        boolean ConnectSuccess = true;
        try {
            if (btSocket == null || !isBtConnected) {
		    //get the mobile bluetooth device
                myBluetooth = BluetoothAdapter.getDefaultAdapter();
		    //connects to the device's address and checks if it's available
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
		    //create a RFCOMM (SPP) connection
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		    //start connection
                btSocket.connect();
            }
        } catch (IOException e) {
		//if the try failed
            ConnectSuccess = false;
        }


        return ConnectSuccess;
    }

    // function that disconnects from the bluetooth module
    public void DisconnectToMainActivity() {
	  //If the btSocket is busy or full
        if (btSocket != null)
        {
            try {
		    //close connection
                btSocket.close(); 
                stopService(new Intent(getApplicationContext(), BluetoothService.class));
            } catch (IOException e) {
                msg("Error, could not disconnect.");
            }
        }
    }

    public void Disconnect() {
    //If the btSocket is busy or full
        if (btSocket != null)
        {
            try {
		    //close connection
                btSocket.close(); 
                stopService(new Intent(getApplicationContext(), BluetoothService.class));
            } catch (IOException e) {
                msg("Error, could not disconnect.");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}
