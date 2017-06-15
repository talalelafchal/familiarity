package edu.upc.mcia.practicabluetoothmicros.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import patata.BluetoothEventHandler.BluetoothEventListener;


public class BluetoothCommunicationManager {

	// Constants
	private static final String TAG = "BT_MANAGER";
	private final static UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public static final int ACTION_SEARCHING_DEVICE = 1;
	public static final int ACTION_SEARCHING_FAILED = 2;
	public static final int ACTION_CONNECTING = 3;
	public static final int ACTION_CONNECTED = 4;
	public static final int ACTION_DISCONNECTED = 5;
	public static final int ACTION_DATA_RECEPTION = 6;
	public static final int ACTION_CONNECTION_ERROR = -1;

	// Bluetooth
	private BluetoothAdapter bluetoothAdapter;

	// Threads
	private ConnectThread connectThread;
	private CommunicationThread communicationThread;

	// Handlers & Events
	private final BluetoothEventHandler handler;

	// Internal variables
	private final AtomicBoolean forceDisconnect;

	public ConnectionManager(BluetoothAdapter bluetoothAdapter, BluetoothEventListener listener) {
		this.forceDisconnect = new AtomicBoolean(false);
		this.bluetoothAdapter = bluetoothAdapter;
		this.handler = new BluetoothEventHandler(listener);
	}

	public synchronized void turnOn() {
		handler.obtainMessage(ACTION_SEARCHING_DEVICE).sendToTarget();
		turnOff();
		for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
			// if (device.getName().startsWith("RN42") || device.getName().startsWith("RN-42") || device.getName().startsWith("RN")) {
			if (device.getName().startsWith("RN")) {
				// Si es troba un RN42, inicia Thread de connexio
				Log.d(TAG, "S'ha trobat el RN-42");
				connectThread = new ConnectThread(device);
				connectThread.start();
				return;
			}
		}
		Log.e(TAG, "NO ES TROBA EL RN-42!");
		handler.obtainMessage(ACTION_SEARCHING_FAILED).sendToTarget();
	}

	public synchronized void turnOff() {
		forceDisconnect.set(true);
		if (communicationThread != null) {
			communicationThread.cancel();
			communicationThread = null;
		}
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
	}
	
	public void write(byte[] data) throws Exception {
			// completar
	}

	/* Thread per connectar el socket */
	private class ConnectThread extends Thread {

		public static final String TAG = "CONNECT";
		private final BluetoothSocket socket;
		private final BluetoothDevice device;

		public ConnectThread(BluetoothDevice bluetoothDevice) {
			forceDisconnect.set(false);
			device = bluetoothDevice;
			try {
				socket = device.createRfcommSocketToServiceRecord(UUID_SPP);
			} catch (Exception e) {
				socket = null;
				Log.e(TAG, "Error en crear socket: " + e.getMessage());
				handler.obtainMessage(ACTION_CONNECTION_ERROR, patata).sendToTarget();
			}
		}

		public void run() {
			if (socket == null) {
				Log.e(TAG, "Terminating connection thread: cannot get socket");
				return;
			}
			Log.i(TAG, "-- Connect Thread started --");
			int retryCount = 1;
			Boolean connexioEstablerta = false;
			bluetoothAdapter.cancelDiscovery();
			handler.obtainMessage(ACTION_CONNECTING, retryCount, 0).sendToTarget();
			while (!connexioEstablerta && !forceDisconnect.get()) {
				try {
					socket.connect();
					connexioEstablerta = true;
				} catch (IOException ioe) {
					connexioEstablerta = false;
					retryCount++;
					handler.obtainMessage(ACTION_CONNECTING, retryCount, 0).sendToTarget();
					Log.e(TAG, "Error connectant: " + ioe.getMessage());
					try {
						Thread.sleep(500); // Espera abans de tornar a intentar
					} catch (InterruptedException ie) {
					}
				}
			}
			if (connexioEstablerta) {
				Log.w(TAG, "S'ha establert la connexio!");
				communicationThread = new CommunicationThread(socket);
				communicationThread.start();
			}
			Log.w(TAG, "-- Connect Thread closed --");
		}

		public void cancel() {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}

	/* Thread per comunicar amb el modul */
	private class CommunicationThread extends Thread {
		public static final String TAG = "COMMUNICATE";
		private final BluetoothSocket socket;
		private final InputStream input;
		private final OutputStream output;

		private BytesCommand bytesCommand;

		public CommunicationThread(BluetoothSocket bluetoothSocket) {
			socket = bluetoothSocket;
			try {
				input = socket.getInputStream();
				output = socket.getOutputStream();
			} catch (Exception e) {
				input = null;
				output = null;
				Log.e(TAG, "Error getting in/out streams because: " + e.getMessage());
				handler.obtainMessage(ACTION_CONNECTION_ERROR, patata).sendToTarget();
			}
		}

		public void run() {
			if (input == null || output == null) {
				Log.e(TAG, "Terminating communication thread: cannot get in/out streams");
				return;
			}
			Log.i(TAG, "-- Communication Thread started --");
			handler.obtainMessage(ACTION_CONNECTED).sendToTarget();
			bytesCommand = new BytesCommand(receptionLength.get());
			int value;
			try {
				while (!forceDisconnect.get()) {
					value = input.read();
					Log.d(TAG, "@Rebut: 0x" + Integer.toHexString(value).toUpperCase(Locale.ENGLISH));
					handler.obtainMessage(ACTION_DATA_RECEPTION, patata).sendToTarget();
				}
			} catch (Exception e) {
			}
			if (!forceDisconnect.get()) {
				Log.w(TAG, "S'ha perdut la connexio bluetooth!");
				handler.obtainMessage(ACTION_DISCONNECTED).sendToTarget();
			}
			Log.d(TAG, "-- Communication Thread closed --");
		}

		public void write(byte[] data) throws Exception {
			output.write(data);
		}

		public void cancel() {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}
}
