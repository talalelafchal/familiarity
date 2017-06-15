/*
 * Copyright (C) 2010 Pye Brook Company, Inc.
 *               http://www.pyebrook.com
 *               info@pyebrook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * This software uses information from the document
 *
 *     'Bluetooth HXM API Guide 2010-07-22'
 *
 * which is Copyright (C) Zephyr Technology, and used with the permission
 * of the company. Information on Zephyr Technology products and how to 
 * obtain the Bluetooth HXM API Guide can be found on the Zephyr
 * Technology Corporation website at
 * 
 *      http://www.zephyr-technology.com
 * 
 *
 */

package cz.mpelant.sportsbuddy.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class BTService.
 */
public abstract class BTService {
  
	/** The Constant TAG. */
	protected static final String TAG = "HXMDemo";
	
	/** The Constant HXM_SERVICE_RESTING. */
	public static final int HXM_SERVICE_RESTING = 0;
	
	/** The Constant HXM_SERVICE_MSG_STATE. */
	public static final int HXM_SERVICE_MSG_STATE = 1;
	
	/** The Constant HXM_SERVICE_CONNECTING. */
	public static final int HXM_SERVICE_CONNECTING = 2;
	
	/** The Constant HXM_SERVICE_MSG_DEVICE_NAME. */
	public static final int HXM_SERVICE_MSG_DEVICE_NAME = 3;
	
	/** The Constant HXM_SERVICE_CONNECTED. */
	public static final int HXM_SERVICE_CONNECTED = 4;
	
	/** The Constant HXM_SERVICE_MSG_TOAST. */
	public static final int HXM_SERVICE_MSG_TOAST = 5;
	
	/** The Constant HXM_SERVICE_MSG_READ. */
	public static final int HXM_SERVICE_MSG_READ = 6;
	
	/** The Constant HXM_SERVICE_MSG_CONNECTION_LOST. */
	public static final int HXM_SERVICE_MSG_CONNECTION_LOST = 7;
	
	public static final int POLAR_SERVICE_RESTING = 8;
	
	public static final int POLAR_SERVICE_MSG_STATE = 9;
	
	public static final int POLAR_SERVICE_CONNECTING = 10;
	
	public static final int POLAR_SERVICE_CONNECTED = 11;
	
	public static final int POLAR_SERVICE_MSG_TOAST = 12;
	
	public static final int POLAR_SERVICE_MSG_READ = 13;
	
	public static final int POLAR_SERVICE_MSG_CONNECTION_LOST = 14;
	
	/** The Constant MY_UUID. */
	@SuppressWarnings("unused")
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	
	/** The m adapter. */
	private final BluetoothAdapter mAdapter;
	
	/** The m handler. */
	protected final Handler mHandler;
	
	/** The m connect thread. */
	private ConnectThread mConnectThread;
	
	/** The m connected thread. */
	protected ConnectedThread mConnectedThread;
	
	/** The m state. */
	private int mState;
	
	/** The cancelled. */
	private boolean cancelled;

	// -------------------------------------------------------------------------------------------------
	/**
	 * Instantiates a new bT service.
	 *
	 * @param context the context
	 * @param handler the handler
	 */
	public BTService(Context context, Handler handler) {
		cancelled = false;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = handler;
		setState(HXM_SERVICE_RESTING);
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	private void setState(int state) {
		mState = state;
		mHandler.obtainMessage(HXM_SERVICE_MSG_STATE, state, -1).sendToTarget();
	}

	/**
	 * Send info.
	 *
	 * @param msgID the msg id
	 * @param msgText the msg text
	 */
	private void sendInfo(int msgID, String msgText) {
		Message msg = mHandler.obtainMessage(msgID);
		Bundle bundle = new Bundle();
		bundle.putString(null, msgText);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
		Log.d(TAG, msgText);
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Cancel.
	 */
	public void cancel() {
		cancelled = true;
		stop();

	}

	/**
	 * Stop.
	 */
	private synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		setState(HXM_SERVICE_RESTING);
	}

	/**
	 * Connect.
	 *
	 * @param device the device
	 */
	public void connect(BluetoothDevice device) {
		cancelled = false;
		Log.d(TAG, "connect(): starting connection to " + device);

		if (mState == HXM_SERVICE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(HXM_SERVICE_CONNECTING);
	}

	/**
	 * Start connected thread.
	 *
	 * @param socket the socket
	 */
	protected abstract void startConnectedThread(BluetoothSocket socket);

	/**
	 * Connected.
	 *
	 * @param socket the socket
	 * @param device the device
	 */
	private void connected(BluetoothSocket socket, BluetoothDevice device) {
		Log.d(TAG, "connected() starting ");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to manage the connection and read the data from the device
		startConnectedThread(socket);
		sendInfo(HXM_SERVICE_MSG_DEVICE_NAME, device.getName());
		setState(HXM_SERVICE_CONNECTED);
		Log.d(TAG, "connected() finished");
	}

	/**
	 * Connection failed.
	 */
	private void connectionFailed() {
		sendInfo(HXM_SERVICE_MSG_TOAST, "connectionFailed(): Unable to connect device");
		setState(HXM_SERVICE_RESTING);
	}

	/**
	 * Connection lost.
	 */
	protected void connectionLost() {

		setState(HXM_SERVICE_RESTING);
		if (cancelled)
			sendInfo(HXM_SERVICE_MSG_TOAST, "connectionLost(): cancelled");
		else{
			sendInfo(HXM_SERVICE_MSG_TOAST, "connectionLost(): Device connection was lost");
			sendInfo(HXM_SERVICE_MSG_CONNECTION_LOST, "connectionLost()");
		}

	}

	// -------------------------------------------------------------------------------------------------

	/*
	 * This thread runs while attempting to create the outgoing connection with a HxM. It runs straight through; the connection either succeeds or fails.
	 */
	/**
	 * The Class ConnectThread.
	 */
	private class ConnectThread extends Thread {
		
		/** The mm socket. */
		private final BluetoothSocket mmSocket;
		
		/** The mm device. */
		private final BluetoothDevice mmDevice;

		/**
		 * Instantiates a new connect thread.
		 *
		 * @param device the device
		 */
		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				/* ****************************************************************************************
				 * IMPORTANT! IMPORTANT! IMPORTANT! IMPORTANT! IMPORTANT! IMPORTANT! IMPORTANT!
				 * ****************************************************************************************
				 * 
				 * There are some 'issues' with the Bluetooth issues with some versions of Android, and with some specific devices. Ordinarily all you would have to do to create
				 * the BLuetooth socket is use the create call, specifying the UID of the Bluetooth service profile that will be used for the connection. In the case of our
				 * application that call would look like this: tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
				 * 
				 * The problem is, sometimes when you make that call an error will return either from the attempt to create the socket, or later when the attempt is made to connect
				 * to the socket. The error code most often reported is 'Unable to start service discovery For device'.
				 * 
				 * Obviously this is a problem because you can't create a connection to any device on any known UID if the call does not work.
				 * 
				 * There is a technique that has been used to work around this issue. It is referred to as 'java reflection'. If you are not familiar and are interested, an
				 * Internet search for the term will give you lots of interesting reading on the topic.
				 * 
				 * The important thing for us is that it gives us a means to call directly into the Bluetooth rfcomm class/object avoiding whatever problem is present in the
				 * current Android+Bluetooth+Handset implementation.
				 * 
				 * Our application creates the connection using this technique as follows:
				 * 
				 * Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}); tmp = (BluetoothSocket) m.invoke(device, 1);
				 * 
				 * 
				 * CAUTION: The problem with doing this is that there isn't any guarantee that the call we are accessing will be there in future versions of the platform. And if it
				 * is there no guarantee that it will work in the same manner. If you look at the current implementation of the rfcomm object you will find several places where
				 * there is embedded commentary warning developers noting that the class is likely to change in the future.
				 * 
				 * When you build your applications, consider if it is appropriate to restrict the allowed platforms to the ones that you have thoroughly tested. You may also
				 * consider an implementation that uses both the standard implementation and the workaround implementation such that if the documented approach does not work, the
				 * workaround is attempted as a fall-back.
				 * 
				 * It is also advisable to make sure that the error reporting mechanism within your application is especially robust so that problems that users have that may end
				 * up being related to the bluetooth issue are quickly isolated.
				 */
				Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				tmp = (BluetoothSocket) m.invoke(device, 1);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
			mmSocket = tmp;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();
			try {
				mmSocket.connect();
			} catch (Exception e) {
				connectionFailed();
				try {
					mmSocket.close();
				} catch (Exception e2) {
					Log.e(TAG, "ConnectThread.run(): unable to close() socket during connection failure", e2);
				}

				BTService.this.stop();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BTService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (Exception e) {
				Log.e(TAG, "cancel(): close() of connect socket failed", e);
			}
		}
	}

	/*
	 * This thread runs during a connection with the Hxm. It handles all incoming data
	 */
	/**
	 * The Class ConnectedThread.
	 */
	protected abstract class ConnectedThread extends Thread {
		
		/** The mm socket. */
		private final BluetoothSocket mmSocket;
		
		/** The mm in stream. */
		protected final InputStream mmInStream;

		/**
		 * Instantiates a new connected thread.
		 *
		 * @param socket the socket
		 */
		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "ConnectedThread(): starting");

			mmSocket = socket;
			InputStream tmpIn = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
			} catch (IOException e) {
				Log.e(TAG, "ConnectedThread(): temp sockets not created", e);
			}

			mmInStream = tmpIn;

			Log.d(TAG, "ConnectedThread(): finished");

		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public abstract void run();

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "ConnectedThread.cancel(): close() of connect socket failed", e);
			}
		}
	}
}