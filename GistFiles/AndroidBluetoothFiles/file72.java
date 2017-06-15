import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnection {
    private static final String DEVICE_NAME = "NOME";
    OutputStream mmOutputStream;
    InputStream mmInStream;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    Activity activity;

    public BluetoothConnection(Context context) {
        activity = (Activity) context;
    }

    public void connect() {
        new Runnable() {
            public void run() {
                try {
                    find();
                    open();
                } catch (Exception e) {
                    Log.e("bt", e.toString());
                }
            }
        };
    }

    private void find() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            // error, device can't use bluetooth
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(DEVICE_NAME)) {
                    mmDevice = device;
                    break;
                }
            }
        }
    }

    private void open() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInStream = mmSocket.getInputStream();
    }

    public int getInt() {
        String s = "";
        try {
            int b;
            while ((b = mmInStream.read()) != 10 && b != -1) {// non a-capo && non fine stream
                s += b - '0';
            }
        } catch (Exception e) {
            return -1;
        }
        return s == "" ? -1 : Integer.parseInt(s);
    }

    public void clearInput() {
        try {
            mmInStream.skip(mmInStream.available());
        } catch (Exception e) {}
    }

    public boolean sendString(String s) {
        byte[] buffer = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            buffer[i] = (byte)s.indexOf(i);
        }
        try {
            mmOutputStream.write(buffer);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public void close() throws IOException {
        mmOutputStream.close();
        mmInStream.close();
        mmSocket.close();
    }
}
