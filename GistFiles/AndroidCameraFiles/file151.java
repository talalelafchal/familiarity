class CameraDemo {
	Button buttonClick;
	ShutterCallback shutterCallback;
	PictrueCallback rawCallback;
	PictrueCallback jpegCallback;

	protected void onCreate(Bundle SavedInstance);
	protected void onStart();
	protected void onStop();
	protected void onDestroy();
	public Boolean onCreateOptionsMenu(Menu menu);

}


package android.hardware;
class Camera{
	public Camera open(int CameraId);
	public Parameters getParameters();
	public void setParameters(Parameters params);
	final public void setPreviewDisplay(SurfaceHolder holder);
	final public void startPreview();
	final public void stopPreview();
	final public void release();
	final public void takePucture(ShutterCallback shutter, PictrueCallback raw, PictrueCallback postView, PictrueCallback jpeg);
}