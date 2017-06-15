package com.example.bluetoothserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BluetoothServer extends Activity implements Runnable, OnClickListener {

  /**
	 * TAG
	 */
	private static final String TAG = "BT";

	/**
	 * Bluetooth Adapter
	 */
	private BluetoothAdapter mAdapter;

	/**
	 * Bluetooth Devices
	 */
	private BluetoothDevice mDevice;

	/**
	 * Bluetooth UUID
	 */
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/**
	 * Device Name
	 */
	private final String DEVICE_NAME = "FireFly-BE68";

	/**
	 * Socket
	 */
	private BluetoothSocket mSocket;

	/**
	 * Thread
	 */
	private Thread mThread;

	/**
	 * Threadの状態を表す
	 */
	private boolean isRunning;

	/**
	 * Button
	 */
	private Button mButton;

	/**
	 * Button
	 */
	private Button onButton;

	/**
	 * Button
	 */
	private Button offButton;

	/**
	 * Context
	 */
	private Context mContext;

	/**
	 * OnOff Flag
	 */
	private boolean onOff = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_server);
		mContext = this.getApplicationContext();
		mButton = (Button) findViewById(R.id.connectButton);
		mButton.setOnClickListener(this);

		onButton = (Button) findViewById(R.id.onButton);
		onButton.setOnClickListener(this);

		offButton = (Button) findViewById(R.id.offButton);
		offButton.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		isRunning = false;
		try {
			mSocket.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {

		InputStream mmInStream = null;
		OutputStream mmOutputStream = null;
		try {
			// 取得したデバイス名を使ってBluetoothでSocket接続
			mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
			mSocket.connect();

			mmInStream = mSocket.getInputStream();
			mmOutputStream = mSocket.getOutputStream();

			// InputStreamのバッファを格納
			byte[] buffer = new byte[1024];

			// 取得したバッファのサイズを格納
			int bytes;

			while (isRunning) {
				// InputStreamの読み込み　
				bytes = mmInStream.read(buffer);

				// String型に変換
				String readMsg = new String(buffer, 0, bytes);

				// Onの場合
				if (onOff) {
					String commandId = "1";
					mmOutputStream.write(commandId.getBytes());
				}
				// Offの場合
				else {
					String commandId = "0";
					mmOutputStream.write(commandId.getBytes());
				}

				// null以外なら表示
				if (readMsg.trim() != null && !readMsg.trim().equals("")) {
					Log.i(TAG, "value=" + readMsg.trim());
				} else {
					Log.i(TAG, "value=nodata");
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "error:" + e);
			try {
				mSocket.close();
			} catch (Exception ee) {
			}
			Looper.prepare();
			Toast.makeText(mContext, "エラー" + e, Toast.LENGTH_LONG).show();
			Looper.loop();
			isRunning = false;
		}
	}

	@Override
	public void onClick(View mView) {
		// TODO Auto-generated method stub
		if (mView.equals(mButton)) {
			mAdapter = BluetoothAdapter.getDefaultAdapter();
			Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
			for (BluetoothDevice device : devices) {
				Log.i(TAG, "DEVICE:" + device.getName());
				if (device.getName().equals(DEVICE_NAME)) {
					mDevice = device;
					Toast.makeText(mContext, "デバイス名:" + device.getName(), Toast.LENGTH_LONG).show();
				}
			}

			// Threadを起動し、Bluetooth接続
			mThread = new Thread(this);
			isRunning = true;
			mThread.start();
		} else if (mView.equals(onButton)) {
			onOff = true;
		} else if (mView.equals(offButton)) {
			onOff = false;
		}
	}
}