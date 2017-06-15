package com.inex.ioioapp;

import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;

public class MainActivity extends IOIOActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);

	}
	
	class Looper extends BaseIOIOLooper {
		
		protected void setup() throws ConnectionLostException
				, InterruptedException {
			
		}
		
		public void loop() throws ConnectionLostException
				, InterruptedException {
	
		}
		
		public void disconnected() {
			
		}
		
		public void compatible() {
			
		}
		
	}
	
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}
