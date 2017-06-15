package navin.tuts.opencvapp; // Change this to your package

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String TAG = "OpenCVExample::Main";

    private CameraBridgeViewBase mOpenCvCameraView; //The Cameraview we will use to get image input
    private Mat mGray; // The OpenCV Mat which will hold the grayscale image
    private Mat mGauss; // The OpenCV Mat which will hold the Gaussian image

    // Static loading of OpenCV
    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Don't show title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.camera);
      
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(1000, 800);
        
        // We want our application in landscape...
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // ... and fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }
    
    @Override
    public void onCameraViewStarted(int width, int height) {
       // Initialize the Mat objects when the Camera starts
        mGray = new Mat(height, width, CvType.CV_8UC4);
        mGauss = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mGauss.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
      // This is where the magic happens. When we get a frame from the camera, we do the following:
      //First, we convert it to grayscale and save it in mGray
      mGray = inputFrame.gray();
      // Then we perform a Gaussian blur on mGray and save it in the mGauss
      Imgproc.GaussianBlur(mGray, mGauss, new Size(), 5);
      // Return the Gaussian-ed Mat back to screen
      return mGauss;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // If OpenCV loaded then enable the CameraView
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {   super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
