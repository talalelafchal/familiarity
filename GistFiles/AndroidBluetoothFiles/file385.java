package com.example.ble;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AcceptActivity extends Activity {

	private BluetoothAdapter btAdapter;
	private BluetoothServerSocket serverSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accept);

		btAdapter = getDefaultAdapter();

		// 【作法】BTを利用可能にするよう促す
		if (!btAdapter.isEnabled()) {
			// adapter.setEnable();このメソッドはない
			Intent intent = new Intent(ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, 1);
		}

		// BTを発見可能にする。
		if (btAdapter.getScanMode() != SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent intent = new Intent(ACTION_REQUEST_DISCOVERABLE);
			intent.putExtra(EXTRA_DISCOVERABLE_DURATION, 3000);
			startActivityForResult(intent, 2);
		}

		Button acceptButton = (Button) findViewById(R.id.acceptButton);
		acceptButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AcceptTask().execute();				
			}
		});

	}

	class AcceptTask extends AsyncTask<Void, Void, BluetoothSocket> {

		@Override
		protected BluetoothSocket doInBackground(Void... params) {
			Log.i("BT", "start Accept");
			try {
				if (serverSocket == null){
					serverSocket = btAdapter
							.listenUsingRfcommWithServiceRecord(
									"My Mail Address", MainActivity.MY_UUID);
				}
				Log.i("BT","connect start");
				BluetoothSocket connected = serverSocket.accept();
				Log.i("BT","connected");
				return connected;
			} catch (IOException e) {
				Log.e("BT","error", e);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(BluetoothSocket result) {
			Log.i("BT", "");
			if (result == null)
				return;

			ReceiveTask receiveTask = new ReceiveTask();
			receiveTask.execute(result);
		}
	}
	public class ReceiveTask extends AsyncTask<BluetoothSocket, Void, String> {
		
		@Override
		protected String doInBackground(BluetoothSocket... params) {
			Log.i("BT", "start Send");
			BluetoothSocket socket = params[0];
			OutputStream os;
			try {
				os = socket.getOutputStream();
				os.write("BT通信出来ました！おめでとう！！".getBytes());
				
				InputStream is = socket.getInputStream();
				byte[] buffer = new byte[1024];
				is.read(buffer);
				String result = new String(buffer);
				socket.close();
				return result.trim();
			} catch (IOException e) {
				Log.i("BT", "Send error");
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result == null) return;
			Log.i("BT", "get data "+result);
			new AcceptTask().execute();
		}
	}
}
