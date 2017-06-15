final String command = "HELO\n";
final UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

BluetoothAdapter.getDefaultAdapter();
if (btAdapter == null) {
	sendLogMessage("Bluetooth adapter is not available.");
	return;
}
sendLogMessage("Bluetooth adapter is found.");

if (!btAdapter.isEnabled()) {
    sendLogMessage("Bluetooth is disabled. Check configuration.");
    return;
}
sendLogMessage("Bluetooth is enabled.");

BluetoothDevice btDevice = null;
Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
for (BluetoothDevice dev : bondedDevices) {
    sendLogMessage("Paired device: " + dev.getName() + " (" + dev.getAddress() + ")");

    if (dev.getName().equals("MyDeviceName")) {
        btDevice = dev;
    }
}

if (btDevice == null) {
    sendLogMessage("Target Bluetooth device is not found.");
    return;
}
sendLogMessage("Target Bluetooth device is found.");

BluetoothSocket btSocket;
try {
    btSocket = btDevice.createRfcommSocketToServiceRecord(sppUuid);
} catch (IOException ex) {
    sendLogMessage("Failed to create RfComm socket: " + ex.toString());
    return;
}
sendLogMessage("Created a bluetooth socket.");

for (int i = 0; ; i++) {
    try {
        btSocket.connect();
    } catch (IOException ex) {
        if (i < 5) {
            sendLogMessage("Failed to connect. Retrying: " + ex.toString());
            continue;
        }

        sendLogMessage("Failed to connect: " + ex.toString());
        return;
    }
    break;
}

sendLogMessage("Connected to the bluetooth socket.");
try {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(btSocket.getOutputStream(), "ASCII"));
    writer.write(command);
    writer.flush();
} catch (IOException ex) {
    sendLogMessage("Failed to write a command: " + ex.toString());
    return;
}
sendLogMessage("Command is sent: " + command);

String output;
try {
    BufferedReader reader = new BufferedReader(new InputStreamReader(btSocket.getInputStream(), "ASCII"));
    output = reader.readLine();
} catch (IOException ex) {
    sendLogMessage("Failed to write a command: " + ex.toString());
    return;
}
sendLogMessage("Result: " + output);

try {
    btSocket.close();
} catch (IOException ex) {
    sendLogMessage("Failed to close the bluetooth socket.");
    return;
}
sendLogMessage("Closed the bluetooth socket.");