package mangar.bluedruino;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

  Button btnLed1, btnLed2, btnLed3, btnLed4;
	TextView txtBtStatus, txtMessage;

	private static final String TAG = "bluetooth1";

	private BluetoothDevice btDevice = null;
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;

	// SPP UUID service
	private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// MAC-address of Bluetooth module (you must edit this line)
	private String btAddress = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnLed1 = (Button) findViewById(R.id.buttonLed1);
		btnLed2 = (Button) findViewById(R.id.buttonLed2);
		btnLed3 = (Button) findViewById(R.id.buttonLed3);
		btnLed4 = (Button) findViewById(R.id.buttonLed4);
		txtBtStatus = (TextView) findViewById(R.id.textView1);
		txtMessage = (TextView) findViewById(R.id.textView2);

		this.updateBluetoothDeviceAddress();

		checkBTState();

		btnLed1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendData("1");
				Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
			}
		});

		btnLed2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendData("0");
				Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
			}
		});

		btnLed3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendData("3");
				Toast.makeText(getBaseContext(), "Turn LED blinking", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

		this.updateBluetoothDeviceAddress();

		Log.d(TAG, "...onResume - try connect...");

		if (this.btDevice != null) {

			// Set up a pointer to the remote node using it's address.
			// BluetoothDevice device = btAdapter.getRemoteDevice(btAddress);
			this.btDevice = btAdapter.getRemoteDevice(btAddress);
			// BluetoothDevice device = btAdapter.getRemoteDevice(btAddress);

			// Two things are needed to make a connection:
			// A MAC address, which we got above.
			// A Service ID or UUID. In this case we are using the
			// UUID for SPP.

			try {
				btSocket = createBluetoothSocket(btDevice);
			} catch (IOException e1) {
				errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
			}

			// Discovery is resource intensive. Make sure it isn't going on
			// when you attempt to connect and pass your message.
			btAdapter.cancelDiscovery();

			// Establish the connection. This will block until it connects.
			Log.d(TAG, "...Connecting...");
			try {
				btSocket.connect();
				Log.d(TAG, "...Connection ok...");
			} catch (IOException e) {
				try {
					btSocket.close();
				} catch (IOException e2) {
					errorExit("Fatal Error",
							"In onResume() and unable to close socket during connection failure" + e2.getMessage()
									+ ".");
				}
			}

			// Create a data stream so we can talk to server.
			Log.d(TAG, "...Create Socket...");

			try {
				outStream = btSocket.getOutputStream();
			} catch (IOException e) {
				errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "...In onPause()...");

		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
			}
		}

		try {
			btSocket.close();
		} catch (IOException e2) {
			errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
		}
	}

	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
		if (Build.VERSION.SDK_INT >= 10) {
			try {
				final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord",
						new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Insecure RFComm Connection", e);
			}
		}
		return device.createRfcommSocketToServiceRecord(MY_UUID);
	}

	private void checkBTState() {
		// Check for Bluetooth support and then check to make sure it is turned on
		// Emulator doesn't support Bluetooth and will return null
		if (btAdapter == null) {
			errorExit("Fatal Error", "Bluetooth not support");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
			} else {
				// Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}

	private void errorExit(String title, String message) {
		Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
		finish();
	}

	private void sendData(String message) {
		byte[] msgBuffer = message.getBytes();
		Log.d(TAG, "...Send data: " + message + "...");

		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {
			String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
			if (btAddress.equals("00:00:00:00:00:00"))
				msg = msg
						+ ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
			msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

			errorExit("Fatal Error", msg);
		}
	}

	private void updateBluetoothDeviceAddress() {

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (pairedDevices == null || pairedDevices.size() == 0) {
			txtMessage.setText("No paired devices..");
		} else {

			for (BluetoothDevice bluetoothDevice : pairedDevices) {
				if (bluetoothDevice.getName().equalsIgnoreCase("linvor")) {
					this.btDevice = bluetoothDevice;

					txtMessage.setText("Paired with: " + this.btDevice.getName() + " (" + this.btDevice.getAddress()
							+ ")");
					this.btAddress = this.btDevice.getAddress();

					if (this.btDevice.getUuids() != null && this.btDevice.getUuids().length > 0) {
						this.MY_UUID = this.btDevice.getUuids()[0].getUuid();
					}

				}
			}

		}

	}

}
