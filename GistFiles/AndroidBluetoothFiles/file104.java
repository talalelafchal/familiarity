/**
 * BLE Time Signal Service
 *
 * Notify central devices the local time in this peripheral device at a certain interval.
 * Here, the service itself is defined by BluetoothGattServer and BluetoothGattServerCallback,
 * and advertisement is performed by AdvertiseData.Builder, AdvertiseSettings.Builder and
 * BluetoothLeAdvertiser.
 */

package com.gclue.test.ble_peripheral;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.L)
public class BLEPeripheralActivity extends Activity {

    private static final String TAG = "TEST";

    // DEFINE YOUR SERVICE AND CHARACTERISTIC UUIDS (it doesn't have to be original, you can use
    // existing services and characteristics if interfaces of these services and characteristics
    // meet your needs).
    private static UUID SERVICE_UUID = new UUID(0x123456789ABCDEF0l, 0x123456789ABCDEF0l);
    private static UUID CHARACTERISTIC_UUID = new UUID(0x0123456789ABCDEFl, 0x0123456789ABCDEFl);

    private BluetoothGattServer mGattServer;
    private BluetoothGattCharacteristic mCharacteristic;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        mGattServer.close();

        super.onDestroy();
    }

    /**
     * check if BLE Supported device
     */
    public static boolean isBLESupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * get BluetoothManager
     */
    public static BluetoothManager getManager(Context context) {
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private void init() {
        // BLE check
        if (!isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // BT check
        BluetoothManager manager = getManager(this);
        mGattServer = manager.openGattServer(this, new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothDevice device, int status, int newState) {
                Log.d(TAG, "# onConnectionStateChange()");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d(TAG, "# onConnectionStateChange() connected");
                        mTimer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                String data = "android: " + new java.util.Date();
                                Boolean result = mCharacteristic.setValue(data);
                                if (!result) {
                                    Log.d(TAG, "characteristic setValue() failed.");
                                }
                                result = mGattServer.notifyCharacteristicChanged(device, mCharacteristic, false);
                                if (!result) {
                                    Log.d(TAG, "server notifyCharacteristicChanged() failed.");
                                }
                                Log.d(TAG, "# Timer sent: " + data);
                            }
                        };
                        mTimer.schedule(task, 0, 1000);
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d(TAG, "# onConnectionStateChange() disconnected");
                        mTimer.cancel();
                    }
                }
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "# onServiceAdded()");
                }
            }
        });

        mCharacteristic = new BluetoothGattCharacteristic(CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY, 0);
        BluetoothGattService bluetoothService = new BluetoothGattService(SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        bluetoothService.addCharacteristic(mCharacteristic);
        Boolean result = mGattServer.addService(bluetoothService);
        if (!result) {
            Log.d(TAG, "server addService() failed.");
        }

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(new ParcelUuid(SERVICE_UUID));
        dataBuilder.setIncludeTxPowerLevel(true);

        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        settingsBuilder.setConnectable(true);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);

        BluetoothAdapter adaptor = manager.getAdapter();
        if (!adaptor.isMultipleAdvertisementSupported()) {
            Log.d(TAG, "##### advertising is not supported #####");
            return;
        }
        BluetoothLeAdvertiser bleAdvertiser = adaptor.getBluetoothLeAdvertiser();
        bleAdvertiser.startAdvertising(settingsBuilder.build(), dataBuilder.build(), new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                Log.d(TAG, "advertising failed: " + errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "advertising succeeded.");
            }
        });
    }
}
