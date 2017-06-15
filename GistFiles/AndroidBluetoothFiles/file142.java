package com.morkout.nbsocial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ClassicBluetoothClient extends Activity {

	public final static String TAG = "ClassicBluetoothClient";
	public static final int REQUEST_TO_ENABLE_BT = 100;
	private BluetoothAdapter mBluetoothAdapter;
	private TextView mTvInfo;
	private UUID MY_UUID = UUID.fromString("D04E3068-E15B-4482-8306-4CABFA1726E7");	
	
	// replace this with your own device names  
	private final static String CBT_SERVER_DEVICE_NAME = "OnePlus One";
	public static final int MAX_LINES = 7;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mTvInfo = (TextView) findViewById(R.id.info);
		writeText("Bluetooth Klient");
		
		//mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		//Gucken ob das Geraet Bluetooth beinhaltet
		if (mBluetoothAdapter == null) {
			Log.v(TAG, "Device does not support Bluetooth");
			Toast.makeText(ClassicBluetoothClient.this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();			
			return;
		}
		else { 
			//	Gucken ob BLT an ist.
			if (!mBluetoothAdapter.isEnabled()) {
				Log.v(TAG, "Bluetooth supported but not enabled");
				Toast.makeText(ClassicBluetoothClient.this, "Bluetooth supported but not enabled", Toast.LENGTH_LONG).show();							
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_TO_ENABLE_BT); 
			}else{
				Log.v(TAG, "Bluetooth supported and enabled");
				// discover new Bluetooth devices
				discoverBluetoothDevices();

				// find devices that have been paired 
				getBondedDevices();
			}        	
		}        

	}
	
	/**
	 * Schreibt einen String auf den TextView und löscht Wörter, wenn zu viele Linen auf dem View sind.
	 * @param data
	 */
	
	public void writeText(String data){
        mTvInfo.append(" " + data + System.getProperty("line.separator"));

        int linienAnzahl = mTvInfo.getLineCount() - MAX_LINES;
        if(linienAnzahl > 0) {
            int eolIndex = -1;
            CharSequence charSequence = mTvInfo.getText();
            for (int i = 0; i < linienAnzahl; i++) {
                do {
                    eolIndex++;
                } while (eolIndex < charSequence.length() && charSequence.charAt(eolIndex) != '\n');
            }
            if (eolIndex < charSequence.length()) {
                mTvInfo.getEditableText().delete(0, eolIndex + 1);
            } else {
                mTvInfo.setText("");
            }
        }
	}
	public void writeTextTime(String data)
	{
		writeText(WordService.getTime() + data);
	}
	
	
/**
 * 
 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TO_ENABLE_BT) { 
			discoverBluetoothDevices();
			getBondedDevices();
			return;
		}
	}	
/**
 * Findet Bluetooth-Geräte
 */

	void discoverBluetoothDevices () {
		// register a BroadcastReceiver for the ACTION_FOUND Intent 
		// to receive info about each device discovered.
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);       
		registerReceiver(mReceiver, filter); 
		mBluetoothAdapter.startDiscovery();
	}

	// for each device discovered, the broadcast info is received
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.v(TAG, "BroadcastReceiver on Receive - " + device.getName() + ": " + device.getAddress());
				String name = device.getName();

				// found another Android device of mine and start communication
				if (name != null && name.equalsIgnoreCase(CBT_SERVER_DEVICE_NAME)) {
					new ConnectThread(device).start();
				}
			}			
		}
	};

	protected void onDestroy() {
		
		//Beim zerstören den Receiver  unregistern.
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}	

	// Durch läuft alle Geräte und überprüft, ob ein Gerät den CBT_SERVER_DEVICE_NAME hat.
	//Mit dem Gerät verbindet es sich dann
	void getBondedDevices () {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			
			for (BluetoothDevice device : pairedDevices) {
				Log.v(TAG, "bonded device - " + device.getName() + ": " + device.getAddress());
				if (device.getName().equalsIgnoreCase(CBT_SERVER_DEVICE_NAME)) {
					Log.d(TAG, CBT_SERVER_DEVICE_NAME);
					new ConnectThread(device).start();
					break;
				}
			}
		}		
		else {
			Toast.makeText(ClassicBluetoothClient.this, "Keine gekopelten Geräte", Toast.LENGTH_LONG).show();			
		}
	}

	private class ConnectThread extends Thread {
		int bytesRead;
		int total;		
		private final BluetoothSocket mmSocket;
		public ConnectThread(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server code
				Log.v(TAG, "before createRfcommSocketToServiceRecord");
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
				Log.v(TAG, "after createRfcommSocketToServiceRecord");
			} catch (IOException e) { 
				Log.v(TAG, " createRfcommSocketToServiceRecord exception: "+ e.getMessage());
			}
			mmSocket = tmp;
		}
		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
			} catch (IOException e) {
				Log.v(TAG, e.getMessage());
				try {
					mmSocket.close();
				} catch (IOException closeException) { }
				return;
			}
			manConnectedSocket(mmSocket);
		}
		
		private void manConnectedSocket(BluetoothSocket socket)
		{
			ByteArrayOutputStream baos = null;
			try
			{
				InputStream instream = socket.getInputStream();
				baos = new ByteArrayOutputStream();
				StreamUtils.copyBuffered(instream, baos);
				final String word = baos.toString("UTF-8");
				writeTextTime(word);
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						writeTextTime(word);						
					}
				});
			}
			catch(final IOException ex)
			{	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					writeText("EXPETION" + ex.getMessage());
					
				}
			});
				
			}
			finally
			{
				StreamUtils.safeClose(baos);
				SocketUtils.safeClose(socket);
			}
		}		
		
		private void manageConnectedSocket(BluetoothSocket socket) {
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			ByteArrayOutputStream baos = null;
			

			try {
				InputStream instream = socket.getInputStream();
				baos = new ByteArrayOutputStream();
				bytesRead = -1;
				total = 0;
				while ((bytesRead = instream.read(buffer)) > 0) {
					total += bytesRead;
					baos.write(buffer, 0, bytesRead);
					Log.i(TAG, "bytesRead="+bytesRead+",bufferSize="+bufferSize+",total="+total);
					final String text = baos.toString("UTF-8");
					
					//Schreibt 
					runOnUiThread(new Runnable() {
						public void run() {
							writeTextTime(text);							
						}
					});						
				}
				StreamUtils.safeClose(baos);
				//baos.close();
				SocketUtils.safeClose(socket);
			} catch (IOException ex) {
				writeText(ex.getMessage());
			}
			finally
			{
				StreamUtils.safeClose(baos);
				SocketUtils.safeClose(socket);
			}
		}	
	}
}