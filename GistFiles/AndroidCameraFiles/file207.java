package cc.omora.android.brokencamera;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.OutOfMemoryError;
import java.lang.Runnable;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.util.Log;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class BrokenCamera extends Activity implements KeyEvent.Callback
{
        private static final String TAG = "BrokenCamera";

        Camera mCameraDevice;
        SurfaceView mFinder;
        ProgressDialog mLevelControl;

        Handler mHandler;
        Runnable mRunnable;

        int mWidth;
        int mHeight;
        int mLevel;
        long mLastLevelControlTimeMillis;

        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                if(mFinder == null) {
                        mFinder = new Finder(this);
                        setContentView(mFinder);
                }
                if(mFinder != null) {
                        OnClickListener listener = new OnFinderClickListener();
                        mFinder.setOnClickListener(listener);
                }
                mLevel = PreferenceManager.getDefaultSharedPreferences(this).getInt("Level", 50);
                if(mLevelControl == null) {
                        mLevelControl = new ProgressDialog(this) {
                                public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
                                        return true;
                                }
                                public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
                                        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                                                showGlitchLevelControl(keyCode);
                                        }
                                        return true;
                                }
                        };
                        mLevelControl.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mLevelControl.setTitle("Glitch Level");
                        mLevelControl.setProgress(mLevel);
                }
        }

        public boolean onKeyUp(int keyCode, KeyEvent event) {
                return true;
        }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        showGlitchLevelControl(keyCode);
                } else if(keyCode == KeyEvent.KEYCODE_BACK) {
                        if(mCameraDevice != null) {
                                mCameraDevice.setPreviewCallback(null);
                                mCameraDevice.stopPreview();
                                mCameraDevice.release();
                                mCameraDevice = null;
                        }
                        setResult(Activity.RESULT_CANCELED);
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                        editor.putInt("Level", mLevel);
                        editor.commit();
                        finish();
                }
                return true;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                setResult(resultCode);
                finish();
        }

        class OnFinderClickListener implements OnClickListener {
                public void onClick(View v) {
                        try {
                                mCameraDevice.autoFocus(mAutoFocusCallback);
                        } catch(NullPointerException e) {
                                Toast.makeText(BrokenCamera.this, "error", Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_CANCELED);
                                finish();
                        }
                }
        }

        public void showGlitchLevelControl(int keyCode) {
                if(keyCode == KeyEvent.KEYCODE_VOLUME_UP && mLevel < 100) {
                        mLevel++;
                } else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && mLevel > 0) {
                        mLevel--;
                }
                mLevelControl.setProgress(mLevel);
                if(mRunnable != null) {
                        mHandler.removeCallbacks(mRunnable);
                }
                if(!mLevelControl.isShowing()) {
                        mLevelControl.show();
                }
                mLastLevelControlTimeMillis = System.currentTimeMillis();
                mHandler = new Handler();
                mRunnable = new Runnable() {
                        public void run() {
                                if(System.currentTimeMillis() - mLastLevelControlTimeMillis > 1000 && mLevelControl.isShowing()) {
                                        mLevelControl.dismiss();
                                }
                        }
                };
                new Thread(new Runnable() {
                        public void run() {
                                try {
                                        Thread.sleep(1000);
                                } catch(InterruptedException e) {
                                }
                                mHandler.post(mRunnable);
                        }
                }).start();
        }

        class Finder extends SurfaceView implements SurfaceHolder.Callback {
                Finder(Context context) {
                        super(context);
                        try {
                                SurfaceHolder holder = getHolder();
                                holder.addCallback(this);
                                holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
                        } catch(NullPointerException e) {
                                Toast.makeText(BrokenCamera.this, "error", Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_CANCELED);
                                finish();
                        }
                }
                public void surfaceCreated(SurfaceHolder holder) {
                        try {
                                mCameraDevice = Camera.open();
                                mCameraDevice.setPreviewCallback(mPreviewCallback);
                        } catch(NullPointerException e) {
                                Toast.makeText(BrokenCamera.this, "error", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_CANCELED);
                                finish();
                        } catch(RuntimeException e) {
                                Toast.makeText(BrokenCamera.this, "error", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_CANCELED);
                                finish();
                        }
                }
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                        mWidth = width;
                        mHeight = height;
                        if(mCameraDevice != null) {
                                mCameraDevice.stopPreview();
                                Camera.Parameters parameters = mCameraDevice.getParameters();
                                mCameraDevice.startPreview();
                        }
                }
                public void surfaceDestroyed(SurfaceHolder holder) {
                        if(mCameraDevice != null) {
                                mCameraDevice.setPreviewCallback(null);
                                mCameraDevice.stopPreview();
                                mCameraDevice.release();
                                mCameraDevice = null;
                        }
                }
        }

        PreviewCallback mPreviewCallback = new PreviewCallback() {
                public void onPreviewFrame(byte [] data, final Camera camera) {
                        Camera.Parameters parameters = camera.getParameters();

                        Size size = parameters.getPreviewSize();
                        int [] colors = new int[size.width*size.height];
                        decodeYUV420SP(colors, data, size.width, size.height);
                        Bitmap bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(colors, 0, size.width, 0, 0, size.width, size.height);

                        SurfaceHolder holder = mFinder.getHolder();
                        Canvas canvas = holder.lockCanvas();
                        Rect rectDst = new Rect(0, 0, mWidth, mHeight);
                        canvas.drawBitmap(bitmap, null, rectDst, null);
                        holder.unlockCanvasAndPost(canvas);
                }
                // http://code.google.com/p/android/issues/detail?id=823
                public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
                        final int frameSize = width * height;
                        
                        for (int j = 0, yp = 0; j < height; j++) {
                                int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
                                for (int i = 0; i < width; i++, yp++) {
                                        int y = (0xff & ((int) yuv420sp[yp])) - 16;
                                        if (y < 0) y = 0;
                                        if ((i & 1) == 0) {
                                                v = (0xff & yuv420sp[uvp++]) - 128;
                                                u = (0xff & yuv420sp[uvp++]) - 128;
                                        }
                                        
                                        int y1192 = 1192 * y;
                                        int r = (y1192 + 1634 * v);
                                        int g = (y1192 - 833 * v - 400 * u);
                                        int b = (y1192 + 2066 * u);
                                        
                                        if (r < 0) r = 0; else if (r > 262143) r = 262143;
                                        if (g < 0) g = 0; else if (g > 262143) g = 262143;
                                        if (b < 0) b = 0; else if (b > 262143) b = 262143;
                                        
                                        rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                                }
                        }
                }
        };

        AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {
                public void onAutoFocus(boolean success, final Camera camera) {
                        camera.takePicture(mShutterCallback, mRawPictureCallback, mJpgPictureCallback);
                }
        };

        PictureCallback mJpgPictureCallback = new PictureCallback() {
                public void onPictureTaken(byte [] data, final Camera camera) {
                        SurfaceHolder holder = mFinder.getHolder();
                        Canvas canvas = holder.lockCanvas();
                        for(int i = 0; i < data.length; i++) {
                                if(data[i] == 48 && new Random().nextInt((100 - mLevel)*5) == 0) {
                                        data[i] = (byte) (new Random().nextInt(10) + 47);
                                }
                        }
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        Rect rectDst = new Rect(0, 0, mWidth, mHeight);
                        canvas.drawBitmap(bitmap, null, rectDst, null);

                        String file_name = String.valueOf(System.currentTimeMillis()) + ".jpg";
                        String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, file_name, null);

                        holder.unlockCanvasAndPost(canvas);
                        camera.startPreview();
                }
        };

        PictureCallback mRawPictureCallback = new PictureCallback() {
                public void onPictureTaken(byte [] data, Camera camera) {
                }
        };

        ShutterCallback mShutterCallback = new ShutterCallback() {
                public void onShutter() {
                }
        };

        ErrorCallback mErrorCallback = new ErrorCallback() {
                public void onError(int error, android.hardware.Camera camera) {
                        if (error == Camera.CAMERA_ERROR_SERVER_DIED) {
                                //mMediaServerDied = true;
                        }
                }
        };
}