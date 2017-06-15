package ru.dzen.besraznitsy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * Created by azaz on 21/11/15.
 */
public class Client {
    public void startClient(Context mContext) {
        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);// Не забудьте снять регистрацию в onDestroy
    }

    private final BroadcastReceiver mReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action= intent.getAction();
             if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                 new ConnectThread(device).start();////<--- подключение к устройству
                 context.unregisterReceiver(mReceiver);
                //Добавляем имя и адрес в array adapter, чтобы показвать в ListView
                //mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());
            }
        }
    };

    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.MY_UUID));
            } catch (IOException e) {;}
            mmSocket = tmp;
        }

        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            // управлчем соединением (в отдельном потоке)
            try {
                manageConnectedSocket(mmSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * отмена ожидания сокета
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) throws IOException {
        PrintWriter out = new PrintWriter(mmSocket.getOutputStream());
        InputStreamReader is = new InputStreamReader(mmSocket.getInputStream());
        BufferedReader in = new BufferedReader(is);
        while (true){
            out.write(System.currentTimeMillis()+"");
            if(in.ready()){
                Log.d("Blue",in.readLine());
            }
            try {
                Thread.currentThread().sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
