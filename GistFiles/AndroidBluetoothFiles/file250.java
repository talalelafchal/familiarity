package io.aceisnotmycard.redtooth.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by sergey on 18/12/15.
 */
public class BluetoothWrapper {

    private static final String LOG_TAG = BluetoothWrapper.class.getName();

    private static final String SERVICE_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    public static final int DATA_RECEIVED = 0;

    private static BluetoothWrapper wrapper;

    private BluetoothAdapter bluetoothAdapter;
    private Context ctx;

    /**
     * @param ctx – Application context
     * @return singleton
     */
    public static BluetoothWrapper get(Context ctx) {
        if (wrapper == null) {
            wrapper = new BluetoothWrapper(ctx);
        }
        return wrapper;
    }

    private BluetoothWrapper(Context ctx) {
        this.ctx = ctx;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * call startActivityForResult with this intent to enable BT
     * @return intent to enable BT
     */
    public Intent enable() {
        return new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    }

    /**
     * call startActivityForResult with this intent to make device discoverable
     * @return
     */
    public Intent makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        return discoverableIntent;
    }

    /**
     * @return is BT enabled?
     */
    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Start device discovery process
     */
    public void startDiscovery() {
        bluetoothAdapter.startDiscovery();
    }

    /**
     * Register broadcast receiver that will track device discovering process
     * Important! Shoud be registered before you call startDiscovery()
     * @param onDeviceDiscoveredCallback – called when single device discovered
     * @return
     */
    public Intent registerDiscoveredReceiver(final OnDeviceDiscoveredCallback onDeviceDiscoveredCallback) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    onDeviceDiscoveredCallback.onDeviceDiscovered(device);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        return ctx.registerReceiver(receiver, intentFilter);
    }

    /**
     * Starts a thread that is listening for incoming connection
     * @param name
     * @param callback called when somebody is connected
     */
    public void startServerThread(String name, OnDeviceConnectedCallback callback) {
         new ServerThread(name, UUID.fromString(SERVICE_UUID), callback).start();
    }

    /**
     * Starts a thread that is connecting to target device
     * @param device
     * @param callback called when connected
     */
    public void startClientThread(BluetoothDevice device, OnDeviceConnectedCallback callback) {
        new ClientThread(device, UUID.fromString(SERVICE_UUID), callback).start();
    }

    /**
     * Thread that will listen for incoming data from socket and process it with handler
     * @param socket
     * @param handler
     * @return
     */
    public ConnectionThread createConnectionThread(BluetoothSocket socket, Handler handler) {
        return new ConnectionThread(socket, handler);
    }

    public interface OnDeviceDiscoveredCallback {
        void onDeviceDiscovered(BluetoothDevice device);
    }

    public interface OnDeviceConnectedCallback {
        void onDeviceConnected(BluetoothSocket socket);
    }

    public class ClientThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private OnDeviceConnectedCallback callback;

        public ClientThread(BluetoothDevice device, UUID uuid, OnDeviceConnectedCallback callback) {
            this.callback = callback;
            BluetoothSocket tmp = null;
            this.device = device;

            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                d(e.getMessage());
            }
            socket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException connectException) {
                d(connectException.getMessage());
                try {
                    socket.close();
                } catch (IOException closeException) { }
                return;
            }
            callback.onDeviceConnected(socket);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }

    public class ServerThread extends Thread {
        private final BluetoothServerSocket serverSocket;
        private OnDeviceConnectedCallback callback;

        public ServerThread(String name, UUID uuid, OnDeviceConnectedCallback callback) {
            BluetoothServerSocket tmp = null;
            this.callback = callback;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
            } catch (IOException e) { }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = serverSocket.accept();
                    d("accepted connection");
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    callback.onDeviceConnected(socket);
                    d("Callback called");
                    cancel();
                    break;
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) { }
        }
    }

    public class ConnectionThread extends Thread {

        public static final int BUFFER_SIZE = 1024;

        private BluetoothSocket socket;
        private Handler handler;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ConnectionThread(BluetoothSocket socket, Handler handler) {
            this.socket = socket;
            this.handler = handler;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;
            try {
                tmpInputStream = socket.getInputStream();
                tmpOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                d(e.getMessage());
            }
            inputStream = tmpInputStream;
            outputStream  = tmpOutputStream;
            d("Created steams");
        }

        @Override
        public void run() {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    d("read from input");
                    handler.obtainMessage(DATA_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    d(e.getMessage());
                    break;
                }
            }
        }


        /**
         * Call from UI thread to write data to target device
         * @param bytes
         */
        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                d("Wrote to output");
            } catch (IOException e) {
                d(e.getMessage());
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }

    private void d(String msg) {
        Log.d(LOG_TAG, msg);
    }
}
