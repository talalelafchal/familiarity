package gclue.com.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MyActivity extends Activity {

    private static final String TAG = "MyCamera";
    private static Camera mCamera;
    private static Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreview = new Preview(this);
        setContentView(mPreview);
    }

    protected void onResume() {
        super.onResume();

        // バックカメラIDを取得する.
        int cameraId = getCameraId();

        // カメラのインスタンスを取得する.
        mCamera = getCameraInstance(cameraId);

        Log.d(TAG,"mCamera:"+mCamera);
    }

    /**
     * カメラIDを取得する.
     * @return int カメラID.
     */
    public int getCameraId() {
        int cameraId = -1;
        // 仕様可能なカメラ数.
        int numberOfCameras = Camera.getNumberOfCameras();
        Log.d(TAG,"numberOfCameras:"+numberOfCameras);

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK || numberOfCameras == 1) {
                Log.d(TAG,"CAMERA_FACING_BACK:"+i);
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    /**
     * カメラのインスタンスを取得.
     * @return Camera カメラのインスタンス.
     */
    public static Camera getCameraInstance(int id){

        Camera c = null;
        try {
            c = Camera.open(id);
        }
        catch (Exception e){
            Log.d(TAG, "Error:"+e);
        }

        return c;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

