package com.app.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * Created by joaspacce.
 *
 * Helper class to import images from the gallery or the camera.
 *
 */

public class ImagePickerUtil {

    private final static String TAG = ImagePickerUtil.class.getSimpleName();
    private final static int INTERNAL_STORAGE = 0;
    private final static int INTERNAL_STORAGE_CACHE = 1;
    private final static int EXTERNAL_STORAGE = 2;

    private Activity activity;
    private Fragment fragment;
    private Context context;
    private int requestCodeGallery;
    private int requestCodeCamera;
    private Uri imageUriCamera;
    private int modeStorage;
    private ImagePickerUtilListener imagePickerUtilListener;
    private ImageFetchAsyncTask imageLoadAsyncTask;

    public ImagePickerUtil(Activity activity, ImagePickerUtilListener imagePickerUtilListener) {
        this(activity, INTERNAL_STORAGE_CACHE, imagePickerUtilListener);
    }

    public ImagePickerUtil(Activity activity, int modeStorage, ImagePickerUtilListener imagePickerUtilListener) {
        this.activity = activity;
        this.fragment = null;
        this.modeStorage = modeStorage;
        this.imagePickerUtilListener = imagePickerUtilListener;
        this.context = activity.getApplicationContext();
    }

    public ImagePickerUtil(Fragment fragment, ImagePickerUtilListener imagePickerUtilListener) {
        this(fragment, INTERNAL_STORAGE_CACHE, imagePickerUtilListener);
    }

    public ImagePickerUtil(Fragment fragment, int modeStorage, ImagePickerUtilListener imagePickerUtilListener) {
        this.activity = null;
        this.fragment = fragment;
        this.modeStorage = modeStorage;
        this.imagePickerUtilListener = imagePickerUtilListener;
        this.context = fragment.getActivity().getApplicationContext();
    }

    private String getPathFileFromLocalUri(Context context, Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            String pathFile = cursor.getString(column_index);
            cursor.close();
            return pathFile;
        } else {
            return uri.getPath();
        }
    }

    private String getPictureFromCamera(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String pathFile = cursor.getString(column_index);
            cursor.close();
            return pathFile;
        } else {
            return null;
        }
    }

    private String formatFileName(String url) {
        if (url != null && url.contains("http")) {
            return url.replace("/", "").replace("http", "");
        } else {
            return url;
        }
    }

    private void copyFdToFile(FileDescriptor src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    public void callIntentGallery(int requestCode) {
        requestCodeGallery = requestCode;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (activity != null) {
            activity.startActivityForResult(intent, requestCodeGallery);
        } else {
            fragment.startActivityForResult(intent, requestCodeGallery);
        }
    }

    public void callIntentCamera(int requestCode) {
        requestCodeCamera = requestCode;
        imageUriCamera = null;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, UUID.randomUUID().toString() + ".jpg");
            imageUriCamera = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUriCamera);
            if (activity != null) {
                activity.startActivityForResult(intent, requestCodeCamera);
            } else {
                fragment.startActivityForResult(intent, requestCodeCamera);
            }
        } catch (Exception e) {
            Log.e(TAG, "No camera detected " + e.getMessage().toString());
            if (imagePickerUtilListener != null) {
                imagePickerUtilListener.onErrorNotCamera();
            }
        }
    }

    public void handleResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == this.requestCodeGallery) { // Gallery
            if (resultCode == Activity.RESULT_OK) {
                if (resultData != null) {
                    Uri uri = resultData.getData();
                    Log.i(TAG, "Uri: " + uri.toString());
                    if (imageLoadAsyncTask != null && !imageLoadAsyncTask.isCancelled() &&
                            imageLoadAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                        imageLoadAsyncTask.cancel(true);
                    }
                    imageLoadAsyncTask = new ImageFetchAsyncTask();
                    imageLoadAsyncTask.execute(uri);
                } else {
                    imagePickerUtilListener.onErrorImportGallery();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                imagePickerUtilListener.onCancelAction();
            }
        } else if (requestCode == this.requestCodeCamera) { // Camera
            if (resultCode == Activity.RESULT_OK) {
                if (imageUriCamera != null) {
                    Log.i(TAG, "Uri: " + imageUriCamera.toString());
                    String path = getPictureFromCamera(context, imageUriCamera);
                    if (imagePickerUtilListener != null) {
                        imagePickerUtilListener.onFoundFile(path);
                    }
                } else {
                    imagePickerUtilListener.onErrorImportCamera();
                }
            } else {
                imagePickerUtilListener.onCancelAction();
            }
        }
    }

    /**
     * Grabs metadata for a document specified by URI, logs it to the screen.
     *
     * @param uri The uri for the document whose metadata should be printed.
     */
    private void dumpImageMetaData(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an int can't be
                // null in java, the behavior is implementation-specific, which is just a fancy
                // term for "unpredictable".  So as a rule, check if it's null before assigning
                // to an int.  This will happen often:  The storage API allows for remote
                // files, whose size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString will do the
                    // conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Create a File from the URI for that image and return the path to it.
     *
     * @param uri the Uri for the image to return the path.
     */
    private String getBitmapFilePathFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            File f = getFile(uri);
            copyFdToFile(fileDescriptor, f);
            parcelFileDescriptor.close();
            String filePath = f.getAbsolutePath();
            Log.i(TAG, "File path: " + filePath);
            return filePath;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    /**
     * Create a File from Uri, to storage selected.
     *
     * @param uri
     * @return
     * @throws IOException
     */
    private File getFile(Uri uri) throws IOException {
        if (modeStorage == INTERNAL_STORAGE) {
            String fileName = formatFileName(uri.getLastPathSegment());
            File file = new File(context.getFilesDir(), fileName);
            boolean deleted = file.delete();
            Log.i(TAG, "File temp deleted = " + deleted);
            file.createNewFile();
            return file;
        } else if (modeStorage == INTERNAL_STORAGE_CACHE) {
            String fileName = formatFileName(uri.getLastPathSegment());
            File file = File.createTempFile(fileName, null, context.getCacheDir());
            return file;
        } else { // modeStorage == EXTERNAL_STORAGE
            String fileName = Environment.getExternalStorageDirectory() + java.io.File.separator + formatFileName(uri.getLastPathSegment());
            File file = new File(fileName);
            boolean deleted = file.delete();
            Log.i(TAG, "File temp deleted = " + deleted);
            file.createNewFile();
            return file;
        }
    }

    public interface ImagePickerUtilListener {
        public void onBeginFetch();

        public void onErrorNotCamera();

        public void onErrorImportGallery();

        public void onErrorImportCamera();

        public void onFoundFile(String filePath);

        public void onCancelAction();
    }

    /**
     * AsyncTask to fetch Image.
     */
    private class ImageFetchAsyncTask extends AsyncTask<Uri, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "ImageFetchAsyncTask Begin");
            if (imagePickerUtilListener != null) {
                imagePickerUtilListener.onBeginFetch();
            }
        }

        @Override
        protected String doInBackground(Uri... uris) {
            String path = getPathFileFromLocalUri(context, uris[0]);
            if (path != null) {
                return path;
            } else { // path == null
                dumpImageMetaData(uris[0]);
                return getBitmapFilePathFromUri(uris[0]);
            }
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            Log.i(TAG, "ImageFetchAsyncTask Finished");
            if (imagePickerUtilListener != null) {
                imagePickerUtilListener.onFoundFile(fileName);
            }
        }
    }
}
