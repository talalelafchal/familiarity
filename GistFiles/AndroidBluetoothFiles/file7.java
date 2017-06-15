package android.bluetooth;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

public class BluetoothA2dp implements IBluetoothA2dp {
	public BluetoothA2dp(Context c) {
		
	}

	public boolean connectSink(BluetoothDevice device) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean disconnectSink(BluetoothDevice device)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public BluetoothDevice[] getConnectedSinks() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public BluetoothDevice[] getNonDisconnectedSinks() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSinkPriority(BluetoothDevice device) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSinkState(BluetoothDevice device) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean resumeSink(BluetoothDevice device) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setSinkPriority(BluetoothDevice device, int priority)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean suspendSink(BluetoothDevice device) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
