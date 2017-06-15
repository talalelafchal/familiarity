import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by lamphuong.
 */
public class ImagePicker {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_GALLERY, TYPE_CAMERA})
    public @interface RequestType {
    }

    public static final int TYPE_GALLERY = 37;
    public static final int TYPE_CAMERA = 137;

    private Activity mActivity;
    private Callback mCallback;
    private Uri mFileUri;

    public interface Callback {
        void onSuccess(Uri imageUri);

        void onFail();
    }

    public ImagePicker(Activity activity) {
        mActivity = activity;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (mFileUri != null)
            outState.putParcelable("uri", mFileUri);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("uri"))
            mFileUri = savedInstanceState.getParcelable("uri");
    }

    public void setCallback(@NonNull Callback callback) {
        mCallback = callback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallback == null) 
            return;
        if (requestCode == TYPE_GALLERY || requestCode == TYPE_CAMERA) {
            if (resultCode == Activity.RESULT_OK)
                result(requestCode, data);
            else
                mCallback.onFail();
        }
    }

    private void result(int requestCode, Intent data) {

        if (requestCode == TYPE_CAMERA) {
            mCallback.onSuccess(mFileUri);
            Log.d("result", "result: " + mFileUri);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mFileUri);
            mActivity.sendBroadcast(mediaScanIntent);

        } else if (data != null) {
            mCallback.onSuccess(data.getData());
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 13) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromCamera();
            }
        }
    }


    public void pick(@RequestType int requestType) {
        if (requestType == TYPE_GALLERY) {

            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mActivity.startActivityForResult(intent, TYPE_GALLERY);

        } else if (requestType == TYPE_CAMERA) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Utils.showPermissionRequest(mActivity, Manifest.permission.CAMERA, 13);
                return;
            }
            pickFromCamera();
        }
    }

    private void pickFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {

                mFileUri = FileProvider.getUriForFile(mActivity,
                        "net.beutech.timnha.fileprovider",
                        photoFile);

                List<ResolveInfo> resInfoList = mActivity.getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    Log.d("packageName ", packageName);
                    mActivity.grantUriPermission(packageName, mFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                mActivity.startActivityForResult(intent, TYPE_CAMERA);
            }
        }
    }


    @NonNull
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        File storageDir;
        storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

    }

}
