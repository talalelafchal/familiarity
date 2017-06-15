import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.sanmiao.genuine.utils.LLog;
import com.sanmiao.genuine.utils.ToastUtil;

/**
 * @author 刚桥恕
 * @功能描述 描述
 * @date 2016/9/28 0028.
 */

public class PermissionUtils {

    private static PermissionsManager pm = PermissionsManager.getInstance();

    public interface OnPermissionResult {
        void onGranted();
//        void onDenied();
    }

    private static void permission(final Activity activity, final String permission, final String permissionExplain, final OnPermissionResult callback){
        if(pm.hasPermission(activity, permission)){
            callback.onGranted();
            return;
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            new AlertDialog.Builder(activity)
                    .setTitle(permissionExplain)
                    .setPositiveButton("给予权限", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermission(activity, permission, callback);
                            LLog.d(getClass(), "click ok");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return;
        }
        requestPermission(activity, permission, callback);
    }

    private static void requestPermission(final Activity activity, String permission, final OnPermissionResult callback){
        LLog.d("*-*", "requesting");
        pm.requestPermissionsIfNecessaryForResult(activity, new String[]{permission}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                if(callback != null){
                    LLog.d(getClass(), "granted");
                    callback.onGranted();
                }
            }

            @Override
            public void onDenied(String permission) {
                LLog.d(getClass(), "denied");
                ToastUtil.showShort(activity, "您拒绝了权限申请，无法启动相关组件");
            }
        });
    }

    public static void phone(final Activity activity, final OnPermissionResult callback) {
        permission(
                activity,
                Manifest.permission.CALL_PHONE,
                "您需要同意电话权限以使用相关功能",
                callback
        );
    }

    public static void storage(final Activity activity, final OnPermissionResult callback) {
        permission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                "您需要同意内存卡权限以使用相关功能",
                callback
        );
    }

    public static void contacts(final Activity activity, final OnPermissionResult callback) {
        permission(
                activity,
                Manifest.permission.GET_ACCOUNTS,
                "您需要同意联系人权限以使用相关功能",
                callback
        );
    }

    public static void camera(final Activity activity, final OnPermissionResult callback) {
        permission(
                activity,
                Manifest.permission.CAMERA,
                "您需要同意相机权限以使用相关功能",
                callback
        );
    }

    public static void location(final Activity activity, final OnPermissionResult callback) {
        permission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                "您需要同意定位权限以使用相关功能",
                callback
        );
    }
}
