BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
if (mBluetoothAdapter == null) {
    // Device does not support Bluetooth
} else {
    if (!mBluetoothAdapter.isEnabled()) {
        // Bluetooth is not enable :)
    }
}