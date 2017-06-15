package com.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.IBluetooth;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.IBinder;
import android.os.Message;

public class BluetoothListener {

	public static String btaddr;
	public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public BluetoothListener(Context c){
		startListener(c);
		connectToFORAP20B();
	}
	public static IBluetooth getIBluetooth() {
    	IBluetooth ibt = null;
    	try {
    	    Class c2 = Class.forName("android.os.ServiceManager");
    	    Method m2 = c2.getDeclaredMethod("getService",String.class);
    	    IBinder b = (IBinder) m2.invoke(null, "bluetooth");
    	    Class c3 = Class.forName("android.bluetooth.IBluetooth");
    	    Class[] s2 = c3.getDeclaredClasses();
    	    Class c = s2[0];
    	    Method m = c.getDeclaredMethod("asInterface",IBinder.class);
    	    m.setAccessible(true);
    	    ibt = (IBluetooth) m.invoke(null, b);
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return ibt;
    }
	public void startListener(Context c){
		System.out.println("starting bluetooth listener!!!!");
		//connect receiver
		final BroadcastReceiver mReceiver4 = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        //System.out.println("Connection received!");
		        
		        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        btaddr=device.getAddress();
		        System.out.println(device.getName()+" connected...");
		        System.out.println("status: "+device.getBondState());

	        	final IBluetooth ib=getIBluetooth();
		        try {
		        	if (device.getBondState()==10){
		        		device.getClass().getMethod("cancelPairingUserInput").invoke(device);
		        		device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, false);
		        		
		        		//ib.createBond(btaddr);
		        	}
		        	else if (device.getBondState()==12){// && device.getName().startsWith("Nonin_Medical_Inc.")){
		        		
		    	        
		        	}
		        	
		        	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		};
		IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		c.registerReceiver(mReceiver4, filter4);
		
		
		//disconnect receiver
		final BroadcastReceiver mReceiver5 = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        //System.out.println("Connection received!");
		    	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    	System.out.println(device.getName()+" disconnected...");
		    	btaddr=device.getAddress();
		        
		    }
		};
		IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		c.registerReceiver(mReceiver5, filter5);
		
		
		//disconnect request receiver
		final BroadcastReceiver mReceiver6 = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        //System.out.println("Connection received!");
		    	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    	System.out.println(device.getName()+" disconnect requested...");
		        
		    }
		};
		IntentFilter filter6 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		c.registerReceiver(mReceiver6, filter6);
		
		
		//bluetooth state change receiver
		final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        final String action = intent.getAction();

		        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
		            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
		                                                 BluetoothAdapter.ERROR);
		            switch (state) {
		            case BluetoothAdapter.STATE_OFF:
		            	//turn bluetooth back on
		            	BluetoothAdapter.getDefaultAdapter().enable();
		            	BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		                break;
		            case BluetoothAdapter.STATE_TURNING_OFF:
		                //setButtonText("Turning Bluetooth off...");
		                break;
		            case BluetoothAdapter.STATE_ON:
		                //setButtonText("Bluetooth on");
		            	BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		                break;
		            case BluetoothAdapter.STATE_TURNING_ON:
		            	//setButtonText("Turning Bluetooth on...");
		            	BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		                break;
		            }
		        }
		    }
		};
		IntentFilter btfilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		c.registerReceiver(bluetoothReceiver, btfilter);
	}
	
	public void connectToFORAP20B(){
		Thread t=new Thread(new Runnable(){

			@Override
			public void run() {
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

				List<String> s = new ArrayList<String>();
				for(BluetoothDevice bt : pairedDevices)
				   s.add(bt.getName());

				int i2=0;
				while (i2<s.size()){
					System.out.println(s.get(i2));
					if (s.get(i2).equals("TaiDoc-Device")){
						break;
					}
					i2++;
				}
				
				//get bt device
				BluetoothDevice dev=(BluetoothDevice) pairedDevices.toArray()[i2];
												
				//try connecting to device
				try {
					BluetoothSocket btsock=dev.createRfcommSocketToServiceRecord(uuid);
					btsock.connect();
					System.out.println("Connected!");
					final InputStream in=btsock.getInputStream();
					final OutputStream out=btsock.getOutputStream();
					
					//start listener for this device
					Thread lt=new Thread(new Runnable(){

						@Override
						public void run() {
							try{
								while(true){
									byte b=(byte)in.read();
									if (b==0x51){ //start of response
										byte b2=(byte)in.read();
										
										//parse b2
										if (b2==0x54){
											//device is letting us know it is going into communication mode, ignore input
											in.read();in.read();in.read();
											in.read();in.read();in.read();
											System.out.println("ForaP20B is in comm mode...");
										}
										else if (b2==0x2B){
											//device is telling us how many stored data points it has
											int strl=in.read();
											int strh=in.read();
											in.read();in.read();in.read();in.read();
											int str=strh+strl;
											System.out.println("Stored data points: "+str);
											
											//now request the data points, index 0 is the latest reading
											out.write(81);
											out.write(0x25);
											out.write(0);  //index
											out.write(0);
											out.write(0);
											out.write(0);
											out.write(163);
											//calculate checksum
											byte[] data = {(byte)81,0x25,(byte)0,(byte)0,(byte)0,(byte)0,(byte) 163};
											int checksum = 0;
											for (int i = 0; i < data.length; i++) {
											  checksum += (data[i] & 0xFF);
											}
											out.write(checksum);
											out.flush();
										}
										else if (b2==0x25){
											//device is giving us a date/time for a measurement
											int datel=in.read();
											int dateh=in.read();
											int min_type=in.read();
											int hour=in.read();
											in.read();in.read();
											System.out.println("Got date/time for measurement: "+datel+" "+dateh+", "+min_type+", "+hour);
											
											//now request the measurement readings, index 0 is the latest reading
											out.write(81);
											out.write(0x26);
											out.write(0);      //index 0
											out.write(0);
											out.write(0);
											out.write(0);
											out.write(163);
											//calculate checksum
											byte[] data = {(byte)81,0x26,(byte)0,(byte)0,(byte)0,(byte)0,(byte) 163};
											int checksum = 0;
											for (int i = 0; i < data.length; i++) {
											  checksum += (data[i] & 0xFF);
											}
											out.write(checksum);
											out.flush();
										}
										else if (b2==0x26){
											//device is sending measurement data
											System.out.println("Getting device measurement data...");
											
											//THE FOLLOWING IS A GUESS!!!!!! NEED UPDATED DATA FROM FORACARE!!!!
											int systolic=in.read();
											in.read();  //not sure what this is
											int diastolic=in.read();
											int heartrate=in.read();
											in.read();  //end byte (A5)
											in.read();  //checksum
											
											System.out.println("Systolic blood pressure: "+systolic+" mmHg");
											System.out.println("Diastolic blood pressure: "+diastolic+" mmHg");
											System.out.println("Heart Rate: "+heartrate+" bpm");
										}
									}
								}
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						
					});
					lt.start();
					
					Thread.sleep(3000);
					
					//get number of stored data points
					out.write(81);
					out.write(0x2B);
					out.write(0);
					out.write(0);
					out.write(0);
					out.write(0);
					out.write(163);
					//calculate checksum
					byte[] data = {(byte)81,0x2B,(byte)0,(byte)0,(byte)0,(byte)0,(byte) 163};
					int checksum = 0;
					for (int i = 0; i < data.length; i++) {
					  checksum += (data[i] & 0xFF);
					}
					out.write(checksum);
					out.flush();
					
					//Thread.sleep(10000);
					
					//btsock.close();
					//System.out.println("Disconnected!");
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		t.start();
		
	}
}
