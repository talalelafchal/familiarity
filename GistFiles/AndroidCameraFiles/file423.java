package com.example.transmitimage;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	
	SurfaceHolder		mHolder;
	Camera			mCamera 	= null;
	Camera.Parameters		parameters;
	Bitmap 			bitmap;
	TCP_Client		client   = null;
	
	
	//	Message
	public	final static int   MSG_ASYNCTASK_CLOSE	=	1;
	public	static boolean	 CheckOfLoop               =	false;
	
	public CameraPreview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		mHolder		=	getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// TODO Auto-generated method stub
		parameters = mCamera.getParameters();
		
		
		parameters.setPictureSize(320,240);
		parameters.setPreviewSize(320,240);
		parameters.setRotation(270);
		
		mCamera.setParameters(parameters);
		mCamera.startPreview();
	
	}
	
	//	Surfaceview가 생성될때 호출되는 callback
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	
		try{
			mCamera	=	Camera.open();
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setPreviewCallback(new CameraCallBack());
		
		}
		catch(IOException e){
			mCamera.release();
			mCamera	=	null;
		}
	}
	
	//	surfaceview가 소멸될때 호출되는 callback
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera	=	null;
	}
	
	
	
	//	프리뷰될때마다 콜백!!
	public class CameraCallBack implements Camera.PreviewCallback
	{
				
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub

			if( CheckOfLoop ==	false)
			{
				//asynctask가 닫혓을경우.
				Log.d("TCP","new thread");				

			   client	=	 new TCP_Client();
			   client.execute(data);
			   
			   CheckOfLoop	=	true;
			}
			
			
	}	
	static	public class MyHandler extends	Handler
	{
		public void handleMessage(Message Msg)
		{
			switch(Msg.what)
			{
				case	MSG_ASYNCTASK_CLOSE:
						CheckOfLoop	=	false;
                                    break;
			
			}
		}
	}
		

}
