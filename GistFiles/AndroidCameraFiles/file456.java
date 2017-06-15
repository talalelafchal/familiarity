package com.idbalap.helper;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Fakhri on 23/09/2015.
 */
public class Helputil {

    public final static int CAM_IMAGE_REQUEST = 100;
    public final static int LIB_IMAGE_REQUEST = 800;
    public final static int STORAGE_REQUEST = 400;
    public final static int CAM_VIDEO_REQUEST = 200;
    public final static int CAM_AUDIO_REQUEST = 300;

    public final static int MEDIA_TYPE_IMAGE = 1;
    public final static int MEDIA_TYPE_VIDEO = 2;
    public final static int MEDIA_TYPE_AUDIO = 3;
    public static final String IMAGE_DIR_NAME = "balap_image";

    public static final int LOGIN_FIRST = 33;

    public static String ToUpperCase(String origin){
        if (origin.length() > 0) {
            String text = origin.toLowerCase().trim();
            String[] textsplit = text.split(" ");
            String output = "";

            for (String txt : textsplit) {
                if (txt.length() > 0){
                    output += txt.substring(0, 1).toUpperCase() + txt.substring(1) + " ";
                }
            }

            return output;
        }else{
            return origin;
        }
    }

    public static String Club_ToUpperCase(String origin){
        if (origin.length() > 5) {
            String text = origin.toLowerCase().trim();
            String[] textsplit = text.split(" ");
            String output = "";

            for (String txt : textsplit) {
                if (txt.length() > 0){
                    output += txt.substring(0, 1).toUpperCase() + txt.substring(1) + " ";
                }
            }
            return output;
        }else{
            return origin.toUpperCase();
        }
    }

    public static Uri getOutputMediaFileUri(int type, String UID) {
        return Uri.fromFile(getOutputMediaFile(type, UID));
    }

    public static File getOutputMediaFile(int type, String UID){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        String folder = "";
        switch (type){
            case MEDIA_TYPE_IMAGE:
                folder = IMAGE_DIR_NAME;
                break;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folder);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
            mediaStorageDir.mkdirs();
        }

        // Create a media file name
        Calendar cal = Calendar.getInstance();
        String timeStamp = "" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DATE);
        File mediaFile;
        Random random = new Random();
        int rand = random.nextInt(999);
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + "_" + UID + Hash.md5(rand+"") + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + "_" + UID +".mp4");
        } else if(type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "AUD_"+ timeStamp + "_" + UID +".3gp");
        } else {
            return null;
        }

        return mediaFile;

    }
}