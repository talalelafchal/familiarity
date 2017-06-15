public class BTConnectionSPP {
    protected BluetoothAdapter BA;
    protected BluetoothSocket socket;
    protected OutputStream OS;
    protected String DeviceName;
    protected String ErrorMsg = "";

    public boolean connect(String DeviceName) {
        this.DeviceName = DeviceName;
        BA = BluetoothAdapter.getDefaultAdapter();
        if (BA == null) {
            this.ErrorMsg = "Did not find BT DefaultAdapter!";
            Log.w("BT", this.ErrorMsg);
            return false;
        } else {
            Log.d("BT", "Adapter " + BA.getName() + " found.");
            if (!BA.isEnabled()) {
                this.ErrorMsg = "Bluetooth Adapter is switched off!";
                Log.w("BT", this.ErrorMsg);
                return false;
            } else {
                BluetoothDevice BTPlayerDevice = null;
                if (BA.getBondedDevices() == null) {
                    this.ErrorMsg = "Bluetooth Adapter: no bonded devices found.";
                    Log.e("BT", this.ErrorMsg);
                    return false;
                } else {
                    for (BluetoothDevice bt : BA.getBondedDevices()) {
                        if (this.DeviceName.equals(bt.getName())) {
                            BTPlayerDevice = bt;
                            break;
                        }
                        Log.d("BT", "not the searched device: '" + bt.getName() + "'");
                    }
                }
                if (BTPlayerDevice == null) {
                    this.ErrorMsg = this.DeviceName + " not found.";
                    Log.d("BT", this.ErrorMsg);
                    return false;
                } else {
                    Log.d("BT", "Connecting … " + BTPlayerDevice.getAddress() + " / " + BTPlayerDevice.getName());
                    BluetoothSocket socket = null;
                    try {
                        // 00001101-0000-1000-8000-00805F9B34FB = Serial Port Profile
                        UUID SppUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        socket = BTPlayerDevice.createRfcommSocketToServiceRecord(SppUUID);
                    } catch (IOException e) {
                        this.ErrorMsg = "createRfcommSocketToServiceRecord failed";
                        Log.e("BT", this.ErrorMsg);
                        e.printStackTrace();
                        socket = null;
                        return false;
                    }
                    try {
                        socket.connect();
                        Log.d("BT", "socket.connect OK");
                    } catch (IOException e) {
                        this.ErrorMsg = "Synth-Application not found - started?";
                        Log.e("BT", this.ErrorMsg);
                        return false;
                    }
                    if (!socket.isConnected()) {
                        this.ErrorMsg = "BT socket is not Connected.\nSynth-Application started?";
                        Log.e("BT", this.ErrorMsg);
                        return false;
                    } else {
                        OutputStream OS = null;
                        try {
                            this.OS = socket.getOutputStream();
                            Log.d("BT", "socket.getOutputStream OK");
                        } catch (IOException e) {
                            Log.e("BT", "getOutputStream IOException:");
                            e.printStackTrace();
                            return false;
                        }
                        if (this.OS == null) {
                            this.ErrorMsg = "getOutputStream == null.";
                            Log.e("BT", this.ErrorMsg);
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
    }

    public boolean isConnected() {
        if (OS == null) {
            this.close();
            return false;
        } else {
            return true;
        }
    }

    public void close() {
        if (this.OS != null) {
            try {
                this.OS.close();
                Log.d("BT", "closed OS");
            } catch (IOException e) {
                Log.e("BT", "OS.close IOException!");
            }
        }
        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
                Log.d("BT", "closed socket");
            } catch (IOException e) {
                Log.e("BT", "socket.close IOException!");
            }
        }
    }

    public String ErrorMessage() {
        return this.ErrorMsg;
    }
}