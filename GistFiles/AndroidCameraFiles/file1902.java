import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Reader;
import com.google.zxing.Result;


import java.util.List;

public class MainActivity extends Activity implements SurfaceHolder.Callback, OnClickListener, AutoFocusCallback {

	private Context mContext;
	private SurfaceView mSurfaceView;
	private Camera mCamera;
	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = getApplicationContext();
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mSurfaceView.setOnClickListener(this);

		mDialog = new AlertDialog.Builder(MainActivity.this)
				.setCancelable(false)
				.setPositiveButton("Yes", null).create();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Camera Open
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		mCamera.setPreviewCallback(_previewCallback);

		Camera.Parameters parameters = mCamera.getParameters();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		} else {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}

		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		Camera.Size previewSize = previewSizes.get(0);
		parameters.setPreviewSize(previewSize.width, previewSize.height);

		mCamera.setParameters(parameters);
		mCamera.startPreview();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.setPreviewCallback(null);
		mCamera.release();
		mCamera = null;

	}

	@Override
	public void onClick(View v) {
		if (mCamera != null) {
			mCamera.autoFocus(this);
		}
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
	}

	private PreviewCallback _previewCallback = new PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

			Log.d("onPreviewFrame", "onPreviewFrame Called");

			if (mDialog.isShowing()) return;

			// Read Range
			Camera.Size size = camera.getParameters().getPreviewSize();

			// Create BinaryBitmap
			PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
					data, size.width, size.height, 0, 0, size.width, size.height, false);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			// Read QR Code
			Reader reader = new MultiFormatReader();
			Result result = null;
			try {
				result = reader.decode(bitmap);
				String text = result.getText();

				mDialog.setMessage(text);
				mDialog.show();

			} catch (NotFoundException e) {
			} catch (ChecksumException e) {
			} catch (FormatException e) {
			}
		}
	};

}