package com.riot.projetoriotboothrfid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.hardware.Camera;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CameraActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private TextView countTxt;
    private int fotos = 3;
    private int delay = 5000; // delay for 5 sec.
    private int period = 1000; // repeat every sec.
    private String[] mFotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        try {
            getCamera();
        } catch (InterruptedException e) {
            Log.d("APPLog","Erro na Camera");
        }
    }

    public void getCamera() throws InterruptedException {
        if (mCamera == null){
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
            frameLayout.addView(mPreview);
            RelativeLayout relativeLayoutControls = (RelativeLayout) findViewById(R.id.controls_layout);
            relativeLayoutControls.bringToFront();
            /*relativeLayoutControls.addView(imageView1);*/
            animateText();
        }
    }

    private int mIndex=3;
    private long mDelay = 1500;
    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            TextView textView;
            textView = (TextView) findViewById(R.id.contagemText);
            if(mIndex >=0) {
                textView.setText(String.valueOf(mIndex--));
                textView.setVisibility(View.VISIBLE);
                mHandler.postDelayed(characterAdder, mDelay);
            }else{
                textView.setText("Xiiisss!!");
                //textView.setVisibility(View.INVISIBLE);
                mHandler.removeCallbacks(characterAdder);
                logSensorData();
            }
        }
    };

    public void animateText() {
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }


    public void logSensorData()
    {
        mIndex=3;
        mCamera.stopPreview();
        mCamera.takePicture(null,null,photoCallback);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


    Camera.PictureCallback photoCallback= new Camera.PictureCallback() {
        public void onPictureTaken(byte[] imageData, Camera camera) {
            MySingleton singleton = MySingleton.getInstance();
            if (imageData != null) {
                String encodedImage = Base64.encodeToString(imageData, Base64.DEFAULT);
                //mFotos[fotos] = encodedImage;
                mCamera.startPreview();
                fotos--;
                if(fotos>0){
                    animateText();
                }else{
                    mCamera.release();
                    Intent volta = new Intent(CameraActivity.this,Instruction.class);
                    startActivity(volta);
                }
            }
        }
    };

}