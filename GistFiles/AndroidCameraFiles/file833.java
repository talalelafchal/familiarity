package com.legendmohe.rappid.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.legendmohe.rappid.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by legendmohe on 16/4/6.
 */
public class ImagePickerHelper {
    private static final String TAG = "ImagePickerHelper";

    private static final int CAMERA_REQUEST_CODE = 1;

    private ImagePicker mImagePicker;
    private CameraImagePicker mCameraImagePicker;
    private String mCameraOutputPath;

    private PickerCreator mPickerCreator;
    private ImageView mSelectedImageView;

    private ImagePickerHelperListener mListener;

    public ImagePickerHelper(AppCompatActivity activity) {
        mPickerCreator = new PickerCreator(activity, null);
    }

    public ImagePickerHelper(AppCompatActivity activity, ImageView imageView) {
        mPickerCreator = new PickerCreator(activity, null);
        mSelectedImageView = imageView;
    }

    public ImagePickerHelper(Fragment fragment) {
        mPickerCreator = new PickerCreator(null, fragment);
    }

    public ImagePickerHelper(Fragment fragment, ImageView imageView) {
        mPickerCreator = new PickerCreator(null, fragment);
        mSelectedImageView = imageView;
    }

    public void setListener(ImagePickerHelperListener listener) {
        mListener = listener;
    }

    private ImagePickerCallback mImagePickerCallback = new ImagePickerCallback() {
        @Override
        public void onImagesChosen(List<ChosenImage> images) {
            if (images.size() != 0) {
                String imagePath = images.get(0).getOriginalPath();
                if (mSelectedImageView != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = ImageUtil.scaleImageFile(
                                mPickerCreator.getContext(),
                                Uri.fromFile(new File(imagePath)),
                                mSelectedImageView.getWidth(),
                                mSelectedImageView.getHeight(),
                                mSelectedImageView.getScaleType()
                        );
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "onImagesChosen: ", e);
                    }
                    mSelectedImageView.setImageBitmap(bitmap);
                }
                if (mListener != null) {
                    mListener.onImagePicked(mSelectedImageView, imagePath, images.get(0).getThumbnailPath());
                }
            }
        }

        @Override
        public void onError(String message) {
            // Do error handling
        }
    };

    public void pickFromGalary() {
        mImagePicker = mPickerCreator.createImagePicker();
        mImagePicker.setImagePickerCallback(mImagePickerCallback);
        mImagePicker.pickImage();
    }

    public void pickFromCamera() {
        Context context = mPickerCreator.getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                mPickerCreator.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);
                return;
            }
        }
        mCameraImagePicker = mPickerCreator.createCameraImagePicker();
        mCameraImagePicker.setImagePickerCallback(mImagePickerCallback);
        mCameraOutputPath = mCameraImagePicker.pickImage();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (mImagePicker == null) {
                    mImagePicker = mPickerCreator.createImagePicker();
                    mImagePicker.setImagePickerCallback(mImagePickerCallback);
                }
                mImagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (mCameraImagePicker == null) {
                    mCameraImagePicker = mPickerCreator.createCameraImagePicker();
                    mCameraImagePicker.reinitialize(mCameraOutputPath);
                    mCameraImagePicker.setImagePickerCallback(mImagePickerCallback);
                }
                mCameraImagePicker.submit(data);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCameraImagePicker = mPickerCreator.createCameraImagePicker();
                mCameraImagePicker.setImagePickerCallback(mImagePickerCallback);
                mCameraOutputPath = mCameraImagePicker.pickImage();
            } else {
                if (mListener != null) {
                    mListener.onUserDeniedPermission();
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(mCameraOutputPath))
            outState.putString("picker_path", mCameraOutputPath);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                mCameraOutputPath = savedInstanceState.getString("picker_path");
            }
        }
    }

    public ImageView getSelectedImageView() {
        return mSelectedImageView;
    }

    public void setSelectedImageView(ImageView selectedImageView) {
        mSelectedImageView = selectedImageView;
    }

    private static class PickerCreator {

        private AppCompatActivity mActivity;
        private Fragment mFragment;

        public PickerCreator(AppCompatActivity activity, Fragment fragment) {
            mActivity = activity;
            mFragment = fragment;
        }

        public CameraImagePicker createCameraImagePicker() {
            if (mActivity != null)
                return new CameraImagePicker(mActivity);
            if (mFragment != null)
                return new CameraImagePicker(mFragment);
            return null;
        }

        public ImagePicker createImagePicker() {
            if (mActivity != null)
                return new ImagePicker(mActivity);
            if (mFragment != null)
                return new ImagePicker(mFragment);
            return null;
        }

        public Context getContext() {
            if (mActivity != null) {
                return mActivity;
            } else if (mFragment != null && mFragment.getContext() != null) {
                return mFragment.getContext();
            }
            return null;
        }

        public void requestPermissions(String[] permissions, int cameraRequestCode) {
            if (mActivity != null) {
                ActivityCompat.requestPermissions(mActivity, permissions, cameraRequestCode);
            }
            if (mFragment != null)
                mFragment.requestPermissions(permissions, cameraRequestCode);
        }
    }


    public interface ImagePickerHelperListener {
        void onImagePicked(ImageView selectedImageView, String imagePath, String thumbnailPath);

        void onUserDeniedPermission();
    }
}
