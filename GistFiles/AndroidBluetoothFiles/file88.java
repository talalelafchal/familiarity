BluetoothSocket btSocket = null;
try
{
    if (btSocket == null || !isBtConnected)
    {
     myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
     BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
     btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
     BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
     btSocket.connect();//start connection
    }
}
catch (IOException e)
{
    ConnectSuccess = false;//if the try failed, you can check the exception here
}

return null;
