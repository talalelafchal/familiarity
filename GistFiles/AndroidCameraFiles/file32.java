package org.diborg.opc;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends Activity {
    private Camera camera;
    private SurfaceView surface;
    private boolean previewStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surface = (SurfaceView) findViewById(R.id.surfaceView);
        Button previewButton = (Button) findViewById(R.id.button);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewStarted)
                    stopPreview();
                else
                    startPreview();
            }
        });
        Button captureButton = (Button) findViewById(R.id.button2);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                    }
                });
            }
        });
    }

    private void stopPreview() {
        camera.stopPreview();
        previewStarted = false;
    }

    private void startPreview() {
        try {
            camera.setPreviewDisplay(surface.getHolder());
            camera.startPreview();
            previewStarted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
        camera = null;
    }


}
