package com.finc.camera.provider;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

/**
 * This Utils is for permission above {@link android.os.Build.VERSION_CODES#LOLLIPOP_MR1}.
 * In {@link android.os.Build.VERSION#SDK_INT} <= 21, the user requires to check permission before install.
 * Over 21, Marshmallow provide the RuntimePermission for user.
 */
public class PermissionUtils {

    private PermissionUtils() {
        // you cannot create my instance. ha ha.
    }

    /**
     * Use this when there is a requirement to check permission.
     * If this returns {@code true}, the user already have that permission on that.
     * Otherwise, it's necessary to use {@link ActivityCompat#requestPermissions(Activity, String[], int)}
     * After calling this method, {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * will be called.
     *
     * @param context     {@link Context}
     * @param permissions {@link String...} permissions required
     * @return boolean
     * @see android.support.v4.content.PermissionChecker
     */
    public static boolean existPermission(@NonNull Context context, @NonNull String... permissions) {
        // ContextCompat.checkSelfPermission() could be ok, but not adapted to App Op.
        // could be better to user PermissionChecker
        for (String permission : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * After calling {@link Activity#requestPermissions(String[], int)},
     * you'll get {@link Activity#onRequestPermissionsResult(int, String[], int[])}.
     * Then check if the user has permissions for that.
     * If {@link #confirmPermissionResults(int...)} returns {@code false},
     * use {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity, String)}, which
     * returns {@code true} or {@code false}.
     *
     * @param grantResults int...
     * @return boolean
     */
    public static boolean confirmPermissionResults(@NonNull int... grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
