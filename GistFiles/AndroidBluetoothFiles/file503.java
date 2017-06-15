package com.inex.ioioapp;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends IOIOActivity {
	ToggleButton toggleButton1;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		toggleButton1 = (ToggleButton)findViewById(R.id.toggleButton1);
	}
	
	class Looper extends BaseIOIOLooper {
		DigitalOutput dout;
		
		protected void setup() throws ConnectionLostException
				, InterruptedException {
			dout = ioio_.openDigitalOutput(0, false);
			
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext()
							, "Connected!!", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		public void loop() throws ConnectionLostException
				, InterruptedException {
			dout.write(!toggleButton1.isChecked());
			Thread.sleep(50);
		}
		
		public void disconnected() {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext()
							, "Disconnect!!", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		public void incompatible() {
			
		}
		
	}
	
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}

