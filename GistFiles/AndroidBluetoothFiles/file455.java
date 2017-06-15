package linz.jku;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import service.ShakeContactService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Shaking extends Activity {

	private final String TAG = "Shaking";
	private boolean service = false;
	private final BluetoothManager bt = BluetoothManager.getBluetoothManager();
	private static final int SERVER = 0;
	private static final int CLIENT = 1;
	private Button bServer;
	private Button bClient;
	private ProgressDialog pp;
	private BTNameChangeReceiver btNCR;
	private BTStateReceiver btSR;
	private String previousName;
	private boolean client = false;
	private static int code = 1;
	private static Uri uri = null;
	
	/** 
	 * Called when the activity is first created. 
	 * 
	 **/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shake);
		
		// stop the service on create
		service = getBaseContext().stopService(
				new Intent(getBaseContext(), ShakeContactService.class));

		bServer = ((Button) findViewById(R.id.Receptor));
		bClient = ((Button) findViewById(R.id.Emisor));
		// Server button pressed
		bServer.setOnClickListener(bServerListener);
		// Client button pressed
		bClient.setOnClickListener(bClientListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (service) {
			startService(new Intent(getBaseContext(), ShakeContactService.class));
			Log.i(TAG, "Service restarted");
		}
	}

	/**
	 * Show a message in the screen with the info share
	 * @param type
	 */
	private void progressDialog(int type) {
		pp = new ProgressDialog(this);
		pp.setOnDismissListener(odlProgressDialog);
		if (type == SERVER) {
			pp.setMessage("Server launching...");
			pp.setTitle("Receiving");
			pp.show();
		} else if (type == CLIENT) {
			pp.setMessage("Client launching...");
			pp.setTitle("Sending");
			pp.show();
		}
	}

	/**
	 * Create a new file with the bytes received, transform the file in Uri and
	 * make persist it in the Contact list
	 * 
	 * @param
	 */
	public void setContactInDevice(byte[] stream) {

		// Create new file and OutputStream and write inside the bytes received
		File file = new File("Contact");
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			out.write(stream);
			out.close();
		} catch (FileNotFoundException e) {
			Log.e("DataReceiver", e.toString());
		} catch (IOException e) {
			Log.e("DataReceiver", e.toString());
		}

		// Create a Uri.Builder and a Uri using the File path
		Uri.Builder builder = new Uri.Builder();
		builder.path(file.getPath());

		// Add the contact to contact list
		Intent createContact = new Intent(
				ContactsContract.Intents.Insert.ACTION, builder.build());				
		startActivity(createContact);

		
	}

	/*
	 * Listeners
	 */

	private OnClickListener bServerListener = new OnClickListener() {
		public void onClick(View v) {
			Context context = getApplicationContext();
			progressDialog(SERVER);
			CharSequence text = "Launching the Server";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			
			// Enabling the device
			if (bt.isEnable()) {
				bt.enableDevice(context); // Registers ON state
				// Change the bluetooth name of the device,
				// it helps to recognise the device
				btNCR = new BTNameChangeReceiver();
				context.registerReceiver(btNCR, new IntentFilter(
						BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED));
				
				previousName = bt.changeName();
				
				// Enabling the device visibility
				bt.enableVisibility(context);
				return;
			}
			btSR = new BTStateReceiver();
			context.registerReceiver(btSR, new IntentFilter(
					BluetoothAdapter.ACTION_STATE_CHANGED));
			bt.enableDevice(context);
		}
	};

	private OnClickListener bClientListener = new OnClickListener() {

		public void onClick(View v) {
			Context context = getApplicationContext();
			progressDialog(CLIENT);
			CharSequence text = "Launching the client";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			Log.i(TAG, "Client launched");			
			client = true;
			
			if (bt.isEnable()) {
				bt.enableDevice(context); // Registers ON status
				bt.launchEmisor(context, pp);
			} else {
				btSR = new BTStateReceiver();
				context.registerReceiver(btSR, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				bt.enableDevice(context);
			}
		}
	};

	/**
	 * Set the old previous state
	 */
	private OnDismissListener odlProgressDialog = new OnDismissListener() {

		public void onDismiss(DialogInterface dialog) {
			if(client){
				bt.setBluetoothToPreviousState();
			}
			finish();
			
		}
	};

	private class BTNameChangeReceiver extends BroadcastReceiver {

		private final String TAG = "BTNameChangeReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle caca = intent.getExtras();
			// search the other device, knowing the "default" name
			if (caca.get(BluetoothAdapter.EXTRA_LOCAL_NAME.toString()).equals(
					BluetoothManager.CONNECTION_KEY)) {
				context.unregisterReceiver(btNCR);
				Log.i(TAG, "Server launched");
				// launch the bluetooth server
				bt.launchServer(context, pp, previousName);
			} else {
				return;
			}
		}
	}

	private class BTStateReceiver extends BroadcastReceiver {

		private final String TAG = "btStateReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle caca = intent.getExtras();
			
			// Open the Bluetooth receiver
			if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE)) {
				if (caca.get(BluetoothAdapter.EXTRA_STATE).equals(
						BluetoothAdapter.STATE_ON)) {
					Log.v(TAG, "Bluetooth ON");
					if(client){
						bt.launchEmisor(context, pp);
						context.unregisterReceiver(btSR);
						return;
					}
					btNCR = new BTNameChangeReceiver();
					context.registerReceiver(btNCR, new IntentFilter(
							BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED));
					previousName = bt.changeName();

					// Enabling the device visibility
					bt.enableVisibility(context);
					context.unregisterReceiver(btSR);
				}
			}
		}
	}

	/**
	 * Get the code of the object, used for select a contact
	 * or a MP3 file
	 * @return integer with the code
	 */
	public static int getCode() {
		return code;
	}

	/**
	 * Return the uri of the object
	 * @return uri
	 */
	public static Uri getUri() {
		return uri;
	}

	/**
	 * Set the code of the object, used for select a contact
	 * or a MP3 file
	 * @param i
	 */
	public static void setCode(int i) {
		Shaking.code = i;
		
	}

	/**
	 * Set a new uri
	 * @param uri2
	 */
	public static void setUri(Uri uri2) {
		Shaking.uri = uri2;
		
	}

	
}