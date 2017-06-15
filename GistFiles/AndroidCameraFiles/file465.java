package com.applikey.babybook.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import com.applikey.babybook.activity.MainActivity;
import com.applikey.babybook.activity_land.MainActivityLandscape;

import java.io.*;

public class BitmapUtils {
    public static final int MAX_RENDER_SIZE_XLARGE = 4096;
    public static final int MAX_RENDER_SIZE_LARGE = 2048;
    public static final int MAX_RENDER_SIZE_NORMAL = 1024;  // SMALL=1024dp (same as NORMAL)

    public static int max_render_size = 1024;

    private final Lo l = new Lo(BitmapUtils.this);
    private DisplayMetrics displaymetrics;
    private Activity context = null;


    public BitmapUtils(Activity context) {
        this.context = context;
    }


    public Bitmap resizeBitmapAspectRatio(Bitmap source, int maxSideSize, boolean rotate) {
        Bitmap bitmap;

        float largerSide = source.getHeight() >= source.getWidth() ? source.getHeight() : source.getWidth();
        float scale = maxSideSize / largerSide;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        if (rotate == true){
            int orientation = getOrientation(source);
            matrix = fixBitmapOrientation(matrix, source.getWidth(), source.getHeight(), orientation);
        }

        bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return bitmap;
    }

    /**
     * Check and fix photo orientation (in portrait mode) for some devices (f.e. Galaxy S2)
     */
    private Matrix fixBitmapOrientation(Matrix matrix, int bitmapWidth, int bitmapHeight, int orientation) {
        l.g("fixBitmapOrientation,  orientation = " + orientation);

        if ((isXLargeScreen() == false) && (bitmapHeight >= bitmapWidth)){
            // "smarphone" and "portrait" --> do nothing
            return matrix;

        } else if ((isXLargeScreen() == false) && (bitmapHeight < bitmapWidth)) {
            // "smarphone" and "landscape" --> rotate
            if (matrix == null) {
                matrix = new Matrix();
            }
            matrix.postRotate(90);
        }

//        if (orientation == 90) {
//            matrix.postRotate(90);
//        } else if (orientation == 180) {
//            matrix.postRotate(180);
//        } else if (orientation == 270) {
//            matrix.postRotate(270);
//        }

        return matrix;
    }

    public void saveBitmapToFile(int albumId, Bitmap resultBitmap, boolean isThumbImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        FileOutputStream fo = null;
        File f = null;

        try {
            f = new File(getAppPictureFilesPath(albumId, isThumbImage));
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            fo = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAppName() {
        Resources appR = context.getResources();
        CharSequence txt = appR.getText(appR.getIdentifier("app_name", "string", context.getPackageName()));
        return (String) txt;
    }

    public String getAppPictureFilesPath(int albumId, boolean isThumbImage) {
        String prefix = "ALBUM_IMG_" + albumId;
        String mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        String imageDirPath = mediaStorageDir + File.separator + getAppName();

        // create BB app dir if needs
        File directory = new File(imageDirPath);
        if (directory.exists() == false) {
            directory.mkdirs();
        }
        // create file to save image
        if (isThumbImage == true) {
            prefix = prefix + "_THUMB";
        }
        String fileFullPath = imageDirPath + File.separator + prefix + ".jpg";
        l.g("Images file path: " + fileFullPath);

        return fileFullPath;
    }

    public Uri scaleGalleryImageForCropper(Intent intent, int maxImageSize) {
        Bitmap scaledImage = getBitmapFromGalleryIntent(intent);
        scaledImage = resizeBitmapAspectRatio(scaledImage, maxImageSize, false);
        Uri photoUri = getUriFromBitmap(scaledImage);

        return photoUri;
    }

    public Uri scaleCameraImageForCropper(Uri imageUri, int maxImageSize) {
        Bitmap tempBitmap = getBitmapFromUri(imageUri);
        tempBitmap = resizeBitmapAspectRatio(tempBitmap, maxImageSize, true);
        Uri photoUri = getUriFromBitmap(tempBitmap);

        return photoUri;
    }

    private Bitmap getBitmapFromGalleryIntent(Intent intent) {
        InputStream stream;
        Bitmap tempImage = null;
        try {
            stream = context.getContentResolver().openInputStream(intent.getData());
            tempImage = BitmapFactory.decodeStream(stream);
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        tempImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        return tempImage;
    }

    private Uri getUriFromBitmap(Bitmap image) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "TEMP", null);

        return Uri.parse(path);
    }

    private Bitmap getBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public String getRealPathFromUri(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public Boolean removeFileFromStorage(Uri bitmapUri) {
        l.g("removeFileFromStorage,  bitmapUri REAL PATH:  + getRealPathFromUri(bitmapUri)");
        File finalFile = new File(getRealPathFromUri(bitmapUri));
        String tempPath = finalFile.getAbsolutePath();
        l.g("File to delete path:  " + tempPath);

        Boolean deleted = false;
        File file = new File(tempPath);
        if (file != null) {
            deleted = file.delete();
        }
        return deleted;
    }

    public Boolean removeFileFromStorage(String fileFullPath) {
        l.g("removeFileFromStorage,  fileFullPath:  " + fileFullPath);
        Boolean deleted = false;
        File file = new File(fileFullPath);

        if (file != null) {
            deleted = file.delete();
        }
        l.g("RESULT:  deleted = " + deleted);

        return deleted;
    }

    public int getMaxSideSize() {
        int maxSide;
        displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        if (height > width) {
            maxSide = height;
        } else {
            maxSide = width;
        }

        return maxSide;
    }

   /**
    *  Get max Bitmap rendering size (it depends from screen bigger side size).
    *   XLARGE:      960 x 720
    *   LARGE:       640 x 480
    *   NORMAL:      470 x 320
    *   SMALL:       426 x 320
   */
    public int getMaxBitmapSize(int screenSideSize) {
        max_render_size = screenSideSize;

        if (screenSideSize >= MAX_RENDER_SIZE_LARGE)
            max_render_size = MAX_RENDER_SIZE_XLARGE;  // side > 2048
        else if (screenSideSize >= MAX_RENDER_SIZE_NORMAL)
            max_render_size = MAX_RENDER_SIZE_LARGE;  // side > 1024
        else
            max_render_size = MAX_RENDER_SIZE_NORMAL; // side < 1024

        return max_render_size;
    }

    private boolean isXLargeScreen() {
        Configuration config = context.getResources().getConfiguration();
        return (config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
    *  Define device type (phone or tablet) and starts 1-st app activity.
    */
    private void startTabletOrPhoneActivity(){
        // @TODO This method should move to very app start ( to some static code block ? )
        Intent i;
        if ( isXLargeScreen() == true){
            i = new Intent(context, MainActivityLandscape.class);
        } else {
            i = new Intent(context, MainActivity.class);
        }
        context.startActivity(i);
    }

    public int getOrientation(Bitmap photo) {

        Uri photoUri = getUriFromBitmap(photo);
        Cursor cursor = context.getContentResolver().query(photoUri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if ((cursor == null) ||(cursor.getCount() != 1)) {
            return 0;
        }
        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    //    private void checkScreenOrientation(Configuration newConfig){
    //        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
    //            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
    //        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
    //            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
    //        }
    //    }
}
