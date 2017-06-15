// Copyright 2004-present Facebook. All Rights Reserved.

package com.facebook.fbu.photosphere.sphere;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.fbu.photosphere.sphere.Sphere.SpherePhotoType;
import com.facebook.fbu.photosphere.spherelib.PhotoSphereConstructor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import static com.facebook.fbu.photosphere.sphere.Sphere.SpherePhotoType.PANORAMA;
import static com.facebook.fbu.photosphere.sphere.Sphere.SpherePhotoType.PHOTOSPHERE;
import static com.facebook.fbu.photosphere.sphere.api.SphereAPI.SphereAddedCallback;
import static com.facebook.fbu.photosphere.sphere.api.SphereAPI.addSphere;


public class UploadPhotoActivity extends FragmentActivity implements
        SphereAddedCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = UploadPhotoActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MAX_UPLOAD_WIDTH_PX = 480; // in pixels
    private static final int LOCATION_UPDATE_INTERVAL = 10000; // in milliseconds
    private static final int LOCATION_UPDATE_FASTEST_INTERVAL = 1000; // in milliseconds

    // UI components
    private ImageView mPhotoImage;
    private EditText mCaptionEditText;
    private Dialog mProgressDialog;
    private SpherePhotoType mSpherePhotoType;

    private Location mLocation;
    private Bitmap mBitmap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean mIsFromCapture = false;
    private PhotoSphereConstructor mPhotoSphereConstructor;
    private Button mPostButton;

    private final Handler mHandler = new Handler();
    private final Runnable mComunicateWithConstructor = new Runnable() {
        @Override
        public void run() {
            if (mPhotoSphereConstructor.isConstructionDone()) {
                Toast.makeText(UploadPhotoActivity.this, "toast", Toast.LENGTH_SHORT).show();
                mPhotoImage.invalidate();
                mBitmap = mPhotoSphereConstructor.getBitmap();
                mPostButton.setEnabled(true);
            }

            mHandler.postDelayed(mComunicateWithConstructor, 100);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_upload_activity);
        mPhotoImage = (ImageView) findViewById(R.id.image);
        mPostButton = (Button) findViewById(R.id.post_button);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.upload_radio_buttons);
        radioGroup.check(R.id.photosphere);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mSpherePhotoType = (checkedId == R.id.photosphere) ? PHOTOSPHERE : PANORAMA;
            }

        });
        if (getIntent().getBooleanExtra(CameraActivity.class.getSimpleName(), false)) {
            mIsFromCapture = true;
            mPhotoSphereConstructor = PhotoSphereConstructor.getInstance();
            mPostButton.setEnabled(false);
            Toast.makeText(this, "HERE : " + mPhotoSphereConstructor.getBitmap().getHeight(),
                    Toast.LENGTH_SHORT).show();
            mPhotoImage.setImageBitmap(mPhotoSphereConstructor.getBitmap());
            mHandler.post(mComunicateWithConstructor);
        }


        
        if(!mIsFromCapture) {
            mPhotoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open new gallery intent
                    Intent startSelectActivity = new Intent(
                            UploadPhotoActivity.this,
                            GallerySelectionActivity.class);
                    startActivityForResult(startSelectActivity, PICK_IMAGE_REQUEST);
                }
            });
            Button postButton = (Button) findViewById(R.id.post_button);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post();
                }
            });
        }
        mCaptionEditText = (EditText) findViewById(R.id.caption_edit_text);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
    }
    /**
     * Handles image in upload selection
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                return;
            }
            Uri selectedImage = data.getParcelableExtra(GallerySelectionActivity.IMAGE_URI);

            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                mPhotoImage.setImageBitmap(mBitmap);
            } catch (IOException e) {
                Log.e(TAG, "image not found" + e);
            }
        }
    }

    /**
     * checks to see if a photo is a panorama or photosphere based on ratio of width to height
     *
     * @param uploadedPhoto - photo to analyze
     * @return true if panorama or photosphere, else false
     */
    private static boolean isPanoOrSphere(Bitmap uploadedPhoto) {
        return uploadedPhoto.getWidth() >= 2 * uploadedPhoto.getHeight();
    }

    /**
     * handles the uploading of the user's entry data to parse
     */
    private void post() {
        String caption = UXUtils.getText(mCaptionEditText);
        // if no img selected exit post
        if (mBitmap == null || !isPanoOrSphere(mBitmap)) {
            Toast.makeText(this, R.string.no_photo, Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                mBitmap,
                MAX_UPLOAD_WIDTH_PX,
                MAX_UPLOAD_WIDTH_PX * mBitmap.getHeight() / mBitmap.getWidth(),
                false);
        byte[] thumbnailByteArray = UXUtils.getByteArray(scaledBitmap);
        byte[] imageByteArray = UXUtils.getByteArray(mBitmap);
        mProgressDialog = ProgressDialog.show(
                UploadPhotoActivity.this,
                getResources().getString(R.string.app_name),
                getResources().getString(R.string.uploading),
                true);
        addSphere(
                imageByteArray,
                thumbnailByteArray,
                mSpherePhotoType,
                caption,
                mLocation,
                UploadPhotoActivity.this);
    }

    @Override
    public void onSphereAdded() {
        mProgressDialog.dismiss();
        finish();
    }

    @Override
    public void onSphereAddFailed() {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.upload_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this);
        } else {
            handleNewLocation(location);
        }
        Log.d(TAG, "Location services connected.");
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /**
     * used to reset mLat and mLng based on location
     *
     * @param location- the new location to be handled
     */
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        mLocation = location;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        Toast.makeText(this, getResources().getString(R.string.no_loc), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // starts an activity to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Location services connection failed with code " +
                    connectionResult.getErrorCode());
            Toast.makeText(
                    this,
                    getResources().getString(R.string.no_loc),
                    Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

}
