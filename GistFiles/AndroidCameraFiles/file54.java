import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.hardware.Camera.PreviewCallback;

import javax.xml.transform.Result;

public class MainActivity extends Activity {

    SurfaceView sv;
    SurfaceHolder sh;
    Camera cam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout fl = new FrameLayout(this);
        setContentView(fl);

        sv = new SurfaceView(this);
        sv.setOnClickListener(onClickListener);
        sh = sv.getHolder();
        sh.addCallback(new SurfaceHolderCallback());

        fl.addView(sv);
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            cam = Camera.open();
            try {
                cam.setPreviewDisplay(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            Camera.Parameters parameters = cam.getParameters();
            //List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            //Camera.Size previewSize = previewSizes.get(0);
            //parameters.setPreviewSize(previewSize.width, previewSize.height);
            cam.setParameters(parameters);
            cam.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cam.stopPreview();
            cam.release();
            cam = null;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // オートフォーカス
            if (cam != null) {
                cam.autoFocus(autoFocusCallback);
            }
        }
    };

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {

            }
        }
    };
}
