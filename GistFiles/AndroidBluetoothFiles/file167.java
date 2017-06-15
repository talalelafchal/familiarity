private class BluetoothReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Found a Bluetooth device
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            onDeviceFound(device); // Do something with it
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            // Discovery has started, display progress spinner
            setProgressBarIndeterminateVisibility(true);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            // Discovery has ended, hide progress spinner
            setProgressBarIndeterminateVisibility(false);
        }
    }

}