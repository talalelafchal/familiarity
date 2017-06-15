private BluetoothAdapter mBluetoothAdapter;
    BluetoothManager bluetoothManager;
    BluetoothDevice device;
    private boolean mScanning;
    private Handler mHandler;



 //get paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName().toString() + "\n" + device.getAddress().toString());
                Log.e("start here","start here and size  "+device.getName() );
                for (ParcelUuid idd:device.getUuids())
                {
                    //if (device.getName().contains("BLUENOVEN"))
                    //{
                        Log.e("deviceName gggg  ",device.getName() + "  uuid  "+ idd.getUuid().toString()  );
                       // this.device = device;
                   // }


                }

                //Log.e("Device detected",device.getName().toString() +"    UUID;;  ");

            }
           // mBluetoothAdapter.cancelDiscovery();

        }
