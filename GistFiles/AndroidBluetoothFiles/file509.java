public boolean pairDevice(BluetoothDevice device) {
    try {
        Method createBond = BluetoothDevice.class.getMethod("createBond");
        return (boolean) createBond.invoke(device);
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean unpairDevice(BluetoothDevice device) {
    try {
        Method removeBond = BluetoothDevice.class.getMethod("removeBond");
        return (boolean) removeBond.invoke(device);
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}