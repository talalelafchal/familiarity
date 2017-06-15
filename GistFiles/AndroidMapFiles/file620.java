package com.gmail.fedorenko.kostia.app1lesson4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by kfedoren on 23.09.2015.
 */
public class Util {
    public static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] bytearray) {
        return BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
    }

    public static String formatTime (String input){
        String output = input;
        SimpleDateFormat fromInput = new SimpleDateFormat("HH:mm");
        SimpleDateFormat toOutput = new SimpleDateFormat("HH:mm");
        try {
            String reformattedTime = toOutput.format(fromInput.parse(input));
            output = reformattedTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }
}
