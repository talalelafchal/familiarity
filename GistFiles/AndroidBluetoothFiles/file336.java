package com.example.bluetooth2;
import android.bluetooth.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

abstract public class BluetoothSerialThread extends Thread
{
		protected final String TAG = "BluetoothSerialThread";
		
		abstract protected void onDisconnected();
		abstract protected void onConnected();
		abstract protected void onConnecting();
		abstract protected void onRead(String line);
		
		public BluetoothSerialThread(String address)
		{
				this.address = address;
				btAdapter = null;
				btDevice = null;
				btSocket = null;
				closing = false;
				closed = false;
				handler = new Handler() {
						public void handleMessage(android.os.Message msg)
						{
								switch (msg.what)
								{
										case RECIEVE_MESSAGE:													// if receive massage
												byte[] readBuf = (byte[]) msg.obj;
												String line = new String(readBuf, 0, msg.arg1);					// create string from bytes array

												onRead(line);
												//Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
												break;

										case CONNECTED_MESSAGE:
												onConnected();
												break;

										case DISCONNECTED_MESSAGE:
												onDisconnected();
												break;

										case CONNECTING_MESSAGE:
												onConnecting();
												break;
								}
						}
				};
		}

		/**
		 * @brief  shut down the connection and stop the thread
		 */

		public void close()
		{
				closing = true;
				try 
				{
						btSocket.close();
				} 
				catch (IOException e2) 
				{
						Log.d(TAG, "IOException in ConnectedThread.close()... " + e2.getMessage() + ".");
				}
				try
				{
						join();
				}
				catch (InterruptedException e)
				{
						Log.w(TAG, "ConnectedThread.join() in ConnectedThread.close() interrupted...");
				}
				closed = true;
		}

		public void run()
		{
				while(!closing)
				{
						handler.obtainMessage(CONNECTING_MESSAGE).sendToTarget();

						btAdapter = BluetoothAdapter.getDefaultAdapter();

						if((btAdapter == null) || (!btAdapter.isEnabled()))
						{
								Log.d(TAG, "...Bluetooth adapter OFF or not found...");
								closing = true;
								handler.obtainMessage(DISCONNECTED_MESSAGE).sendToTarget();
								continue;
						}

						// Set up a pointer to the remote node using it's address.
						btDevice = btAdapter.getRemoteDevice(address);

						// Two things are needed to make a connection:
						//   A MAC address, which we got above.
						//   A Service ID or UUID.  In this case we are using the
						//     UUID for SPP.

						try 
						{
								btSocket = null;
								if (Build.VERSION.SDK_INT >= 10)
								{
										try
										{
												final Method  m = btDevice.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
												btSocket = (BluetoothSocket) m.invoke(btDevice, MY_UUID);
										}
										catch (Exception e)
										{
												Log.e(TAG, "Could not create Insecure RFComm Connection", e);
										}
								}
								if(btSocket == null)
								{
										btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
								}

						} 
						catch (IOException e) 
						{
								Log.w(TAG, "In onResume() and socket create failed: " + e.getMessage() + ".");
								handler.obtainMessage(DISCONNECTED_MESSAGE).sendToTarget();
								continue;
						}

						// Discovery is resource intensive.  Make sure it isn't going on
						// when you attempt to connect and pass your message.
						btAdapter.cancelDiscovery();

						// Establish the connection.  This will block until it connects.
						Log.d(TAG, "...Connecting...");
						try
						{
								btSocket.connect();
								Log.d(TAG, "....Connection ok...");
						}
						catch (IOException e)
						{
								Log.d(TAG, "...Connection failure: btSocket.connect() raised " + e.getClass().getName() + ": " + e.getMessage());
								try
								{
										btSocket.close();
								}
								catch (IOException e2)
								{
										Log.w(TAG, "Fatal Error In onResume() and unable to close socket during connection failure: " + e2.getMessage());
								}
								handler.obtainMessage(DISCONNECTED_MESSAGE).sendToTarget();
								continue;
						}

						// Create a data stream so we can talk to server.
						Log.d(TAG, "...Create Socket...");

						handler.obtainMessage(CONNECTED_MESSAGE).sendToTarget();

						InputStream tmpIn = null;
						OutputStream tmpOut = null;

						// Get the input and output streams, using temp objects because
						// member streams are final
						try
						{
								tmpIn = btSocket.getInputStream();
								tmpOut = btSocket.getOutputStream();
						}
						catch (IOException e)
						{ 

						}

						mmInStream = tmpIn;
						mmOutStream = tmpOut;

						byte[] buffer = new byte[4096];  // buffer store for the stream
						int bytes; // bytes returned from read()
						StringBuilder sbuf = new StringBuilder();

						// Keep listening to the InputStream until an exception occurs
						while (true)
						{
								try
								{
										// Read from the InputStream
										bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
										String strb = new String(buffer, 0, bytes);					// create string from bytes array
										sbuf.append(strb);												// append string
										int endOfLineIndex = sbuf.indexOf("\n");							// determine the end-of-line
										while (endOfLineIndex >= 0)
										{ 											// if end-of-line,
												String line = sbuf.substring(0, endOfLineIndex);				// extract string
												sbuf.delete(0, endOfLineIndex + 1);										// and clear
												endOfLineIndex = sbuf.indexOf("\n");
												handler.obtainMessage(RECIEVE_MESSAGE, line.length(), -1, line.getBytes()).sendToTarget();		// Send to message queue Handler
										}
								}
								catch (IOException e)
								{
										break;
								}
						}

						try
						{
								btSocket.close();
						}
						catch (IOException e)
						{
								Log.w(TAG, "Error in btSocket.close() at end of ConnectedThread.run()" + e.getMessage());
						}
						handler.obtainMessage(DISCONNECTED_MESSAGE).sendToTarget();
				}
		}

		/* Call this from the main activity to send data to the remote device */
		public boolean write(String message)
		{
				Log.d(TAG, "...Data to send: " + message + "...");
				byte[] msgBuffer = message.getBytes();
				try
				{
						mmOutStream.write(msgBuffer);
				}
				catch (IOException e)
				{
						Log.d(TAG, "...Error data send: " + e.getMessage() + "...");     
						return false;
				}
				return true;
		}
		
		// SPP UUID service
		private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

		private InputStream mmInStream;
		private OutputStream mmOutStream;
		private BluetoothSocket btSocket;
		private BluetoothDevice btDevice;
		private BluetoothAdapter btAdapter;
		private boolean closing, closed;
		private String address;
		private Handler handler;

		private final int RECIEVE_MESSAGE = 1;		// Recieved a complete line of data over SPP connection
		private final int CONNECTED_MESSAGE = 2;  // Established (or reestablished upon resume) SPP connection
		private final int DISCONNECTED_MESSAGE = 3; // Lost or shut down SPP connection. Triggers -.
		private final int CONNECTING_MESSAGE = 4; // Trying to reestablish SPP connection   <------'
}
