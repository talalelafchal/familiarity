private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi,byte[] scanRecord) {
         // デバイスが検出される度に呼び出されます。
    });
};