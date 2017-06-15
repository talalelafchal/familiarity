// Create a BroadcastReceiver for ACTION_FOUND and ACTION_DISCOVERY_FINISHED
private final BroadcastReceiver mDeviceDiscoverReceiver = new BroadcastReceiver() {
    int cnt = 0;
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {

            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothClass clazz   = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);

            Log.v(TAG, "BluetoothDevice ... " + ++cnt);
            Log.v(TAG, "  Name            =" + device.getName());
            Log.v(TAG, "  Address         =" + device.getAddress());
            Log.v(TAG, "  MajorDeviceClass=" + clazz.getMajorDeviceClass());
            Log.v(TAG, "  DeviceClass     =" + clazz.getDeviceClass());

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.v(TAG, "BluetoothDevice ... Count:" + cnt);
        }
    }
};

mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
if (mBluetoothAdapter == null) {
    // Device does not support Bluetooth
    return;
}
if (!mBluetoothAdapter.isEnabled()) {
    // Bluetooth is not Enabled
    return;
}

// Register the BroadcastReceiver
IntentFilter filter = new IntentFilter();
filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
filter.addAction(BluetoothDevice.ACTION_FOUND);
registerReceiver(mDeviceDiscoverReceiver, filter); // Don't forget to unregister during onDestroy

if (mBluetoothAdapter.isDiscovering()) {
    mBluetoothAdapter.cancelDiscovery();
}
mBluetoothAdapter.startDiscovery();
