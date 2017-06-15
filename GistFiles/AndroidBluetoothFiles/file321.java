package com.napster.bt.printer;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Prototype extends Activity implements OnClickListener	{

	private BluetoothAdapter bA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prototype);

		bA = BluetoothAdapter.getDefaultAdapter();
		findViewById(R.id.btn_print).setOnClickListener(this);

		IntentFilter stateF = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		IntentFilter discSF = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		IntentFilter discFF = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		IntentFilter foundF = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(bluetoothListener, stateF);
		registerReceiver(bluetoothListener, discSF);
		registerReceiver(bluetoothListener, discFF);
		registerReceiver(bluetoothListener, foundF);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.prototype, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_print)	{
			Log.d("[ Starting Printing Procedure ]");
			if(bA.isEnabled())	{
				Log.d("Bluetooth is enabled. Sending search request");
				bA.startDiscovery();
			}	else	{
				Log.d("Bluetooth is disabled. Sending enable request to the system");
				bA.enable();
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(bluetoothListener);
		super.onDestroy();
	}

	BroadcastReceiver bluetoothListener = new BroadcastReceiver() {

		BluetoothDevice device = null;

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()))	{
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				switch(state)	{
				case BluetoothAdapter.STATE_TURNING_ON:
					Log.d("Bluetooth is turning ON...");
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					Log.d("Bluetooth is turning OFF...");
					break;
				case BluetoothAdapter.STATE_ON:
					Log.d("Bluetooth is now ON Sending search request to the system");
					bA.startDiscovery();
					break;
				case BluetoothAdapter.STATE_OFF:
					Log.d("Bluetooth is now OFF");
					break;
				}
			}	else	if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction()))	{
				Log.d("Searching for BT devices, please wait...");
			}	else	if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()))	{
				Log.d("Searching for bluetooth devices finished!");
				if(device == null)	{
					Log.d("No bluetooth devices found. Exiting.");
					return;
				}
			}	else	if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))	{
				bA.cancelDiscovery();
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
				Log.d(">> Device Found : " + remoteDeviceName);

				UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Universal UUID
				try {
					BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
					Log.d("Trying to connect to : " + remoteDeviceName);
					socket.connect(); //UI Blocking code. Call in a separate thread in production level codes
					Log.d("Succesfully connected to " + remoteDeviceName);
					printReceipt(socket, "Hello Printer!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private void printReceipt(BluetoothSocket bS, String message)	{
		try {
			OutputStream oS = bS.getOutputStream();
			oS.write(0x1B);
			oS.write(0x40);
			oS.write(message.getBytes());
			oS.write(0xA);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bA.disable();
	}
}