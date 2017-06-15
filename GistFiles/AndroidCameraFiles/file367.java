package com.paloghas.cameracomponent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.unity3d.player.UnityPlayerActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Bundle; 
import android.util.Log;

public class AndroidNativeCam extends UnityPlayerActivity implements
		SurfaceTexture.OnFrameAvailableListener {

	private static final String LOG_TAG = AndroidNativeCam.class
			.getSimpleName();

	public static Context mContext;

	private Camera mCamera;
	private SurfaceTexture texture;

	// unity texture
	private int nativeTexturePointer = -1;

	private int prevHeight;
	private int prevWidth;

//  private ByteBuffer mPixelBuf;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = this;
		Log.d(LOG_TAG, "now mContext=" + mContext);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		mCamera.stopPreview();
		mCamera.release();
	}

	/*
	 * JAVA texture creation
	 */
	public int startCamera() {
		// create the texture
		nativeTexturePointer = createExternalTexture();
		texture = new SurfaceTexture(nativeTexturePointer);
		texture.setOnFrameAvailableListener(this);

    // open the camera
		mCamera = Camera.open(); 
		setupCamera();

		Log.d(LOG_TAG, "camera opened: " + (mCamera != null));

		try {
			mCamera.setPreviewTexture(texture); 
			mCamera.startPreview();

		} catch (IOException ioe) {
			Log.w("MainActivity", "CAM LAUNCH FAILED");
		}
		
		Log.d(LOG_TAG, "nativeTexturePointer="+nativeTexturePointer);
		return nativeTexturePointer;  
	}

	@SuppressLint("NewApi")
	private void setupCamera() {
		Camera.Parameters parms = mCamera.getParameters();

		// Give the camera a hint that we're recording video. This can have a
		// big impact on frame rate.
		parms.setRecordingHint(true);
		parms.setPreviewFormat(20);

		// leave the frame rate set to default
		mCamera.setParameters(parms);

		Camera.Size mCameraPreviewSize = parms.getPreviewSize();
		prevWidth = parms.getPreviewSize().width;
		prevHeight = parms.getPreviewSize().height;
		
//		mPixelBuf = ByteBuffer.allocateDirect(prevWidth * prevHeight * 4);
//		mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);

		// only for debugging output
		int[] fpsRange = new int[2];
		parms.getPreviewFpsRange(fpsRange); 
		String previewFacts = mCameraPreviewSize.width + "x"
				+ mCameraPreviewSize.height;
		if (fpsRange[0] == fpsRange[1]) {
			previewFacts += " @" + (fpsRange[0] / 1000.0) + "fps";
		} else {
			previewFacts += " @[" + (fpsRange[0] / 1000.0) + " - "
					+ (fpsRange[1] / 1000.0) + "] fps";
		}

//		previewFacts += ", supported Preview Formats: ";
//		List<Integer> formats = parms.getSupportedPreviewFormats();
//		for (int i = 0; i < formats.size(); i++) {
//			previewFacts += formats.get(i).toString() + " ";
//		}
//		Integer format = parms.getPreviewFormat();
//		previewFacts += ", Preview Format: ";
//		previewFacts += format.toString();

		Log.i(LOG_TAG, "previewFacts=" + previewFacts);
		
		checkGlError("endSetupCamera");
	}

	public void updateTexture() {
	  // check for errors at the beginning
		checkGlError("begin_updateTexture()");

		Log.d(LOG_TAG, "GLES20.glActiveTexture.."); 
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		checkGlError("glActiveTexture");
		Log.d(LOG_TAG, "GLES20.glBindTexture..");
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				nativeTexturePointer);
		checkGlError("glBindTexture");

		Log.d(LOG_TAG,"ThreadID="+Thread.currentThread().getId());
		Log.d(LOG_TAG, "texture.updateTexImage..");

		texture.updateTexImage();
		checkGlError("updateTexImage");

//		mPixelBuf.rewind();
//		Log.d(LOG_TAG, "GLES20.glReadPixels..");
//		GLES20.glReadPixels(0, 0, prevWidth, prevHeight, GLES20.GL_RGBA,
//				GLES20.GL_UNSIGNED_SHORT_4_4_4_4, mPixelBuf);
//		checkGlError("glReadPixels");

//		Log.d(LOG_TAG, "mPixelBuf.get(0)=" + mPixelBuf.get(0));
	}

	public int getPreviewSizeWidth() {
		return prevWidth;
	}

	public int getPreviewSizeHeight() {

		return prevHeight;
	}

	@Override
	public void onFrameAvailable(SurfaceTexture arg0) {

		Log.d(LOG_TAG, "onFrameAvailable");
	}

	// create texture here instead by Unity
	private int createExternalTexture() {
		int[] textureIdContainer = new int[1];
		GLES20.glGenTextures(1, textureIdContainer, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				textureIdContainer[0]);

		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		return textureIdContainer[0];
	}

  // check for OpenGL errors
	private void checkGlError(String op) {
	    int error;
	    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
	        Log.e(LOG_TAG, op + ": glError 0x" + Integer.toHexString(error));
	    }
	}

}