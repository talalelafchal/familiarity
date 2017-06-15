package com.sixpmplc.ble_demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@TargetApi(21)
public class MainActivity extends ActionBarActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 30000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BluetoothDevice mDevice;

    // setup UI handler
    private final static int UPDATE_DEVICE = 0;
    private final static int UPDATE_VALUE = 1;
    private final Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            final String value = (String) msg.obj;
            switch(what) {
                case UPDATE_DEVICE: updateDevice(value); break;
                case UPDATE_VALUE: updateValue(value); break;
            }
        }
    };

    private void updateDevice(String devName){
        TextView t=(TextView)findViewById(R.id.dev_type);
        t.setText(devName);
    }

    private void updateValue(String value){
        TextView t=(TextView)findViewById(R.id.value_read);
        t.setText(value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            scanLeDevice(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            String devicename = result.getDevice().getName();

            if (devicename != null){
                if (devicename.startsWith("TAIDOC")){
                    Log.i("result", "Device name: "+devicename);
                    Log.i("result", result.toString());
                    BluetoothDevice btDevice = result.getDevice();
                    connectToDevice(btDevice);
                }
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            Log.d("connectToDevice", "connecting to device: "+device.toString());
            this.mDevice = device;
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");

                    //update UI
                    Message msg = Message.obtain();

                    String deviceName = gatt.getDevice().getName();
                    switch (deviceName){
                        case "TAIDOC TD1261":
                            deviceName = "Thermo";
                            break;
                        case "TAIDOC TD8255":
                            deviceName = "SPO2";
                            break;
                        case "TAIDOC TD4279":
                            deviceName = "SPO2";
                            break;
                    }

                    msg.obj = deviceName;
                    msg.what = 0;
                    msg.setTarget(uiHandler);
                    msg.sendToTarget();

                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    Log.i("gattCallback", "reconnecting...");
                    BluetoothDevice mDevice = gatt.getDevice();
                    mGatt = null;
                    connectToDevice(mDevice);
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mGatt = gatt;
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue( BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                mGatt.writeDescriptor(descriptor);
            }

            //gatt.readCharacteristic(therm_char);
            gatt.setCharacteristicNotification(therm_char, true);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            byte[] value=characteristic.getValue();
            String v = new String(value);
            Log.i("onCharacteristicRead", "Value: " + v);
            //gatt.disconnect();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic) {
            float char_float_value = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT,1);
            Log.i("onCharacteristicChanged", Float.toString(char_float_value));


            String deviceName = gatt.getDevice().getName();
            String value = null;
            switch (deviceName){
                case "TAIDOC TD1261":
                    value = Float.toString(char_float_value);
                    break;
                case "TAIDOC TD8255":
                    value = Float.toString(char_float_value*10);
                    break;
            }

            //update UI
            Message msg = Message.obtain();
            msg.obj = value;
            msg.what = 1;
            msg.setTarget(uiHandler);
            msg.sendToTarget();



            //gatt.disconnect();
        }

    };
}