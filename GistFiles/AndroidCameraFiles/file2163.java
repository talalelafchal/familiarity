package net.dhruvpatel.examplefrontcamera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private Camera.PictureCallback mPicture;
    private CameraManager cameraManager;
    private CameraPreview mPreview;
    private Context myContext;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;
        cameraManager = new CameraManager(this);

        frameLayout= (FrameLayout) findViewById(R.id.camera_preview);
        int camerasNumber = Camera.getNumberOfCameras();
        if (camerasNumber > 1) {
            //release the old camera instance
            //switch camera, from the front and the back and vice versa

            releaseCamera();
            chooseCamera();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        Button captureBtn = (Button) findViewById(R.id.btn_capture);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    public void onResume(){

        super.onResume();
        if (!cameraManager.hasCamera()) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (CameraManager.findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
            }
            try {
                mCamera = Camera.open(CameraManager.findFrontFacingCamera());
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
            catch (Exception e){
                Log.e("camera", e.getMessage());
            }
        }
    }

    public void initialize() {
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        frameLayout.addView(mPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();           // release the camera immediately on pause event
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseCamera();
        finish();

    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public void chooseCamera() {
        int cameraId = CameraManager.findFrontFacingCamera();
        if (cameraId >= 0) {
            //open the backFacingCamera
            //set a picture callback
            //refresh the preview
            try {
                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
            catch(Exception e){
                Log.e("Camera",e.getMessage());
            }
        }
    }


    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = generateMediaFile();

                try {
                    ImageProcessing imageProcessing = new ImageProcessing(myContext);
                    imageProcessing.createImage(pictureFile, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    public File generateMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Selfie Flashlight");
        if(!mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }
}
