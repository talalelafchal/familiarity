package com.ozateck.opencv;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends Activity implements CvCameraViewListener2{

	private CameraBridgeViewBase mCameraView;
	private Mat mOutputFrame;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
		@Override
		public void onManagerConnected(int status){
			switch (status){
				case LoaderCallbackInterface.SUCCESS:
					mCameraView.enableView();
					break;
				default:
					super.onManagerConnected(status);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mCameraView = (CameraBridgeViewBase)findViewById(R.id.camera_view);
		mCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onResume(){
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}

	@Override
	public void onPause(){
		super.onPause();
		if (mCameraView != null){
			mCameraView.disableView();
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if (mCameraView != null){
			mCameraView.disableView();
		}
	}

	@Override
	public void onCameraViewStarted(int width, int height){
		mOutputFrame = new Mat(height, width, CvType.CV_8UC1);
	}

	@Override
	public void onCameraViewStopped(){
		if(mOutputFrame != null)mOutputFrame.release();
		mOutputFrame = null;
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame){

		// Cannyフィルタをかける
		Imgproc.Canny(inputFrame.gray(), mOutputFrame, 80, 100);
		// ビット反転
		Core.bitwise_not(mOutputFrame, mOutputFrame);
		return mOutputFrame;
	}
}
