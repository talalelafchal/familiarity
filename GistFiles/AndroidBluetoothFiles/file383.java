package com.gorrotowi.btsendreceive;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    static Handler mHandler;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "No bluetooth aviable", Toast.LENGTH_SHORT).show();
        } else if (bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    bluetoothDevice = device;
                }
            }

            ConnectThread connectThread = new ConnectThread(bluetoothDevice);
            connectThread.run();

            mHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    byte[] writeBuf = (byte[]) msg.obj;
                    int begin = (int) msg.arg1;
                    int end = (int) msg.arg2;

                    switch (msg.what) {
                        case 1:
                            String writeMessage = new String(writeBuf);
                            writeMessage = writeMessage.substring(begin, end);
                            break;
                    }
                }
            };

        }
    }

    private class ConnectThreadReceiveAndSend extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectThreadReceiveAndSend(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = bluetoothSocket.getInputStream();
                tmpOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
        }

    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            this.mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmSocket = tmp;
        }

        @Override
        public void run() {
            super.run();
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                ConnectThreadReceiveAndSend connectThreadReceiveAndSend = new ConnectThreadReceiveAndSend(mmSocket);
                connectThreadReceiveAndSend.run();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        public void cance() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
