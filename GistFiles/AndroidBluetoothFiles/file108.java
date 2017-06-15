package linz.jku;

import java.util.UUID;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class BluetoothManager {

	// SINGLETON PATTERN
	private static BluetoothManager bluetooth = null;
	private final UUID UUID = new UUID(0x0003, 0x0003);
	private final String TAG = "BluetoothManager";
	// Key used to find the destination device, both devices will change their
	// name whit this key
	public static final String CONNECTION_KEY = "OJETE";

	private BluetoothAdapter adapter = null;
	private BluetoothDevice device = null;
	private ProgressDialog progressDialog;
	private String PREVIOUS_NAME;
	private boolean bluetoothWasRunning = false;

	private BluetoothManager() {
		// Getting the Bluetooth adapter
		adapter = BluetoothAdapter.getDefaultAdapter();
		Log.i("Bluetooth", "\nAdapter: " + adapter);
		// Check for Bluetooth support in the first place
		// Emulator doesn't support Bluetooth and will return null
		if (adapter == null) {
			Log.e("Bluetooth", "\nBluetooth NOT supported. Aborting.");
		}
	}

	/**
	 * Start the transferData service. Specifically start AcceptThread to begin
	 * a session in listening mode (server).
	 */
	public synchronized void launchServer(Context c, ProgressDialog nopp,
			String previousName) {

		this.progressDialog = nopp;

		// Start the AsyncTask
		new AcceptThread().execute(adapter, CONNECTION_KEY, UUID, c,
				this.progressDialog, this);
	}

	private static synchronized void setBluetoothManager() {
		bluetooth = new BluetoothManager();
	}

	public static synchronized BluetoothManager getBluetoothManager() {
		if (bluetooth == null) {
			BluetoothManager.setBluetoothManager();
		}
		return bluetooth;
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	public synchronized void launchEmisor(Context c, ProgressDialog nopp) {
		this.progressDialog = nopp;

		Log.i("Bluetooth ", "conect to:" + device);

		// Create the Socket
		Log.i(TAG, "Starting the thread");
		new ConnectThread(c, this.progressDialog).execute(adapter,
				CONNECTION_KEY, UUID);

	}
	
	/**
	 * Change the name of the bluetooth, it is a friendly name for recognize
	 * the mobile which is the server
	 * @return
	 */
	public synchronized String changeName() {
		// get the actual name of the device
		PREVIOUS_NAME = adapter.getName();
		// set the friendly name
		if(PREVIOUS_NAME.equals(CONNECTION_KEY)){
			adapter.setName("Shake Contact Rox");
			return PREVIOUS_NAME;
		}
		adapter.setName(CONNECTION_KEY);
		Log.v(TAG,"Friendly name changed: "+CONNECTION_KEY);
		return PREVIOUS_NAME;
	}

	/**
	 * Enable the bluetooth of the device
	 * @param c
	 */
	public synchronized void enableDevice(Context c) {
		CharSequence text;
		if (!adapter.isEnabled()) {
			adapter.enable();
			text = "Turning bluetooth on";

		} else {
			text = "Bluetooth on";
			this.bluetoothWasRunning = true;
		}

		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(c, text, duration);
		toast.show();
	}

	/**
	 * Enable the visibility of the bluetooth during 30s
	 * @param c
	 */
	public synchronized void enableVisibility(Context c) {
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
		c.startActivity(discoverableIntent);
	}

	/**
	 * Return if the device is enable
	 * @return boolean
	 */
	public synchronized boolean isEnable() {
		return adapter.isEnabled();
	}

	public synchronized UUID getUuid() {
		return UUID;
	}

	/**
	 * Set the previous name and state of the bluetooth
	 */
	public void setBluetoothToPreviousState() {
		Log.d(TAG, "Setting bt to previous state. " + PREVIOUS_NAME);
		if (PREVIOUS_NAME != null) {
			adapter.setName(PREVIOUS_NAME);
		}
		if (!this.bluetoothWasRunning) {
			if (adapter != null) {
				Log.d(TAG, "Disabling Bluetooth");
				adapter.disable();
			}
		}
	}
}