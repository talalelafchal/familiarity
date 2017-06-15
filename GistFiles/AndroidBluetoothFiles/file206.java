package YOURPACKAGE.gateopener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

/**
 * Created by trh on 12/1/14.
 */

public class Receiver extends BroadcastReceiver {

    public static String WIDGET_BUTTON = "YOURPACKAGE.gateopener.WIDGET_BUTTON";
    public BluetoothAdapter mBluetoothAdapter;
    private UUID gateUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String OPEN_GATE = "OPEN_GATE";
    private static String END_OF_COMMANDS = "\n\n";

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            // MY_UUID is the app's UUID string, also used by the server code
            try {
                Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmp = (BluetoothSocket) m.invoke(mmDevice, 10);

            } catch (NoSuchMethodException e) {
                Log.e("GateOpener", e.getMessage());
            } catch (InvocationTargetException e) {
                Log.e("GateOpener", e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e("GateOpener", e.getMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();

                OutputStream mmOut = mmSocket.getOutputStream();
                mmOut.write(OPEN_GATE.getBytes());

            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.e("GateOpener", "Exception in run:");
                Log.e("GateOpener", connectException.getMessage());

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("GateOpener", "Exception in connect thread run");

                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("GateOpener", "exception in connect thread cancel");

            }
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
            } catch (IOException e) {
                Log.e("GateOpener", "Exception in connected thread");

            }

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
                    Log.e("GateOpener", String.format("Response: %s", bytes));

                } catch (IOException e) {
                    Log.e("GateOpener", "Exception in connected thread run");
                    Log.e("GateOpener", e.getMessage());
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("GateOpener", "Exception in connected thread write");
                Log.e("GateOpener", e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("GateOpener", "exception in connected thread cancel");

            }
        }
    }


    public void onReceive(Context context, Intent intent) {

        if(WIDGET_BUTTON.equals(intent.getAction())) {
            Log.e("GateOpener", "Bluetooth open gate");
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // No BT support

            }

            BluetoothDevice gate = this.getDevice();
            Thread cThread = new ConnectThread(gate);
            cThread.start();

        }
    }

    public void manageSocket(BluetoothSocket mmSocket) {
        Thread connThread = new ConnectedThread(mmSocket);
        connThread.start();
    }

    private BluetoothDevice getDevice() {
        BluetoothDevice dev = null;
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Log.e("GateOpener", device.getName());
                Log.e("GateOpener", String.format("%s", device.getName().length()));
                if (device.getName().equalsIgnoreCase("NAME-OF-RPI-BLUETOOTH-DEVICE")) {
                    dev = device;
                }
            }
        }
        return dev;
    }
}


