package tk.kolnoochenko.FlashLightADS;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ToggleButton;


import static android.view.View.*;

public class MyActivity extends Activity implements OnClickListener {

    private static Camera camera;
    private static Parameters parameters;
    private ToggleButton onOff;
    MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);

        onOff = (ToggleButton) this.findViewById(R.id.toggleButton_on_off);
        onOff.setOnClickListener(this);
        onOff.setChecked(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * Release Camera
         */
        if (camera != null) {
            camera.release();
            camera = null;
        }
        onOff.setChecked(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Check if light was on when App was paused. If so, turn back on.
         */
        if (parameters != null && parameters.getFlashMode() == Parameters.FLASH_MODE_TORCH) {
            if (camera == null)
                camera = Camera.open();
            camera.setParameters(parameters);
            onOff.setChecked(true);
        }
    }

    private void setFlashOn() {
        if (camera == null)
            camera = Camera.open();
        parameters = camera.getParameters();
        parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);

    }

    private void setFlashOff() {
        parameters = camera.getParameters();
        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggleButton_on_off:
                if (onOff.isChecked()) {
                    setFlashOn();
                    mp = MediaPlayer.create(MyActivity.this, R.raw.light_switch_on);

                     }
                else {
                    setFlashOff();
                    mp = MediaPlayer.create(MyActivity.this, R.raw.light_switch_off);

                }
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        mp.release();
                    }
                });
                mp.start();

                break;
        }

    }

}