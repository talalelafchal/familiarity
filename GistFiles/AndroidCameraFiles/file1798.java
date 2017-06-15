package com.mmg.mlive.media;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public class CameraUtil {

	public CameraUtil getInstance() {
		return null;
	}
	
	public void open() {
		
	}
	
	public void setPreviewDisplay(SurfaceHolder holder) {
		
	}
	
	public void startPreview() {
		
	}
	
	public void stopPreview() {
		
	}
	
	/**
	 * change camera preview size, if camera support width x height
	 * then set preview size to that, and return null. If the
	 * requested size if not supported, set it to the closest
	 * supported one, and return the size.
	 * @param width
	 * @param height
	 * @return
	 */
	public Camera.Size changePreviewSize(int width, int height) {
		return null;
	}
	
	public void setPreviewCallback(Camera.PreviewCallback cb) {
		
	}
}
