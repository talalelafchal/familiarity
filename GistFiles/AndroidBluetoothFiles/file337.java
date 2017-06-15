package com.example.ble;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;
import static android.bluetooth.BluetoothDevice.EXTRA_RSSI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final UUID MY_UUID = 
			UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	public static final String MY_NAME = "shikajiro@gmail.com";
	
	private BluetoothAdapter btAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btAdapter = getDefaultAdapter();

		// 【作法】BTを利用可能にするよう促す
		if (!btAdapter.isEnabled()) {
			// adapter.setEnable();このメソッドはない
			Intent intent = new Intent(ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, 1);
		}

		// BTを探す
		IntentFilter intentFilter = new IntentFilter(
				ACTION_FOUND);
		registerReceiver(mReceiver, intentFilter);
		
		Button findButton = (Button) findViewById(R.id.findButton);
		findButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btAdapter.startDiscovery();				
			}
		});
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			if (ACTION_FOUND.equals(intent.getAction())) {
				BluetoothDevice device = intent
						.getParcelableExtra(EXTRA_DEVICE);
				int rssi = intent.getShortExtra(EXTRA_RSSI, Short.MIN_VALUE);

				Log.i("BT", String.format("name[%s],address[%s],rssi[%d]",
								device.getName(), 
								device.getAddress(), 
								rssi
						));

				//接続処理
				if("shikajiro7".equals(device.getName())){
					Log.i("BT","shikajiro7 found");
					btAdapter.cancelDiscovery();
					
					try {
						BluetoothSocket socket = device.createRfcommSocketToServiceRecord(
								MY_UUID);
						socket.connect();
						new SendTask().execute(socket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	};
	
	class SendTask extends AsyncTask<BluetoothSocket, Void, String> {
		
		@Override
		protected String doInBackground(BluetoothSocket... params) {
			Log.i("BT", "start Send");
			BluetoothSocket socket = params[0];
			OutputStream os;
			try {
				os = socket.getOutputStream();
				os.write("渡したいメッセージ".getBytes());
				
				InputStream is = socket.getInputStream();
				byte[] buffer = new byte[1024];
				is.read(buffer);
				String result = new String(buffer);
				return result.trim();
			} catch (IOException e) {
				Log.i("BT", "Send error");
				e.printStackTrace();
			} finally{
				try {
					if(socket != null) socket.close();
				} catch (IOException e) {}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.i("BT", "get data "+result);
			if(result == null) return;
			Toast.makeText(MainActivity.this, "データを受け取りました."+result, Toast.LENGTH_SHORT).show();
		}
	};

}
