package com.nativeandroid.flashlight;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

@SuppressWarnings("deprecation")
public class FlashControl {
	
	public final String Tag = "FlashControl";
	
	private Camera camera;
	
	public FlashControl () {
		camera = Camera.open();
	}
	
	public void release () {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}
	
	public void turnOn () {
		try {
			Parameters params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
		} catch (Exception e) {
			Log.d(Tag, e.getMessage());
		}
	}
	
	public void turnOff () {
		try {
			Parameters params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
		} catch (Exception e) {
			Log.d(Tag, e.getMessage());
		}
	}
}
