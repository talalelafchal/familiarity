package com.example.camerademo;

import java.io.IOException;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends FragmentActivity implements
		SurfaceHolder.Callback {

	Camera mCamera;
	MediaRecorder mRecorder;
	String strVidPath;
	SurfaceView svCameraPreview;
	SurfaceHolder sHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		svCameraPreview = (SurfaceView) findViewById(R.id.sv_cam_preview);
		sHolder = svCameraPreview.getHolder();
		sHolder.addCallback(this);
	}

	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.lock();
			mCamera.release();
			mCamera = null;
		}
	}

	public void releaseMediaPlayer() {
		if (mRecorder != null) {
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void startVideoRecord() {
		strVidPath = getExternalFilesDir(null)
				.getAbsolutePath() + "/" + "temp.mp4";
		Log.d("VideoPath", strVidPath);
		mCamera = CameraUtils.getCameraInstance();
		if (mCamera != null) {

			CamcorderProfile camProfile = CamcorderProfile
					.get(CamcorderProfile.QUALITY_480P);

			mRecorder = new MediaRecorder();
			mCamera.unlock();
			mRecorder.setCamera(mCamera);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
			mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			mRecorder.setProfile(camProfile);
			mRecorder.setOutputFile(strVidPath);
			mRecorder.setPreviewDisplay(sHolder.getSurface());
			try {
				mRecorder.prepare();
				mRecorder.start();
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		startVideoRecord();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaPlayer();
		releaseCamera();
	}
}
