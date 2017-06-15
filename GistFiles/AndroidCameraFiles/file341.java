import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class HomeActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout frameLayout;

    
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume(){
        super.onResume();

        mCamera = getCameraInstance();

        mPreview = new CameraPreview(this, mCamera);
        frameLayout.addView(mPreview);
    }


    private void releaseCamera(){
        if (mCamera != null){
            mPreview.stopPreview();
            mCamera.lock();
            mCamera.release();
            frameLayout.removeAllViews();
            mCamera = null;
            mPreview = null;
        }
    }
}