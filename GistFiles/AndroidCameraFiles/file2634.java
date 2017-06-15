package com.gpi.ratethis;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by u63527 on 6/30/2014.
 */
public class PhotoHelper {

    public static Uri generateTimeStampPhotoFileUri() {
        Uri photoFileUri = null;

        File outputDir = getPhotoDirectory();

        if(outputDir != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
            String photoFileName = "IMG_" + timeStamp + ".jpg";

            File photoFile = new File(outputDir, photoFileName);
            photoFileUri = Uri.fromFile(photoFile);
        }

        return photoFileUri;
    }

    public static File getPhotoDirectory() {
        File outputDir = null;

        //Confirm that the external storage (SD Card) is mounted
        String externalStorageState = Environment.getExternalStorageState();

        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            outputDir = new File(pictureDir, "RateThis");

            if(!outputDir.exists()) {
                if(!outputDir.mkdirs()) {
                    outputDir = null;
                }
            }
        }
        return outputDir;
    }

    public static void addPhotoToMediaStoreAndDisplayImage(String pathName, Activity activity, ImageView imageView) {
        final ImageView pictureImageView = imageView;
        final Activity pictureActivity = activity;


        String[] filesToScan = {pathName};

        MediaScannerConnection.scanFile(pictureActivity, filesToScan, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                long id = ContentUris.parseId(uri);
                ContentResolver resolver = pictureActivity.getContentResolver();

                final Bitmap image = MediaStore.Images.Thumbnails.getThumbnail(
                        resolver, id, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, null);

                pictureActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pictureImageView.setImageBitmap(image);
                    }
                });
            }
        });
    }
}
