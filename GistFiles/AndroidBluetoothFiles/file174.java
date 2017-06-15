//     tmp = device.createRfcommSocketToServiceRecord(uuid) is not work ;
// you should use the code below instead

Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
tmp = (BluetoothSocket) m.invoke(device, 1);