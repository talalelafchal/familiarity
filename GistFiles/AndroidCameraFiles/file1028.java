package me.johnweland.androidrtp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jweland on 12/11/2015.
 */
public class PermissionChecker {
    private static final String TAG = PermissionChecker.class.getSimpleName();
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 0;

    private MainActivity mainActivity;
    private static PermissionChecker instance = null;
    private PermissionChecker(MainActivity activity) {
        mainActivity = activity;
    }

    static public PermissionChecker getInstance(MainActivity activity) {
        if (instance == null) {
            instance = new PermissionChecker(activity);
            return instance;
        } else {
            return instance;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void permissionsCheck(){
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        // Add permission check for any permission that is not NORMAL_PERMISSIONS
        if(!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add(mainActivity.getString(R.string.permission_microphone));
        if(!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add(mainActivity.getString(R.string.permission_camera));

        if(permissionsList.size() > 0) {
            if(permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = mainActivity.getString(R.string.permission_grant_message) + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + "\n" +permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainActivity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
                mainActivity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            
        }
        mainActivity.demo();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mainActivity)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_ok_button_text, okListener)
                .setNegativeButton(R.string.dialog_cancel_button_text, null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (mainActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!mainActivity.shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

}
