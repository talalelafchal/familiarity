package linz.jku;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class btStateReceiver extends BroadcastReceiver {

	private final String TAG = "btStateReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle caca = intent.getExtras();
		if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE)){
			if(caca.get(BluetoothAdapter.EXTRA_STATE).equals(BluetoothAdapter.STATE_ON)){
				Log.v(TAG,"Bluetooth ON");
			}
			Log.v(TAG,caca.get(BluetoothAdapter.EXTRA_STATE).toString());
			
		}
			
	}
}