package com.veretenenko.a2dpconnector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothA2dp;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;


public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = new TextView(this);
        tv.setText("hello!");
        setContentView(tv);
        //BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        //bt.enable();
        //AudioManager am = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        try {
        	BluetoothA2dp bt = new BluetoothA2dp(this);
        	tv.setText("OK");
        	String text = "";
        	Field f = bt.getClass().getDeclaredField("mService");
        	f.setAccessible(true);
        	IBluetoothA2dp service = (IBluetoothA2dp) f.get(bt);
        	/*Method[] methods = service.getClass().getDeclaredMethods();
        	for (Method method : methods) {
        		text += "\r\n" + method.getName();
        	}*/
        	BluetoothDevice[] devices = service.getConnectedSinks();
        	for (BluetoothDevice device : devices) {
        		text += "\r\n" + device.getName();
        	}
        	tv.setText(text);
        } catch (Exception e) {
        	tv.setText(e.toString());
			e.printStackTrace();
        }
    }
}