package com.roycwc.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic characteristic;
    private Button testButton;
    private boolean got = false;
    private boolean written = false;
    private byte[] responseValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ini();
    }

    private boolean write(byte[] bytes){
        characteristic.setValue(bytes);
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private byte[] testRequest(int i){
        written = false;
        byte[] bytes = new byte[20];
        for(int j=0;j<20;j++){
            bytes[j] = (byte)i;
        }
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        characteristic.setValue(bytes);
        write(bytes);
        while(!written){}
        return responseValue;
    }

    private void ini() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                if (bluetoothDevice.getName().contains("HitecCharger")){
                    if (got) return;
                    got= true;
                    Log.wtf("WTF", "Scan: "+bluetoothDevice.getName());
                    mBluetoothAdapter.stopLeScan(null);
                    mBluetoothGatt = bluetoothDevice.connectGatt(MainActivity.this, false, new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                Log.wtf("WTF", "Connected");
                                mBluetoothGatt.discoverServices();
                            }
                            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                Log.wtf("WTF", "Disconnected");
                            }
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            super.onServicesDiscovered(gatt, status);
                            BluetoothGattService service = gatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                            characteristic = service.getCharacteristics().get(0);
                            boolean setNotifyStatus = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                            Log.wtf("WTF", "writable now "+setNotifyStatus);
                        }

                        @Override
                        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                            super.onCharacteristicChanged(gatt, characteristic);
//                            mBluetoothGatt.readCharacteristic(characteristic);
//                            responseValue = characteristic.getValue();
                        }

                        @Override
                        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                            super.onCharacteristicWrite(gatt, characteristic, status);
//                            try {
//                                Thread.sleep(60);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            responseValue = null;
                            written = true;
                        }

                        @Override
                        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                            super.onCharacteristicRead(gatt, characteristic, status);
                            written = true;
                        }
                    });
                }
            }
        });
        testButton = (Button)findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (characteristic == null) return;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<5000;i++){
                            testRequest(i);
                        }
                    }
                }).start();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothGatt != null){
            mBluetoothGatt.disconnect();
        }
    }
}
