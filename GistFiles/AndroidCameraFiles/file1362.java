package com.gmrmarketing.asdf;

import android.hardware.Camera;

public class CameraPlugin {
	Camera camera;
	int refid;
	
	CameraPlugin(int refid){
		this.refid = refid;
	}
		
	void setCamera(Camera camera){
		this.camera = camera;
	}
	
	void takePhoto(){
		camera.takePicture(null,null,null,new Camera.PictureCallback(){
			public void onPictureTaken(byte[] data,Camera camera){
				onPhotoCb(data);
			}
		});
	}
	
	public native void onPhotoCb(byte[] data);
}