package gardenator.sapher.com.gardenator.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;

import gardenator.sapher.com.gardenator.R;

@EActivity(R.layout.activity_device_wizard)
public class MyActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    BluetoothAdapter bluetoothAdapter;

    @AfterViews
    public void init() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) {
            Log.d(TAG, "init: this device does not support bluetooth");
        }
        else {

            // Android 6.0
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Log.d(TAG, "init: version lollipop");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION" }, 0);

                    Log.d(TAG, "init: request permission");

                    bluetoothAdapter.startDiscovery();
                }
            }
        }
    }

    @Receiver(actions = BluetoothDevice.ACTION_FOUND)
    protected void onDeviceFound(Intent intent) {

        Log.d(TAG, "onDeviceFound: triggered");

        if(intent == null) return;

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        Log.d(TAG, "onDeciveFound: " + device.getAddress());
    }
}
