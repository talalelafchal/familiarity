private class BluetoothReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Found a Bluetooth device
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            onDeviceFound(device); //Do something with it
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            // Device pairing/unpairing occurred
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
            switch (state) {
                case BluetoothDevice.BOND_BONDED:
                    // Device was paired
                    Log.i("BluetoothReceiver", "Paired with " + device.getName());
                    break;
                case BluetoothDevice.BOND_NONE:
                    // Device was unpaired
                    Log.i("BluetoothReceiver", "Unpaired with " + device.getName());
                    break;
                case BluetoothDevice.BOND_BONDING:
                    // Device is in the process of pairing
                    Log.i("BluetoothReceiver", "Pairing with " + device.getName());
                    break;
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            // Discovery has started, display progress spinner
            setProgressBarIndeterminateVisibility(true);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            // Discovery has ended, hide progress spinner
            setProgressBarIndeterminateVisibility(false);
        }
    }

}