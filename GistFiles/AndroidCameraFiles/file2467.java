import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jp.tapestry.R;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{
	private SurfaceHolder mSurfaceHolder;
	private SurfaceView mSurfaceView;
	private Camera mCamera;
	private Camera.ShutterCallback mShutterListener = new Camera.ShutterCallback() {public void onShutter() {}};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mSurfaceView = (SurfaceView) findViewById(R.id.camera_activity_surface_view);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);

		mCamera = getCameraInstance();
		mSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {autoFocus();}
		});
		findViewById(R.id.camera_activity_shutter).setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// mShutterListenerをnullに変えると音がならない
				mCamera.takePicture(mShutterListener, null, new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						// Bitmapデータの作成
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
						String saveDir = Environment.getExternalStorageDirectory().getPath() + "/" + getPackageName();
						File file = new File(saveDir);
						if (!file.exists()) {if (!file.mkdir()) {}}
						// 画像保存パス
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmss");
						String imgPath = saveDir + "/" + sf.format(cal.getTime()) + "_" + getPackageName() + ".jpg";
						// ファイル保存
						FileOutputStream fos;
						try {
							fos = new FileOutputStream(imgPath, true);
							fos.write(data);
							fos.close();
							registAndroidDB(imgPath);
						} catch (Exception e) {}
						fos = null;
					}
				});
			}
		});
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, int format, final int width,final int height) {
		Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				mSurfaceHolder = holder;
				if (mCamera == null) {
					mCamera = getCameraInstance();
				}
				Camera.Parameters parameters = mCamera.getParameters();
				List<Size> previewSizes = parameters.getSupportedPreviewSizes();
				Camera.Size previewSize = previewSizes.get(0);
				parameters.setPreviewSize(previewSize.width, previewSize.height);

				List<Size> pictureSizes = parameters.getSupportedPictureSizes();
				Camera.Size pictureSize = pictureSizes.get(0);
				parameters.setPictureSize(pictureSize.width, pictureSize.height);
				mCamera.setParameters(parameters);
				mCamera.startPreview();
				try {
					mCamera.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//  カメラ解放
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	// カメラ取得
	private Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(0);
			c.setDisplayOrientation(90);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(CameraActivity.this.getApplicationContext(), "カメラ起動に失敗しました。\n端末を再起動してください", Toast.LENGTH_SHORT).show();
			finish();
		}
		return c;
	}
	// オートフォーカス
	private void autoFocus() {
		boolean hasAutoFocus = false;
		PackageManager packageManager = getApplicationContext().getPackageManager();
		hasAutoFocus = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
		if (hasAutoFocus) {
			try{
				mCamera.cancelAutoFocus();
				mCamera.autoFocus(new AutoFocusCallback() {
					public void onAutoFocus(boolean success, Camera camera) {}});
			}catch(Exception e){}
		}
	}
	// 画像保存
	private void registAndroidDB(String path) {
		ContentValues values = new ContentValues();
		ContentResolver contentResolver = CameraActivity.this.getContentResolver();
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put("_data", path);
		contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}
}
