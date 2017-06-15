package gclue.com.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

class Preview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "MyCamera";
    private Camera mCamera;

    Preview(Context context) {
        super(context);

        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout l:"+l+"t:"+t+"r:"+r+"b:"+b);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");

        mCamera = Camera.open();


        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e){ }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged format:"+format+"width:"+width+"height:"+height);

        Camera.Parameters mParams = mCamera.getParameters();
        mCamera.setParameters(mParams);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
    }
}