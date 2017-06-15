import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cmm.cosign.R;
import com.cmm.cosign.custom.CameraPreview;
import com.cmm.cosign.src.Config;
import com.cmm.cosign.src.DiskLruImageCache;

public class CameraViewFragment extends SherlockFragment {

	public static final int PICK_IMAGE = 0;
	private static final String TAG = "CameraViewFragment";
	private boolean cameraBack, cameraFront, flashLED, autoFocus, manualFocus;
	private int backCamera, frontCamera, activeCamera, degrees;
	private Orientation orientation;
	private Flash activeFlashState;

	private Camera camera;
	private Display display;
	private Parameters parameters;
	private CameraPreview cameraPreview;
	private FrameLayout frameLayout;
	private ImageButton imageButtonFlash, imageButtonCameraFlip;
	private enum Orientation {
		LANDSCAPE, PORTRAIT
	}
	private enum Flash {
		AUTO, ON, OFF
	}

	private DiskLruImageCache cache;

	private PictureCallback pictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(final byte[] data, final Camera camera) {
			File file = new File(Config.PATH_IMAGES);
			file.mkdirs();

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					new AlertDialog.Builder(getActivity())
					.setMessage("Use this Image?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//largeHeap = true in Manifest to prevent OOM exception
							Bitmap bitmap = null, rBitmap = null;
							System.gc();
							try {
								Matrix matrix = new Matrix();
								matrix.postRotate(90 - degrees);
								bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
								rBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
								cache.put("tempimage", rBitmap);
							} catch (OutOfMemoryError e) {
								Log.e(TAG, "Out of Memory!!");
							} catch (Exception e) {
								Log.e(TAG, "Error accessing file: " + e.getMessage(), e);
							} finally {
								if(bitmap != null)
									bitmap.recycle();
								if(rBitmap != null)
									rBitmap.recycle();
								bitmap = rBitmap = null;
								System.gc();
							}

							Intent data = new Intent();
							getActivity().setResult(Activity.RESULT_OK, data);
							getActivity().finish();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							camera.startPreview();
						}
					})
					.show();
				}
			}, 2000);
		}
	};

	private final ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			mgr.playSoundEffect(AudioManager.STREAM_SYSTEM);
		}
	};

	private final AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			Log.d(TAG, "focused " + success);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cache = new DiskLruImageCache(getActivity(), Config.CACHE_TEMP, Config.CACHE_SIZE);
		if(!isCameraPresent()) {
			Toast.makeText(getActivity(), "No Camera Found", Toast.LENGTH_SHORT).show();
			getActivity().finish();
		} else {
			getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			display = getActivity().getWindowManager().getDefaultDisplay();

			if(display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180) 
				orientation = Orientation.PORTRAIT;
			else if(display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270)
				orientation = Orientation.LANDSCAPE;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		if(orientation == Orientation.PORTRAIT) 
			view = inflater.inflate(R.layout.camera_view_portrait, null);
		else
			view = inflater.inflate(R.layout.camera_view_landscape, null);

		frameLayout = (FrameLayout) view.findViewById(R.id.frameLayoutCamera);
		imageButtonFlash = (ImageButton) view.findViewById(R.id.imageButtonFlash);
		imageButtonCameraFlip = (ImageButton) view.findViewById(R.id.imageButtonCameraFlip);

		if(cameraBack) {
			camera = Camera.open(backCamera);
			cameraPreview = new CameraPreview(getActivity(), camera);
			frameLayout.removeAllViews();
			frameLayout.addView(cameraPreview);
			activeCamera = backCamera;
		} else if (cameraFront) {
			camera = Camera.open(frontCamera);
			cameraPreview = new CameraPreview(getActivity(), camera);
			frameLayout.removeAllViews();
			frameLayout.addView(cameraPreview);
			activeCamera = frontCamera;
		}
		if(camera != null) {
			parameters = camera.getParameters();

			setCameraDisplayOrientation(getActivity(), activeCamera, camera);

			if(flashLED && activeCamera == backCamera) {
				imageButtonFlash.setEnabled(true);
				parameters = camera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				camera.setParameters(parameters);
				imageButtonFlash.setImageResource(R.drawable.ic_action_flash_auto);
			} else {
				imageButtonFlash.setEnabled(false);
			}
			if(cameraBack && cameraFront)
				imageButtonCameraFlip.setEnabled(true);
			else
				imageButtonCameraFlip.setEnabled(false);

			view.findViewById(R.id.imageButtonCancel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().finish();
				}
			});
		}
		imageButtonCameraFlip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activeCamera == backCamera && cameraFront) {
					camera.release();
					camera = Camera.open(frontCamera);
					cameraPreview = new CameraPreview(getActivity(), camera);
					frameLayout.removeAllViews();
					frameLayout.addView(cameraPreview);
					activeCamera = frontCamera;
					if(flashLED) {
						imageButtonFlash.setEnabled(false);
						parameters = camera.getParameters();
						parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
						camera.setParameters(parameters);
						imageButtonFlash.setImageResource(R.drawable.ic_action_flash_off);
					}				
				} else if(activeCamera == frontCamera && cameraBack) {
					camera.release();
					camera = Camera.open(backCamera);
					cameraPreview = new CameraPreview(getActivity(), camera);
					frameLayout.removeAllViews();
					frameLayout.addView(cameraPreview);
					activeCamera = backCamera;
					imageButtonFlash.setEnabled(true);

					if(flashLED) {
						imageButtonFlash.setEnabled(true);
						parameters = camera.getParameters();
						parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
						camera.setParameters(parameters);
						imageButtonFlash.setImageResource(R.drawable.ic_action_flash_auto);
					}
				}
				setCameraDisplayOrientation(getActivity(), activeCamera, camera);
				camera.startPreview();
			}
		});

		imageButtonFlash.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flashLED && activeFlashState == Flash.AUTO && activeCamera == backCamera) {
					activeFlashState = Flash.ON;
					parameters = camera.getParameters();
					parameters.setFlashMode(Parameters.FLASH_MODE_ON);
					camera.setParameters(parameters);
					camera.startPreview();
					imageButtonFlash.setImageResource(R.drawable.ic_action_flash_on);
				} else if (flashLED && activeFlashState == Flash.ON && activeCamera == backCamera) {
					activeFlashState = Flash.OFF;
					parameters = camera.getParameters();
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(parameters);
					camera.startPreview();
					imageButtonFlash.setImageResource(R.drawable.ic_action_flash_off);
				} else if (flashLED && activeFlashState == Flash.OFF && activeCamera == backCamera) {
					activeFlashState = Flash.AUTO;
					parameters = camera.getParameters();
					parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
					camera.setParameters(parameters);
					camera.startPreview();
					imageButtonFlash.setImageResource(R.drawable.ic_action_flash_auto);
				}
			}
		});

		frameLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Autofocus: " + autoFocus);
				if (autoFocus) {
					if (parameters.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_AUTO)) {
						parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
						camera.setParameters(parameters);
					}
					manualFocus = true;
					camera.autoFocus(autoFocusCallback);
				}
			}
		});

		view.findViewById(R.id.imageButtonCapture).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parameters = camera.getParameters();

				if(!manualFocus) {
					/* Used for Auto Focus, simple and efficient way since API 9. 
					 * No need of Callback
					 * Focuses as soon as parameter is set. So parameter should be set while capturing Picture.
					 */
					if (parameters.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
						parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
						camera.setParameters(parameters);
					}

					if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
						if (parameters.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
							parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
							camera.setParameters(parameters);
						}
				}
				parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width, parameters.getSupportedPictureSizes().get(0).height);
				camera.setParameters(parameters);

				camera.takePicture(shutterCallback, null, pictureCallback);
				manualFocus = false;
			}
		});

		view.findViewById(R.id.imageButtonOpenGallery).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent imagePickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(imagePickerIntent, PICK_IMAGE);
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onresume");
		super.onResume();
		try {
			camera = Camera.open(activeCamera);
			cameraPreview = new CameraPreview(getActivity(), camera);
			frameLayout.removeAllViews();
			frameLayout.addView(cameraPreview);
			setCameraDisplayOrientation(getActivity(), activeCamera, camera);
			if(activeCamera == frontCamera)
				imageButtonFlash.setEnabled(false);
		} catch (Exception e) {
			Log.d(TAG, "Camera already Active.");
		}
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if(savedInstanceState != null) {
			try {
				camera.release();
			} catch (Exception e) {
				Log.d(TAG, "Camera already Released.");
			}
			activeCamera = savedInstanceState.getInt("cameraID");
			camera = Camera.open(activeCamera);
			cameraPreview = new CameraPreview(getActivity(), camera);
			frameLayout.removeAllViews();
			frameLayout.addView(cameraPreview);
			setCameraDisplayOrientation(getActivity(), activeCamera, camera);
			if(activeCamera == frontCamera)
				imageButtonFlash.setEnabled(false);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			camera.release();
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		outState.putInt("cameraID", activeCamera);
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onpause");
		super.onPause();
		try {
			camera.release();
		} catch (Exception e) {
			Log.d(TAG, "Camera already Released.");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == SherlockFragmentActivity.RESULT_OK) {
			switch (requestCode) {
			case PICK_IMAGE:
				String [] proj={MediaStore.Images.Media.DATA};
				Cursor cursor = getActivity().getContentResolver().query(data.getData(), proj,  null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				data = new Intent();
				data.putExtra(Config.REQUEST_IMAGE_PATH, cursor.getString(column_index));
				cursor.close();

				getActivity().setResult(Activity.RESULT_OK, data);
				getActivity().finish();
				break;
			default:
				break;
			}
		}
	}

	public void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0: degrees = 0; break;
		case Surface.ROTATION_90: degrees = 90; break;
		case Surface.ROTATION_180: degrees = 180; break;
		case Surface.ROTATION_270: degrees = 270; break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	private boolean isCameraPresent() {
		activeFlashState = Flash.AUTO;

		if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS))
			autoFocus = true;
		else
			autoFocus = false;

		manualFocus = false;

		if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
			flashLED = true;
		else
			flashLED = false;

		if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) || Camera.getNumberOfCameras() > 0){
			int numberOfCameras = Camera.getNumberOfCameras();
			for (int i = 0; i < numberOfCameras; i++) {
				CameraInfo info = new CameraInfo();
				Camera.getCameraInfo(i, info);
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					cameraFront = true;
					frontCamera = i;
				}
				if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
					cameraBack = true;
					backCamera = i;
				}
			}
			return true;
		} else {
			return false;
		}
	}

}