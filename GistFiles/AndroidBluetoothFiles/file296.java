Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
startActivityForResult(intent, REQUEST_DEVICE_PAIR);