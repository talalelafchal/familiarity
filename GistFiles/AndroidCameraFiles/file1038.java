package com.space150.android.glass.opencvfacedetection;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

public class JView extends JavaCameraView {

	public JView(Context context, int cameraId) {
		super(context, cameraId);
	}

	public JView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

	@Override
	protected boolean initializeCamera(int width, int height) 
	{
		Log.i("JVIEW", "initialize Camera");
		super.initializeCamera(width, height);

		Camera.Parameters params = mCamera.getParameters();

		// Post XE10 Hotfix
		params.setPreviewFpsRange(60000,60000);
		params.setPreviewSize(320,240);
		mCamera.setParameters(params);

		return true;
	}

}
