package gboys.bboxscan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements BluetoothAdapter.LeScanCallback {

    private List<String> bluetoothDevicesAddresses = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> bluetoothDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView bluetoothDevicesListView = new ListView(this);
        bluetoothDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bluetoothDevicesAddresses);
        bluetoothDevicesListView.setAdapter(bluetoothDevicesArrayAdapter);

        setContentView(bluetoothDevicesListView);
    }

    @SuppressLint("NewApi")
    public void onStart() {
        super.onStart();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startLeScan(this);
        Log.d("LeScan", "Start scanning");
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(this);
            Log.d("LeScan", "Stopped scanning");
        }
        super.onPause();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!bluetoothDevicesAddresses.contains(device.getAddress())) {
            Log.d("LeScan", "new device : " + device.getAddress());
            bluetoothDevicesAddresses.add(device.getAddress());
            bluetoothDevicesArrayAdapter.notifyDataSetChanged();
        }
    }
}
