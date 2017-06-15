package com.tao.camera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends Activity {

	private Camera mCamera;
	private String pictureDir = Environment.getExternalStorageDirectory() + "/campics/";
	
	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String filename = UUID.randomUUID().toString() + ".jpg";
			
			File dir = new File(pictureDir);
			if (!dir.exists()) dir.mkdir(); 
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(data),null,null);
				
				//rotate bitmap
				Matrix matrix = new Matrix();
				matrix.setRotate(90);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				
				//compress bitmap to ByteArrayOutputStream as jpg format
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
				
				//write to file
				FileOutputStream os = new FileOutputStream(pictureDir + filename);
				os.write(bos.toByteArray());
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCamera = Camera.open();
		Parameters params = mCamera.getParameters();
		params.setPictureSize(800, 480);
		mCamera.setParameters(params);
		mCamera.startPreview();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		mCamera.takePicture(null, null, mJpegCallback);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mCamera.stopPreview();
		mCamera.release();
	}
}
