package linz.jku;

import java.io.IOException;
import java.util.UUID;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AcceptThread extends AsyncTask<Object, Void, String> {

	private BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter adapter;
	private UUID uuid;
	private String name;
	private ProgressDialog progressDialog;
	private final String TAG = "AcceptThread";
	private BluetoothManager btManager;
	private Context CONTEXT;

	@Override
	protected String doInBackground(Object... params) {
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		adapter = (BluetoothAdapter) params[0];
		name = (String) params[1];
		uuid = (UUID) params[2];
		CONTEXT = (Context) params[3];
		this.progressDialog = (ProgressDialog) params[4];
		this.btManager = (BluetoothManager) params[5];
		BluetoothServerSocket tmp = null;				

		try {
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = adapter.listenUsingRfcommWithServiceRecord(name, uuid);
		} catch (IOException e) {
		}
		mmServerSocket = tmp;
		DataReceiver rec;
		
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned

		while (true) {
			try {
				if (mmServerSocket != null) {
					Log.i(TAG, "Accepting connection");
					socket = mmServerSocket.accept(30000);

				}
			} catch (IOException e) {
				break;
			}
			// If a connection was accepted
			if (socket != null) {
				Log.i("INFO", "Starting data transfer");
				rec = new DataReceiver();
				rec.execute(socket,CONTEXT);
				break;
			}
		}
		return null;
	}

	@Override
	public void onPostExecute(String buffer) {
		try {
			
			mmServerSocket.close();			
		} catch (IOException e) {
			Log.e(TAG, "posExecute Exception");
		} finally {
			this.btManager.setBluetoothToPreviousState();
			this.progressDialog.dismiss();	
		}

	}

}