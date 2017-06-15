// Copyright 2004-present Facebook. All Rights Reserved.

package com.facebook.fbu.photosphere.sphere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.facebook.fbu.photosphere.spherelib.CameraView;

/**
 * Camera activity hosts spherical camera as well as potential to upload existing photo.
 */
public class CameraActivity extends Activity {
    CameraView mCameraView;
    FrameLayout mFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        mCameraView = new CameraView(CameraActivity.this);
        final ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.lower_panel_switcher);
        Button captureButton = (Button) findViewById(R.id.capture_photo);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showNext();
                mFrameLayout = (FrameLayout) findViewById(R.id.camera_holder);
                mFrameLayout.addView(mCameraView);
            }
        });
        ImageButton uploadButton = (ImageButton) findViewById(R.id.upload_photo);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startPhotoUpload = new Intent(CameraActivity.this, UploadPhotoActivity.class);
                startActivity(startPhotoUpload);
            }
        });
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraView.popPicture();
            }
        });

        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showPrevious();
                mFrameLayout.removeView(mCameraView);
            }
        });

        ImageButton doneButton = (ImageButton) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraView.startConstruction();
                mCameraView.savePictureToFileWhenDone("my_file_name");
                Intent uploadPhotoIntent = new Intent(
                        CameraActivity.this,
                        UploadPhotoActivity.class);
                uploadPhotoIntent.putExtra(CameraActivity.class.getSimpleName(), true);
                startActivity(uploadPhotoIntent);
                finish();
            }
        });
    }
}