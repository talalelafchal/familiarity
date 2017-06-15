// connect to a Bluetooth device from Android
Socket socket = null;
try {
    device.createRfcommSocketToServiceRecord(MY_UUID);
    Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
    socket = (BluetoothSocket) m.invoke(device, 1);
} catch (IOException e) {
    Log.e(TAG, "create() failed", e);
}