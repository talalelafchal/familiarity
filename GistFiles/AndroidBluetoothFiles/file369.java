package com.dynofit;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import java.lang.Thread;
import java.lang.String;
import java.lang.Integer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.app.Activity;
import android.app.Service;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

//Simple class which has an infinite loop that continues
//once per second polling Unity.
class Repeat implements  Runnable
{
	private	int count;

	public Repeat()
	{
		count=0;
	}
	
	public void run() 
	{

		try 
		{

			for (;;) 
			{
				String message_text = "From Java " + Integer.toString(count);

				Log.w("dynofit", message_text);

				UnityPlayer.UnitySendMessage("Go1", "Blah", message_text);

				Thread.sleep(1000);
				
				count++;
			}
		} 
		catch (Exception e) 
		{
			Log.e("dynofit", "Java error in Repeat: " + e.getMessage());
		}
	}
}


public class wtf extends Service{
    static public int foo()
    {
    	 Log.w("dynofit", "hahahaha!");
    	 
    	 Repeat r = new Repeat();
    	 
    	 Thread th = new Thread(r);
    	 
    	 th.start();
    			
  	 
    	 return 0xc0ffee;   	 
    }
    
    
    public void scan_le()
    {
    	
    	 final BluetoothManager bluetoothManager =
                 (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    	 BluetoothAdapter adapter = bluetoothManager.getAdapter();
    	
    	   	
    	
    }
    
    static public wtf get_new_instance()
    {
    	return new wtf();
    }
    
}