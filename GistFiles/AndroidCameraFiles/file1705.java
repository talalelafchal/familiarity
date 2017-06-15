import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;


public class CameraManager {

    private static final String STATE_CAMERA_FILE_NAME = "STATE_CAMERA_FILE_NAME";
    private static final int REQUEST_CAMERA = 0x9001;

    String mCameraFileName;

    public CameraManager() {
    }

    public void showCamera(Activity activity) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            mCameraFileName = getNewCameraFileName();
            Uri imageUri = Uri.fromFile(new File(mCameraFileName));
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            activity.startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    public void showCamera(Fragment fragment) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            mCameraFileName = getNewCameraFileName();
            Uri imageUri = Uri.fromFile(new File(mCameraFileName));
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            fragment.startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            String fileName = mCameraFileName;
            mCameraFileName = null;
            if (TextUtils.isEmpty(fileName)) {
                if (mCaptureImageListener != null) {
                    mCaptureImageListener.onFailed();
                }
                return;
            }
            File file = new File(fileName);
            if (!file.exists()) {
                if (mCaptureImageListener != null) {
                    mCaptureImageListener.onFailed();
                }
                return;
            }
            Uri cameraUri = null;
            try {
                cameraUri = Uri.fromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cameraUri == null) {
                if (mCaptureImageListener != null) {
                    mCaptureImageListener.onFailed();
                }
                return;
            }

            mCaptureImageListener.onSuccess(cameraUri);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_CAMERA_FILE_NAME, mCameraFileName);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCameraFileName = savedInstanceState.getString(STATE_CAMERA_FILE_NAME);
        }
    }

    private CaptureImageListener mCaptureImageListener;

    public void setCaptureImageListener(CaptureImageListener listener) {
        mCaptureImageListener = listener;
    }

    public interface CaptureImageListener {
        void onSuccess(Uri imageUri);

        void onFailed();
    }


    public static String getNewCameraFileName() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
        String newPath = path + "/Camera/";

        //check if the path exists
        File f = new File(newPath);
        if (f.isDirectory() && f.exists()) {
            // do nothing
        } else {
            f.mkdirs();
        }

        try {
            File file = File.createTempFile("CAMERA_" + String.valueOf(System.currentTimeMillis()), ".jpg", f);
            return file.getAbsolutePath();
        } catch (IOException e) {
           e.printStackTrace();
        }
        return "";
    }