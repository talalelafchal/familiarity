package android.app;
class Activity{
	protected void onCreate(Bundle state);
	protected void onStart();
	protected void onStop();
	protected void onDestroy();
	public Boolean onCreateOptionsMenu(Menu menu);
	public Boolean onOptionsMenuItemSelected(MenuItem item);
}

class CameraDemo extends Activity {
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