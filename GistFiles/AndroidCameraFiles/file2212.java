/*

BSD License: Copyright (c) 2012 Ted Hagos
All rights reserved.

License text: http://thelogbox.com/source-docs/software-license.txt

Created on November 20, 2012 by Ted Hagos

*/

package com.thelogbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.widget.LinearLayout;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.PictureCallback;
import android.view.SurfaceView;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import android.os.Handler;


class VSimpleCamera extends LinearLayout implements OnClickListener {

	private String TAG = null;
	private SurfaceView surfaceview = null;
	private Camera camera = null;
	private FileOutputStream out = null;

	public VSimpleCamera(Context ctx){
		super(ctx);
		
		TAG = getClass().getName();
		surfaceview = new SurfaceView(ctx);
		camera = Camera.open();

		setOnClickListener(this);


		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			public void run() {
				Log.v(TAG, "Runnable is running");

				//You can put the camera hooba jub here
				
			}
		}, 3000);

	}

	public void onClick(View v){

		boolean cameratest = (camera == null);
		Log.v(TAG, "Clicked");
	
		try {
			camera.setPreviewDisplay(surfaceview.getHolder());
			camera.startPreview();
			camera.takePicture(shutterCallBack, rawCallBack, jpgCallBack);
		}
		catch(Exception e){
			Log.v(TAG, "Exception " + e.toString());
		}
	}

	ShutterCallback shutterCallBack = new ShutterCallback() {
		public void onShutter() {
			Log.v(TAG, "shutter call");
		}
	};

	PictureCallback rawCallBack = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera cam){
			Log.v(TAG, "raw Call back");
		}
	};

	PictureCallback jpgCallBack = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera cam){

				try {
					out = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
					out.write(data);
				}
				catch(FileNotFoundException fe) {
					Log.v(TAG, fe.getMessage());
				}
				catch(IOException ioe) {
					Log.v(TAG,ioe.getMessage());
				}

			Log.v(TAG, "jpg Call Back");
		}
	};

}

public class SimpleCamera extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new VSimpleCamera(this));
    }
}
