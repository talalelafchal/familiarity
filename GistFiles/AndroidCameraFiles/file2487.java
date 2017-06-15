package ar.com.wolox.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class PermissionUtils {

    private static int sRequestCount = 1;
    private static HashMap<Integer, PermissionsListener> sRequestListeners = new HashMap<>();

    public static boolean requirePermission(Fragment fragment, PermissionsListener listener, String... permissions) {
        String[] ungrantedPermissions = filterUngranted(fragment.getActivity(), permissions);
        if (ungrantedPermissions.length > 0) {
            fragment.requestPermissions(ungrantedPermissions, sRequestCount);
            sRequestListeners.put(sRequestCount++, listener);
            return false;
        }
        listener.onPermissionsGranted();
        return true;
    }

    public static boolean requirePermission(Activity activity, PermissionsListener listener, String... permissions) {
        String[] ungrantedPermissions = filterUngranted(activity, permissions);
        if (ungrantedPermissions.length > 0) {
            ActivityCompat.requestPermissions(activity, ungrantedPermissions, sRequestCount);
            sRequestListeners.put(sRequestCount++, listener);
            return false;
        }
        listener.onPermissionsGranted();
        return true;
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, final int[] grantResults) {
        final PermissionsListener listener = sRequestListeners.get(requestCode);
        if(listener != null) {
            sRequestListeners.remove(requestCode);
            if (allGranted(grantResults)) {
                // Workaround to Android bug: https://goo.gl/OwseuO
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPermissionsGranted();
                    }
                });
            } else {
                listener.onPermissionsDenied();
            }
        }
    }

    private static String[] filterUngranted(Context context, String... permissions) {
        ArrayList<String> ungranted = new ArrayList<>();
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ungranted.add(permission);
            }
        }
        return ungranted.toArray(new String[ungranted.size()]);
    }

    private static boolean allGranted(int[] results) {
        if(results.length < 1) return false;
        boolean granted = true;
        for(int result : results) {
            granted = granted && result == PackageManager.PERMISSION_GRANTED;
        }
        return granted;
    }

    public static abstract class PermissionsListener {
        public abstract void onPermissionsGranted();
        public void onPermissionsDenied() {};
    }
}