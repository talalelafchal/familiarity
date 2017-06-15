/**
 *
 */
package me.sotm.practice.camera;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

/**
 *
 */
public class ExternalAppOpener {

    public static final int  REQUEST_GALLERY = 1;
    public static final int  REQUEST_CAMERA  = 2;
    public static final int  REQUEST_CROP    = 3;

    private static final int _TRIMMING_X     = 640;
    private static final int _TRIMMING_Y     = 640;
    private static final int _ASPECT_X       = 1;
    private static final int _ASPECT_Y       = 1;

    /**
     * ギャラリーを開く
     *
     * @param activity
     */
    public static void openGallery(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, ExternalAppOpener.REQUEST_GALLERY);
    }

    /**
     * カメラアプリを開く
     *
     * @param activity
     * @return
     */
    public static Uri openCamera(Activity activity) {
        Uri uri = null;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.currentTimeMillis());
        stringBuilder.append(".jpg");

        String fileName = stringBuilder.toString();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        ContentResolver contentResolver = activity.getContentResolver();
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, ExternalAppOpener.REQUEST_CAMERA);

        return uri;
    }

    /**
     * 切り抜きのアプリを開く
     *
     */
    public static boolean openCrop(Activity activity, Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> resolveInfoList = activity.getPackageManager().queryIntentActivities(intent, 0);
        int resolveInfoSize = resolveInfoList.size();

        if (0 == resolveInfoSize) {
            return false;
        } else {

            intent.setData(uri);
            intent.putExtra("outputX", ExternalAppOpener._TRIMMING_X);
            intent.putExtra("outputY", ExternalAppOpener._TRIMMING_Y);
            intent.putExtra("aspectX", ExternalAppOpener._ASPECT_X);
            intent.putExtra("aspectY", ExternalAppOpener._ASPECT_Y);
            intent.putExtra("scale", true);
            intent.putExtra("setWallpaper", false);
            intent.putExtra("noFaceDetection", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            ResolveInfo resolveInfo = resolveInfoList.get(0);
            String packageName = resolveInfo.activityInfo.packageName;
            String name = resolveInfo.activityInfo.name;
            intent.setComponent(new ComponentName(packageName, name));
            activity.startActivityForResult(intent, ExternalAppOpener.REQUEST_CROP);

            return true;
        }
    }

}
