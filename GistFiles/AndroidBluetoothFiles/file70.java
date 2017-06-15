public void connect(View v){ 
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice device = adapter.getRemoteDevice(ADDR);
    try {
        BluetoothSocket bs = device.createRfcommSocketToServiceRecord(_UUID);
        Field f = bs.getClass().getDeclaredField("mFdHandle");
        f.setAccessible(true);
        f.set(bs, 0x8000);
        bs.close();
        Thread.sleep(2000); // Just in case the socket was really connected 
    } catch (Exception e) { 
       Log.e(TAG, "Reset Failed", e); 
    } 
    try {
        this._socket = device.createRfcommSocketToServiceRecord(_UUID);
        this._socket.connect();
    } catch (IOException e) { 
        Log.e(TAG, "create() failed", e); 
    } 
}
