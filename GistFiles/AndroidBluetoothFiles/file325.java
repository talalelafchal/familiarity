//Main necessary methoeds 
   // get the bluetooth device reference
    public BluetoothAdapter findMyBT(BluetoothAdapter myBt){
    	myBt = BluetoothAdapter.getDefaultAdapter();
    	if (myBt!= null) {
    		Log.i("BTDEV","BT Device Found!");
    		Log.i("BTDEV","" + myBt.getName());
    		return myBt;
    	} else {
    		Log.i("BTDEV","No BT Device Found!");
    		return null;
    	}
    }
    
	//ask to enable BT to user
    public void enableMyBT(BluetoothAdapter myBt) {
    	if ( ! myBt.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    actv.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}	
    }
    
    //ask to be visible for <visibility> seconds.
    public void beVisible() {
    	Log.i("BTDEV","Let me be visible. thanks.");
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    	discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, visibility);
    	this.actv.startActivity(discoverableIntent);
    }

///start listening for incoming data
//this methoed must run inside a thread.
    private void listenForConnection() {
    	try{
			BluetoothServerSocket bss = myBT.listenUsingRfcommWithServiceRecord("YOURSERVICENAME", UUID); //the UUID need to be GENERATED
			bs = bss.accept();
			bss.close();
			out = bs.getOutputStream();
			out.write("goodbye".getBytes()); //send a message
//....

			out.close(); //close it 
			bs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
