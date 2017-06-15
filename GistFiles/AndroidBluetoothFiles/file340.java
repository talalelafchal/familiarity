package linz.jku;

import java.io.IOException;
import java.util.UUID;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class ConnectThread extends AsyncTask<Object, Void, String> {

	private BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice;
	private Context mmContext;
	private String CONNECTION_KEY;
	private BluetoothAdapter adapter;
	private BluetoothSocket tmp = null;
	private UUID uuid;
	private final String TAG = "ConnectThread";

	private ProgressDialog progressDialog;


	public ConnectThread(Context c, ProgressDialog p) {
		
		mmContext = c.getApplicationContext();
		this.progressDialog = p;
		this.progressDialog.setMessage("Searching device...");
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				dismiss();
			}
		}, 30000);
	}

	@Override
	protected String doInBackground(Object... params) {

		Log.i(TAG, "----------STARTING THE THREAD1111111");
		CONNECTION_KEY = (String) params[1];	
		adapter = (BluetoothAdapter) params[0];
		uuid = (UUID) params[2];

		Log.i(TAG, "Finding device.........");
		
		// Create a BroadcastReceiver for ACTION_FOUND

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mmContext.registerReceiver(mReceiver, filter); // Don't forget to
														// unregister
		// during onDestroy

		adapter.startDiscovery();

		return null;
	}

	public boolean findDevice() {
		Log.i(TAG, "STARTING FIND DEVICE");
		// Get a BluetoothSocket to connect with the given
		// BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server
			// code
			tmp = InsecureBluetooth.createRfcommSocketToServiceRecord(mmDevice,
					uuid, true);

		} catch (Exception e) {

			Log.e(TAG, e.getMessage());
		}

		mmSocket = tmp;

		if (mmDevice != null) {
			Log.i(TAG, ("----------Device discovered: " + mmDevice.getName()));
			try {
				this.progressDialog.setMessage("Device found");
				if (mmDevice.getName().equals(CONNECTION_KEY)) {

					// If we're already discovering, stop it
					if (adapter.isDiscovering())
						adapter.cancelDiscovery();
					Log.i(TAG,
							("----------DEVICE FOUND: " + mmDevice.getName()));

					this.progressDialog.setMessage("Sending data...");

					Log.i(TAG, "----------STARTING THE THREAD");
					try {
						// Connect the device through the socket. This will
						// block
						// until it succeeds or throws an exception

						mmSocket.connect();

					} catch (IOException connectException) {
						Log.i("ConnectThread", "Error connecting mmSocket");
						try {
							mmSocket.close();
						} catch (IOException closeException) {
							Log.i("ConnectThread", "Error closing mmSocket");
						}
						return false;
					}
					Log.i("Bluetooth ", "connected, sending info to device: "
							+ mmDevice.getName());
					// Start the thread to manage the connection and perform
					// transmissions
					new DataSender().execute(mmSocket, null,
							this.progressDialog);
					// mDataTransfer.start();
					return true;
				}
			} catch (NullPointerException e) {
				Log.e(TAG, "Error, Retrying");
			}
		}
		return false;
	}

	@Override
	public void onPostExecute(String result) {
		try {
			if (mmSocket != null) {
				mmSocket.close();
				mmContext.unregisterReceiver(mReceiver);
			}
		} catch (IOException e) {
			Log.e("ConnectThread",
					"Error in OnPostExecute closing socket/unregistering receiver.");
		}

	}

	public void dismiss() {		
		this.progressDialog.dismiss();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.i(TAG, "BLUETOOTHDEVICE ACTION FOuND");
				// Get the BluetoothDevice object from the Intent
				try{
				mmDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(mmDevice != null && mmDevice.getName()!=null){
					if(mmDevice.getName().equals(CONNECTION_KEY)){
						findDevice();
					}	
				}	
				}catch(Exception e){
					Log.e(TAG, "---Error: "+e.getMessage());
				}
			}
		}
	};
}