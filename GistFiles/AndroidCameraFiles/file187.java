package com.finc.camera.provider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is manager for a device camera.
 */
public class CameraManager {
    public static final int REQUEST_CODE_CAPTURE = 100;
    public static final int REQUEST_CODE_IMAGE_PICK = 101;
    private Activity activity;
    private Uri captureImageUri;

    public CameraManager(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * Get camera intent for launching device default camera.
     *
     * @param activity {@link Activity}
     * @return {@link Intent}
     */
    public Intent getDefaultCameraIntent(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // check if there is an application in device
        // that has activity to be enable to handle this intent.
        if (intent.resolveActivity(activity.getPackageManager()) == null) {
            return null;
        }

        // there is a case when the external storage is not mounted.
        String state = Environment.getExternalStorageState();
        if (!TextUtils.equals(Environment.MEDIA_MOUNTED, state)) {
            throw new IllegalArgumentException();
        }

        captureImageUri = getCameraFilePath(createCameraFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri);
        return intent;
    }

    public void startCamera(@NonNull Intent cameraIntent) {
        activity.startActivityForResult(cameraIntent, REQUEST_CODE_CAPTURE);
    }

    @Nullable
    public Uri getCameraPreviewFilePath(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    return getCaptureFilePath(data);
                }
                break;
            case REQUEST_CODE_IMAGE_PICK:
                if (resultCode == Activity.RESULT_OK) {
                    return checkNull(data) ? null : data.getData();
                }
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * DON'T CALL THIS METHOD IN MAIN THREAD.
     *
     * @param data {@link Intent}
     * @return {@link Bitmap}
     * @throws IOException
     */
    @Nullable
    public Bitmap getPickerBitmap(@Nullable Intent data) throws IOException {
        if (data == null) {
            return null;
        }

        Uri uri = data.getData();
        if (uri == null) {
            return null;
        }

        ParcelFileDescriptor parcelFileDescriptor
                = activity.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return bitmap;
    }

    public Bitmap getCapturePreviewBitmap(@NonNull Uri uri) {
        // camera uri access is a little bit different depending on versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return getRotatedBitmap(uri);
        }
        return getRotatedBitmapForKitkat(uri);
    }

    private Uri getCaptureFilePath(@Nullable Intent data) {
        if (checkNull(data)) {
            return captureImageUri;
        }
        return data.getData();
    }

    private boolean checkNull(@Nullable Intent data) {
        return data == null
                || data.getData() == null;
    }

    /**
     * Get camera file path that would be stored after capturing image with default camera.
     *
     * @param contentValues {@link ContentValues}
     * @return {@link Uri}
     * @see #createCameraFile()
     */
    private Uri getCameraFilePath(@NonNull ContentValues contentValues) {
        return activity.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    /**
     * Create capture image file as {@link ContentValues}
     * call with {@link #getCameraFilePath(ContentValues)}
     *
     * @return {@link ContentValues}
     * @see #getCameraFilePath(ContentValues)
     */
    private ContentValues createCameraFile() {
        String photoName = System.currentTimeMillis() + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, photoName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return contentValues;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Nullable
    public Bitmap getRotatedBitmapForKitkat(@NonNull Uri uri) {

        // get file path to data.
        String filePath = uri.getPath();
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        //permit temporarily access to data.
        BitmapFactory.Options options = null;
        InputStream inputStream;
        activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            inputStream = activity.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }
            options = getBitmapFactoryOptions(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (options == null) {
            return null;
        }

        Bitmap bmp = null;
        try {
            inputStream = activity.getContentResolver().openInputStream(uri);

            //try to get bitmap data and convert it to byte-array
            bmp = BitmapFactory.decodeStream(inputStream, null, options);

            assert inputStream != null;
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bmp == null) {
            return null;
        }

        Matrix matrix = getRotatedMatrix(filePath);
        Bitmap b = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        // 不要なBitmapは明示的に解放するためにrecycle()を呼んでおく
        if (b != bmp && !bmp.isRecycled()) {
            bmp.recycle();
        }
        return b;
    }

    @Nullable
    public Bitmap getRotatedBitmap(@NonNull Uri uri) {
        // under the kitkat, capture you take will saved in the mounted area.
        // so the default camera returns totally different path from uri's.
        // the data camera returns e.g. `content://media/external/images/media/25`
        // the actual image path is e.g. `/mnt/sdcard/DCIM/Camera/1472280411064.jpg`
        Cursor cursor = getQueriedCursor(uri);
        if (cursor == null) {
            return null;
        }

        String filePath = getFilePath(cursor);
        if (TextUtils.isEmpty(filePath)) {
            cursor.close();
            return null;
        }

        //set inJustDecodeBounds true to use memory efficiently.
        BitmapFactory.Options options = getBitmapFactoryOptions(filePath);

        //create bitmap
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        if (bmp == null) {
            return null;
        }

        Matrix matrix = getRotatedMatrix(filePath);
        Bitmap b = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        // 不要なBitmapは明示的に解放するためにrecycle()を呼んでおく
        if (b != bmp && !bmp.isRecycled()) {
            bmp.recycle();
        }
        return b;
    }

    @Nullable
    private String getFilePath(@NonNull Cursor cursor) {
        int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        // -1 is when not found.
        if (dataColumnIndex == -1) {
            cursor.close();
            return null;
        }
        return cursor.getString(dataColumnIndex);
    }

    @Nullable
    private Cursor getQueriedCursor(@NonNull Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);

        if (cursor == null) {
            return null;
        }

        //if cursor is empty, it returns false
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return cursor;
    }

    private BitmapFactory.Options getBaseBitmapFactoryOptions(@NonNull Object filePath) {
        //set inJustDecodeBounds true to use memory efficiently.
        BitmapFactory.Options options = new BitmapFactory.Options();
        //if set true, next method will not allocate bitmap in memory.
        options.inJustDecodeBounds = true;

        // file is String when image taken by camera or InputStream if image from document.
        if (filePath instanceof String) {
            BitmapFactory.decodeFile((String) filePath, options);
        } else if (filePath instanceof InputStream) {
            BitmapFactory.decodeStream((InputStream) filePath, null, options);
        } else {
            new IllegalArgumentException("argument file must be instance of String or InputStream");
        }

        // set desired size.
        int width = getMaxImageWidth();
        int height = getMaxImageHeight();

        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, width, height);
        //if set false, next decode method create bitmap and put it in memory.
        options.inJustDecodeBounds = false;

        // shrink the size of bitmap
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return options;
    }

    private BitmapFactory.Options getBitmapFactoryOptions(@NonNull String filePath) {
        return getBaseBitmapFactoryOptions(filePath);
    }

    private BitmapFactory.Options getBitmapFactoryOptions(@NonNull InputStream filePath) {
        return getBaseBitmapFactoryOptions(filePath);
    }

    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    private int getMaxImageWidth() {
        return activity.getResources().getDisplayMetrics().widthPixels;
    }

    private int getMaxImageHeight() {
        return activity.getResources().getDisplayMetrics().widthPixels * 9 / 16;
    }


    private Matrix getRotatedMatrix(@NonNull String filePath) {
        ExifInterface exifInterface;
        Matrix matrix = new Matrix();

        try {
            exifInterface = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return matrix;
        }

        // Get the rotation of image.
        int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1f, 1f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(-90f);
                matrix.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90f);
                matrix.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(-90f);
                break;
        }
        return matrix;
    }

}
