private BluetoothReceiver mBluetoothReceiver;
private BluetoothAdapter mBluetoothAdapter;

@Override
public void onCreate(Bundle savedInstanceState) {
    ...
    // Register a BroadcastReceiver for Bluetooth Intents
    mBluetoothReceiver = new BluetoothReceiver();
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    registerReceiver(mBluetoothReceiver, filter);

    // Start scanning for devices
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    mBluetoothAdapter.startDiscovery();
    ...
}

@Override
public void onDestroy() {
    super.onDestroy();
    mBluetoothAdapter.cancelDiscovery();
    unregisterReceiver(mBluetoothReceiver);
}