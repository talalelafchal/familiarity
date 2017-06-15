import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;


public class Permissions {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int LOCATION_REQUEST_CODE = 2;

    private Activity mActivity;
    private Context mContext;
    private SparseArray<PermissionListener> mPermissionListeners;

    public static Permissions with(Activity activity) {
        return new Permissions(activity);
    }

    public static Permissions with(Context context) {
        return new Permissions(context);
    }

    private Permissions(Activity activity) {
        mActivity = activity;
        mContext = activity;
        mPermissionListeners = new SparseArray<>();
    }

    private Permissions(Context context) {
        mContext = context;
    }

    private boolean hasPermission(String perm) {
        return ContextCompat.checkSelfPermission(mContext, perm) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean canAccessLocation() {
        return hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public boolean canAccessCamera() {
        return hasPermission(android.Manifest.permission.CAMERA);
    }

    public void askCameraPermission(PermissionListener permissionListener) {
        askPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE, permissionListener);
    }

    public void askLocationPermission(PermissionListener permissionListener) {
        askPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE, permissionListener);
    }

    private void askPermission(String permission, int requestCode, PermissionListener permissionListener) {
        if (mActivity != null) {
            mPermissionListeners.append(requestCode, permissionListener);
            ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mPermissionListeners != null) {

            PermissionListener permissionListener = mPermissionListeners.get(requestCode);
            if (permissionListener != null) {
                permissionListener.permissionResult(grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }

        }
    }


    public interface PermissionListener {
        void permissionResult(boolean granted);
    }

}