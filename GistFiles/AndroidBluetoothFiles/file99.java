//
//  BluetoothSPPTest.java
//
//  AndroidManifest.xmlで次の2つのpermissionを設定しておくこと。
//    android.permission.BLUETOOTH
//    android.permission.BLUETOOTH_ADMIN
//
package net.sabamiso.android.test.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BluetoothSPPTest extends Activity implements Runnable {
	ConsoleView console;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		console = new ConsoleView(this);
		setContentView(console);
        
		if (connect() == false) {
			log_e("BluetoothSPPTest connect failed...");
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		close();
		log_i("BluetoothSPPTest finished...");

		System.exit(0);
	}

	//////////////////////////////////////////////////////////////////
	private void log_i(String msg) {
    	Log.i("BluetoothSPPTest", msg);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	private void log_e(String msg) {
    	Log.e("BluetoothSPPTest", msg);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	int console_line = 0;
	private void print(String msg) {
		console.print(msg);
		Log.d("BluetoothSPPTest", msg);
		console_line ++;
		if (console_line > 25) {
			console.clear();
			console_line = 0;
		}
	}

	//////////////////////////////////////////////////////////////////
	BluetoothAdapter adapter;
	BluetoothDevice device;
	BluetoothSocket socket;
	InputStream is;
	OutputStream os;
	Thread read_thread;
	
	UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); 
	
	private boolean connect() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			log_e("BluetoothAdapter.getDefaultAdapter() error...");
			close();
			return false;
		}
		if (adapter.isEnabled() == false) {
			log_e("Bluetooth Adapter disabled...");
			close();
			return false;
		}
		
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		if (devices.size()==0) {
			log_e("adapter.getBondedDevices() returned null...");
			close();
			return false;
		}
        
		Iterator<BluetoothDevice> it = devices.iterator();
		while(it.hasNext()) {
			// とりあえず一番最後のデバイスを選択。手抜き。。
			BluetoothDevice d = it.next();
			log_i(d.getName());
			device = d;
		}
        
		try {
			socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
			socket.connect();
			is = socket.getInputStream();
			os = socket.getOutputStream();
		} catch (IOException e) {
			log_e(e.toString());
			close();
			return false;
		}
		
		read_thread = new Thread(this);
		read_thread.start();
        
		return true;
	}
	
	private void close() {
		if (socket != null) {
			try {
				is = null;
				os = null;
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
		if (device != null) {
			device = null;
		}
		if (adapter != null) {
			adapter = null;
		}
		
		try {
			read_thread.join();
		} catch (InterruptedException e) {
		}
		read_thread = null;
	}

	@Override
	public void run() {
		try {
			while(true) {
				// read data
				byte [] buf = new byte[256];
				int len = is.read(buf);

				// print
				String str = new String(buf, 0, len);
				print(str);
			}
		} catch (IOException e) {
			log_e(e.toString());
		}
	}

}