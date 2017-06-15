package com.example.flashlight;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ImageButton imgButton;
	private boolean isFlag = false;
	Camera camera;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imgButton = (ImageButton) findViewById(R.id.imgButton);
		PackageManager pm = getPackageManager();
		// Whether device support camera?
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			camera = Camera.open();
		} else {
			Toast.makeText(this, "No camera", Toast.LENGTH_LONG).show();
		}

	}

	public void btn_Click(View view) {
		Parameters p = camera.getParameters();
		if (isFlag == false) {
			imgButton.setImageResource(R.drawable.flashon);
           //Camera Flash light On
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(p);
			camera.startPreview();

			isFlag = true;
		} else {
			//Camera Flash light Off
			imgButton.setImageResource(R.drawable.flashoff);
			p.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(p);
			camera.stopPreview();
			isFlag = false;
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
        // On application close camera Flash light will off.
		camera.release();

	}

}
