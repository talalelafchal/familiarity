package io.aceisnotmycard.redtooth;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.aceisnotmycard.redtooth.communication.BluetoothWrapper;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private final static int REQUEST_ENABLE_BT = 1;

    private ArrayAdapter<String> arrayAdapter;
    private ListView devicesListView;
    private Button makeDiscoverableButton;
    private Button sendDataButton;
    private Button scanButton;
    private TextView receivedDataTextView;

    private BluetoothWrapper bluetoothWrapper;

    private BluetoothWrapper.ConnectionThread connectionThread;
    private List<BluetoothDevice> devices;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesListView = (ListView) findViewById(R.id.devices_listview);
        makeDiscoverableButton = (Button) findViewById(R.id.make_discoverable_button);
        sendDataButton = (Button) findViewById(R.id.send_data_button);
        scanButton = (Button) findViewById(R.id.scanButton);
        receivedDataTextView = (TextView) findViewById(R.id.received_textview);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.devices_list_item);
        devicesListView.setAdapter(arrayAdapter);
        devices = new ArrayList<>();

        // handler that will handle incoming data
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == BluetoothWrapper.DATA_RECEIVED) {
                    String data = new String((byte[])msg.obj);
                    receivedDataTextView.setText(data);
                }
            }
        };

        bluetoothWrapper = BluetoothWrapper.get(getApplication());


        bluetoothWrapper.registerDiscoveredReceiver(device -> {
            devices.add(device);
            arrayAdapter.add(device.getName());
            arrayAdapter.notifyDataSetChanged();
        });

        if (!bluetoothWrapper.isEnabled()) {
            startActivityForResult(bluetoothWrapper.enable(), REQUEST_ENABLE_BT);
        }

        makeDiscoverableButton.setOnClickListener(v -> {
            startActivity(bluetoothWrapper.makeDiscoverable());
            bluetoothWrapper.startServerThread("Hello", socket -> {
                connectionThread = bluetoothWrapper.createConnectionThread(socket, handler);
                connectionThread.start();
            });
        });

        scanButton.setOnClickListener(v -> bluetoothWrapper.startDiscovery());
        devicesListView.setOnItemClickListener((parent, view, position, id) ->
                bluetoothWrapper.startClientThread(devices.get(position), socket -> {
            connectionThread = bluetoothWrapper.createConnectionThread(socket, handler);
            connectionThread.start();
        }));

        sendDataButton.setOnClickListener(v -> {
            if (connectionThread != null) {
                connectionThread.write("Hello".getBytes());
            }
        });
    }
}
