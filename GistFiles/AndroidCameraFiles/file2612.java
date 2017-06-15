package com.weblinkstech.flashlight;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

/**
 * Created by Márk on 2014.08.25..
 */
public class Flash {
    public boolean isActive() {
        return active;
    }

    private boolean active;
    private Context context;
    private Camera camera = null;
    private Camera.Parameters params;

    public Flash(Context context) {
        this.context = context;
    }

    public boolean hasFlash(){

        return context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void prepare() {
        if (hasFlash()) {

            try {
                camera = Camera.open();
                params = camera.getParameters();
                camera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
        } else {
            AlertDialog alert = new AlertDialog.Builder(context)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    System.exit(0);
                }
            });
            alert.show();

        }
    }


    public void turnOn(Context context){
            {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
           }

        active = true;
    }

    public void turnOff(Context context) {
    {
             params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
             camera.setParameters(params);
             camera.startPreview();
    }

        active = false;
    }


}
