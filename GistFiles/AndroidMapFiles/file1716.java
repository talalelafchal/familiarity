/*
licence pubilc domain
prepered by Shimon Doodkin

vibrate a vibrating bluetooth bracelet

a simple bt client it sends "RING" AT command to a device named "BRACELET"

it is a good "android bluetooth client example".

in class add filed:

ThinBTClient thinBTClient=new ThinBTClient(this);

use it:

if(thinBTClient.disabled)
	thinBTClient.connect();
else 
	thinBTClient.ring();

also add:

@Override
protected void onResume() {
	super.onResume();
	thinBTClient.onResume();
}


@Override
protected void onPause() {
	super.onPause();
	thinBTClient.onPause();
}

*/
package com.example.simple_bluetooth_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

public class ThinBTClient {
	
	public String deviceName="BRACELET";
	String address;// = "XX:XX:XX:XX:XX:XX"; //found by name
	
	//bracelet supports:
	//00001108-0000-1000-8000-00805f9b34fb //  HeadsetServiceClass_UUID
	//0000111e-0000-1000-8000-00805f9b34fb //  HandsfreeServiceClass_UUID
	//other
	//00001101-0000-1000-8000-00805F9B34FB  //  SerialPortServiceClass_UUID // http://forum.btframework.com/index.php?topic=172.0
	
	UUID MY_UUID ;//found by channel//=  UUID.fromString("00001108-0000-1000-8000-00805f9b34fb");
	int channel=0;
		
	
	private static final String TAG = "THINBTCLIENT";
	//private static final boolean D = true;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	// Well known SPP UUID (will *probably* map to
	// RFCOMM channel 1 (default) if not in use);
	// see comments in onResume().
	private Context context;
	ThinBTClient(Context context)
	{
		this.context=context;
	}
	
	// ==> hardcode your server's MAC address here <==

	public boolean disabled=true;
	private InputStream inputStream;
	
	
	/** Called when the activity is first created. */
	
	
	@SuppressLint("NewApi")
	public void connect() {
		try 
		{
		new Thread (new Runnable() {
			
			@Override
			public void run() {
			try{
		Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			toast("Bluetooth is not available.");
			//finish();
			disabled=true;
			return;
		}
		else disabled=false;
		
		mBluetoothAdapter.disable();//after closing a stream for some reason it is not getting closed seems a problem in chipset. as a solution i disable and enable adapter
		//maybe sleep requiered here
		mBluetoothAdapter.enable();
		//maybe sleep requiered here

		Log.e(TAG, "+ ABOUT TO SEARCH FOR A DEVICE +");

		if(disabled)
		{
			toast("Connection Impossible, No BT Adapter");
			Log.d(TAG, " BLUETOTH boolean disabled=true ");
			return;
		}
		
		//need check state better before connecting
		//http://code.tutsplus.com/tutorials/android-quick-look-bluetoothadapter--mobile-7813
			
		if (!mBluetoothAdapter.isEnabled()) {
			toast("Please enable your BT");
			disabled=true;
			//finish();
			return;
		}
		
		int state = mBluetoothAdapter.getState();
		if(state==BluetoothAdapter.STATE_TURNING_ON) {
			toast("Wait. BT State is TURNING ON.");
			disabled=true;
			//finish();
			return;
		}
		
		if(state!=BluetoothAdapter.STATE_ON) {
			toast("BT State is NOT ON");
			disabled=true;
			//finish();
			return;
		}

		
		//String deviceName="BRACELET";
		
		//00001108-0000-1000-8000-00805f9b34fb //  HeadsetServiceClass_UUID
		//0000111e-0000-1000-8000-00805f9b34fb //  HandsfreeServiceClass_UUID
		//UUID MY_UUID =  UUID.fromString("00001108-0000-1000-8000-00805f9b34fb");
		//00001101-0000-1000-8000-00805F9B34FB  //  SerialPortServiceClass_UUID // http://forum.btframework.com/index.php?topic=172.0

		
		//String address = "XX:XX:XX:XX:XX:XX";
		
		//get address and UUID for a name
		boolean found=false;
		Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice searchdevice : devices)
		{
			Log.e(TAG, "+ BLUETOOTH Device: \""+deviceName+"\" +");
			if(searchdevice.getName().equals(deviceName)) // Optionally you may filter by UUIDs of services offered by devices
			{
				address=searchdevice.getAddress();
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
				{
					MY_UUID=searchdevice.getUuids()[channel].getUuid();
					for(ParcelUuid uuid:searchdevice.getUuids())
						Log.e(TAG, "+ BLUETOOTH UUIDs list: "+uuid.getUuid().toString()+" +");
				}
				Log.e(TAG, "+ found BLUETOOTH Device "+deviceName+"  UUID "+MY_UUID.toString()+" +");
				found=true;
				break;
			};
		}
		
		if(!found)
		{
			Log.e(TAG, "+ "+deviceName+" not found +");
			toast(deviceName+" not found");
			disabled=true;
			return;
		}
			
		
		Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");

		// from here suppose you know the address and the uuid
		
		// When this returns, it will 'know' about the server,
		// via it's MAC address.
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		

		if(device.getBondState()!=BluetoothDevice.BOND_BONDED)
		{
			Log.e(TAG, "+ "+deviceName+" not bonded +");
			toast(deviceName+"not bonded. Conenct "+deviceName+" with phone first");
			disabled=true;
			return;
		}	
		
		// We need two things before we can successfully connect
		// (authentication issues aside): a MAC address, which we
		// already have, and an RFCOMM channel.
		// Because RFCOMM channels (aka ports) are limited in
		// number, Android doesn't allow you to use them directly;
		// instead you request a RFCOMM mapping based on a service
		// ID. In our case, we will use the well-known SPP Service
		// ID. This ID is in UUID (GUID to you Microsofties)
		// format. Given the UUID, Android will handle the
		// mapping for you. Generally, this will return RFCOMM 1,
		// but not always; it depends what other BlueTooth services
		// are in use on your Android device.
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			Log.e(TAG, "BT: Socket creation failed.", e);
			toast("BT "+deviceName+" Socket creation failed");
			disabled=true;
			return;
		}
		
		if(btSocket==null)
		{
			Log.e(TAG, "BT: Socket creation failed.");
			toast("BT "+deviceName+" Socket creation failed.");
			disabled=true;
			return;
		}
		
		// Discovery may be going on, e.g., if you're running a
		// 'scan for devices' search from your handset's Bluetooth
		// settings, so we call cancelDiscovery(). It doesn't hurt
		// to call it, but it might hurt not to... discovery is a
		// heavyweight process; you don't want it in progress when
		// a connection attempt is made.
		mBluetoothAdapter.cancelDiscovery();

		// Blocking connect, for a simple client nothing else can
		// happen until a successful connection is made, so we
		// don't care if it blocks.
		try {
			btSocket.connect();
			Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
		} catch (IOException e) {
			
			try {
				btSocket.close();
			} catch (IOException e2) {
				Log.e(TAG, 
					"ON RESUME: Unable to close socket during connection failure", e2);
				disabled=true;
				return;
			}
			//BT BRACELET Socket connection establishment failed.\n\n 
			toast("Please Disconnect "+deviceName+" from phone in Bluetooth properties of "+deviceName+"");
			
			Log.e(TAG, 
					"ON RESUME: Unable to establish socket connection. Please Disconnect "+deviceName+" from phone in Bluetooth properties of "+deviceName+"", e);
				disabled=true;
				return;
		}

		
		
		// Create a data stream so we can talk to server.
		Log.d(TAG, "+ ABOUT TO GET INPUT STREAM FROM SERVER +");

		try {
			outStream = btSocket.getOutputStream();
			
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
			toast("BT "+deviceName+" Output stream creation failed");
			disabled=true;
			return;
		}
		
		// Create a data stream so we can hear the server.
		Log.d(TAG, "+ ABOUT TO GET OUTPUT STREAM FROM SERVER +");

		try {
			inputStream = btSocket.getInputStream();
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Input stream creation failed.", e);
			toast("BT "+deviceName+" Input stream creation failed");
			disabled=true;
			return;
		}
		
		Log.d(TAG, "+ Creating BT Stream Reader Thread +");
		if(dataReaderThread!=null){ try{dataReaderThread.interrupt(); }catch(Exception e){} }
		dataReaderThread=new Thread(dataReader);
		if(dataReaderThread==null)return;
		dataReaderThread.start();
		//send("Hello message from client to server.",true);
		//ring();// test buzz
			}
			catch(Exception e)
			{
				Log.e(TAG, "BT Error in Connection thread.", e);
				toast("BT Error in Connect thread.\n"+e.getStackTrace().toString());
				disabled=true;
				return;
			}
			}
			
		}).start();
	}
	catch(Exception e)
	{
		Log.e(TAG, "BT Error in Connect .", e);
		toast("BT Error in Connection thread.\n"+e.getStackTrace().toString());
		disabled=true;
	}
	}
	
	public void ring() {
		send("RING");
	}
	
	public void send(String message) { send(message,true); }
	public void send(String message,boolean addNewline) {
		try {
		// Create a data stream so we can hear the server.
        Log.d(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");
		if(disabled)return;
		if(!btSocket.isConnected())
		{
			Log.e(TAG, "BT Send failed. "+deviceName+" is Disconnected.");
			toast("BT Send failed. "+deviceName+" is Disconnected.");
			return;
			
		}
		if(outStream==null)return;
			
		//String message = "Hello message from client to server.";
		if(addNewline)message="\r\n"+message+"\r\n";
		
		
		byte[] msgBuffer = message.getBytes();
		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {
			Log.e(TAG, "BT Exception during write. \""+message+"\"", e);
			disabled=true;
			onConnectionLost();
		}	
		
		} catch (Exception e) {
			Log.e(TAG, "BT Exception during write. \""+message+"\"", e);
			disabled=true;
			onConnectionLost();
		}	
	}
	
	public void onData(String line) {
	    Log.d(TAG, "< "+line);
	    if (line.indexOf("BRSF") >= 0)
	    {
	        send("+BRSF:0");
	        send("OK");
	    }
	    if (line.indexOf("CIND=") >= 0)
	    {
	        send("+CIND: (\"service\",(0,1)),(\"call\",(0,1))",true);
	        send("OK");
	    }
	    if (line.indexOf("CIND?") >= 0)
	    {
	        send("+CIND: 1,0");
	        send("OK");
	    }
	    if (line.indexOf("CMER") >= 0)
	    {
	        send("OK");
	    }
	    if (line.indexOf("CHLD=?") >= 0)
	    {
	        send("+CHLD: 0");
	        send("OK");
	    }
	}
	
	public void onConnectionLost() {

	}
	
	Thread dataReaderThread=null;
	
	Runnable dataReader = new Runnable() {
		
		@Override
		public void run() {
			try{
            // Keep listening to the InputStream while connected
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            
            try {
	            while ((line = in.readLine()) != null) {
	            	onData(line);
	            	if (Thread.interrupted())
	            	{
	            		Log.d(TAG, "interrupted");
	    				disabled=true;
	            		onConnectionLost();
	            		break;
	            	}
	            }
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
				disabled=true;
                onConnectionLost();
            }

            } catch (Exception e) {
                Log.e(TAG, "error in datareader thread", e);
				disabled=true;
                onConnectionLost();
            }
		}
	};
	
	public void onResume() {

		Log.e(TAG, "+ ON RESUME +");
		connect() ;
	}
	
	public void toast(final String text)
	{ 
		Handler mHandler = new Handler(context.getMainLooper()); // need to wrap with getMainLooper to run  on service thread. otherwise Toasts fail(and other things that require a looper thread) so to not repeat myself in every callback handling and use when required i just put it here for all callbacks.
	    mHandler.post(new Runnable() {
	        @Override
	        public void run() {
	        	Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	        }});
		
	}
	public void onPause() {
		
		
		Log.e(TAG, "- ON PAUSE -");
		disabled=true;
		if (outStream != null) {
			try {
				outStream.flush();				
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
			}
			try {
				outStream.close();				
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't close output stream.", e);
			}
			outStream=null;
		}
		
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't flush input stream.", e);
			}
			try {
				inputStream.close();				
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't close input stream.", e);
			}
			inputStream=null;
		}
		
		if(btSocket!=null)
		try	{
			btSocket.close();
		} catch (IOException e2) {
			Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
		}
		
		
		if(dataReaderThread!=null)
			try{
		dataReaderThread.interrupt();
		dataReaderThread=null;
			}catch(Exception e){}
	}

}

/*
class bluetooth_methods{
	public static final UUID MY_UUID = null;
	public bluetooth_methods(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

	/ **
		BluetoothChatService mChatService;
	    public void onStart() {
	        // If BT is not on, request that it be enabled.
	        // setupChat() will then be called during onActivityResult
	        if (!mBluetoothAdapter.isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        // Otherwise, setup the chat session
	        } else {
	            if (mChatService == null) setupChat();
	        }
	    }		
	    
	    public synchronized void onResume() {
	        // Performing this check in onResume() covers the case in which BT was
	        // not enabled during onStart(), so we were paused to enable it...
	        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
	        if (mChatService != null) {
	            // Only if the state is STATE_NONE, do we know that we haven't started already
	            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
	              // Start the Bluetooth chat services
	              mChatService.start();
	            }
	        }
	    }
	    
	    private void setupChat() {
	        // Initialize the BluetoothChatService to perform bluetooth connections
	        mChatService = new BluetoothChatService(this, mHandler);
	        
	    }
	    
	    public void onDestroy() {
	        // Stop the Bluetooth chat services
	        if (mChatService != null) mChatService.stop();
	    }
	    
	    private void sendMessage(String message) {
	        // Check that we're actually connected before trying anything
	        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
	            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
	            return;
	        }
	        // Check that there's actually something to send
	        if (message.length() > 0) {
	            // Get the message bytes and tell the BluetoothChatService to write
	            byte[] send = message.getBytes();
	            mChatService.write(send);
	            // Reset out string buffer to zero and clear the edit text field
	            mOutStringBuffer.setLength(0);
	            mOutEditText.setText(mOutStringBuffer);
	        }
	    }
	    ** /
	 
     //Start the chat service. Specifically start AcceptThread to begin a
     // session in listening (server) mode. Called by the Activity onResume() 
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
    }
    
     // Start the ConnectThread to initiate a connection to a remote device.
     // @param device  The BluetoothDevice to connect
     
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
   
    
     //* Stop all threads
     
    public synchronized void stop() {
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        setState(STATE_NONE);
    }
    
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private int mState;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    
     // This thread runs while attempting to make an outgoing connection
     // with a device. It runs straight through; the connection either
     // succeeds or fails.
     
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                //BluetoothChatService.this.start();
                return;
            }

            // Start the connected thread
            //connected(mmSocket, mmDevice);
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
        /
         // Write to the connected OutStream.
         // @param buffer  The bytes to write
         
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
}
*/
