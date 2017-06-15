package com.example.arthu.testandroidcropper;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CropImageView.OnGetCroppedImageCompleteListener, CropImageView.OnSetImageUriCompleteListener {

    private CropImageView mCropImageView;

    private Uri mCropImageUri;

    private View mProgressView;

    private TextView mProgressViewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mProgressView = findViewById(R.id.ProgressView);
        mProgressViewText = (TextView) findViewById(R.id.ProgressViewText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnGetCroppedImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnGetCroppedImageCompleteListener(null);
    }

    /**
     * On load image button click, start pick  image chooser activity.
     */
    public void onLoadImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    /**
     * Crop the image and set it back to the  cropping view.
     */
    public void onCropImageClick(View view) {
        mCropImageView.getCroppedImageAsync();
        mProgressViewText.setText("Cropping...");
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSetImageUriComplete(CropImageView cropImageView, Uri uri, Exception error) {
        mProgressView.setVisibility(View.INVISIBLE);
        if (error != null) {
            Log.e("Crop", "Failed to load image for cropping", error);
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
        mProgressView.setVisibility(View.INVISIBLE);
        if (error == null) {
            if (bitmap != null) {
                mCropImageView.setImageBitmap(bitmap);
            }
        } else {
            Log.e("Crop", "Failed to crop image", error);
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            
            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
                mProgressViewText.setText("Loading...");
                mProgressView.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
            mProgressViewText.setText("Loading...");
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }
}
