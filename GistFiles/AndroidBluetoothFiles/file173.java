package io.macu.sppreader;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getCanonicalName();

	// Must use 00001101-0000-1000-8000-00805F9B34FB when connecting to Socket CHS 7Ci
	// TODO: Try UUID candidates http://stackoverflow.com/a/25846024/1597274
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private ListView lvBonded;
	private TextView tvCurrentConnection, tvReceivedData;
	private Button bnRefreshDevices, bnCloseConnection, bnClear;

	private ProgressDialog prog;

	private BluetoothDevice currentDevice;
	private BluetoothSocket currentSocket;
	private InputStream currentInputStream;
	private ByteBuffer inputBuffer;

	/* ================================================== */
	// Activity lifecycle and view setup

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();

		lvBonded = (ListView) findViewById(R.id.listView);
		bnRefreshDevices = (Button) findViewById(R.id.button_refreshDevices);
		tvCurrentConnection = (TextView) findViewById(R.id.textView_currentConnection);
		bnCloseConnection = (Button) findViewById(R.id.button_closeConnection);
		tvReceivedData = (TextView) findViewById(R.id.textView_receivedData);
		bnClear = (Button) findViewById(R.id.button_clear);

		bnRefreshDevices.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshBondedDevicesList();
			}
		});

		refreshBondedDevicesList();

		bnCloseConnection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnectScanner();
			}
		});

		bnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvReceivedData.setText("");
			}
		});
	}

	private void refreshBondedDevicesList() {
		List<String> names = new ArrayList<>();
		final List<BluetoothDevice> devices = new ArrayList<>();
		for(BluetoothDevice d : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
			names.add(d.getName());
			devices.add(d);
		}

		lvBonded.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
		lvBonded.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				connectScanner(devices.get(position));
			}
		});
	}

	/* ================================================== */
	// Connection management

	private void connectScanner(final BluetoothDevice device) {
		if (currentDevice != null) {
			toast("Close the current connection first");
			return;
		}

		(new Thread(new Runnable() {
			@Override
			public void run() {
				prog("Connecting...", "Starting...");
				currentDevice = device;

				try {
					// Try standard method.
					prog("Connecting...", "Creating socket...");
					// Let SDP decide what radio channel to use.
					// http://stackoverflow.com/a/18582044/1597274
					currentSocket = device.createRfcommSocketToServiceRecord(uuid);
				} catch (IOException ex) {
					try {
						// Try fallback method.
						// TODO Consider going straight to this method for speedup.
						prog("Connecting...", "Creating socket using fallback method...");
						// Use unpublished method. Doesn't check for listener at endpoint. Ignores some error cases.
						// http://stackoverflow.com/a/18582044/1597274
						currentSocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", int.class).invoke(device, 1);
					} catch (Exception ex2) {
						progDismiss();
						toast("Cannot create socket", ex2);
						disconnectScanner();
						return;
					}
				}

				try {
					prog("Connecting...", "Connecting socket...");
					currentSocket.connect();
				} catch (IOException ex) {
					progDismiss();
					toast("Cannot connect socket", ex);
					disconnectScanner();
					return;
				}

				try {
					prog("Connecting...", "Opening input stream...");
					currentInputStream = currentSocket.getInputStream();
				} catch (IOException ex) {
					progDismiss();
					toast("Cannot get in/out streams", ex);
					disconnectScanner();
					return;
				}

				progDismiss();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tvCurrentConnection.setText(device.getName() + " (" + device.getAddress() + ")");
					}
				});
				runReadLoop();
			}
		})).start();
	}

	private void runReadLoop() {
		(new Thread(new Runnable() {
			@Override
			public void run() {
				String deviceName = currentDevice.getName();
				try {
					byte[] buffer = new byte[1024];
					while (true) {
						int read = currentInputStream.read(buffer);
						if (read > 0) {
							final byte[] data = Arrays.copyOfRange(buffer, 0, read);
							Log.d(TAG, "Read " + read + " bytes from " + deviceName + ": " + Arrays.toString(data));
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									String text = tvReceivedData.getText() + new String(data);
									tvReceivedData.setText(text);
								}
							});
						}
					}
				} catch (IOException ex) {
					toast("Error reading, may be EOF", ex);
				}
				currentInputStream = null;
				disconnectScanner();
			}
		})).start();
	}

	private void disconnectScanner() {
		if (currentSocket != null) {
			try {
				currentSocket.close();
			} catch (IOException ex2) {
				toast("Cannot close socket", ex2);
			}
		}

		currentDevice = null;
		currentSocket = null;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvCurrentConnection.setText("");
			}
		});
	}

	/* ================================================== */
	// Utils

	private void toast(final String message) {
		Log.d(TAG, message);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void toast(final String message, Exception ex) {
		Log.e(TAG, message, ex);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void prog(final String title, final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (prog == null) {
					prog = ProgressDialog.show(MainActivity.this, title, message, true, false);
				} else {
					prog.setTitle(title);
					prog.setMessage(message);
				}
			}
		});
	}

	private void progDismiss() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (prog != null) {
					prog.dismiss();
				}
				prog = null;
			}
		});
	}

}
