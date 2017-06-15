package com.lxy.media;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.GetChars;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.List;

public class MainActivity extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback{


    Camera camera;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.CameraView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        surfaceView.setClickable(true);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, null, MainActivity.this);
            }
        });



    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();

        List<String> colorEffects = parameters.getSupportedColorEffects();
        for(String effect :  colorEffects){
            Log.d("TAG", effect);
        }
        parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
        camera.setDisplayOrientation(180);


        List<Camera.Size> previewSize = parameters.getSupportedPreviewSizes();
        if (previewSize.size() > 1){
            for (Camera.Size size : previewSize){
                Log.d("TAG", String.valueOf(size.height) + " * " + String.valueOf(size.width));
            }
        }
        try{
            camera.setPreviewDisplay(holder);
        } catch (Exception e){
            camera.release();
        }
        camera.setParameters(parameters);

        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();;
        camera.release();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        ContentValues contentValues = new ContentValues();

        Uri imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try{
            Toast.makeText(this, "Photo Token", Toast.LENGTH_SHORT).show();
            Log.d("TAG", imageFileUri.toString());
            OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        camera.startPreview();
    }
}
