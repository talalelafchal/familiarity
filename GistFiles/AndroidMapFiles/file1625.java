import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class that wraps access to the runtime permissions API in M and provides basic helper
 * methods.
 */
public abstract class PermissionUtil {

    /**
     * Id to identify a permission setting request
     */
    public static final int REQUEST_PERMISSION_SETTING = 90;

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Call this to tell the user why we need the permission, and redirect him to the settings if
     * click on "Ok"
     *
     * @param activity the current activity
     * @param text     the text to explain why we need this permission (begin with without_permission_)
     */
    public static void displayPermissionTotallyRefusedAlert(final Activity activity, String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        alertDialog.setTitle(R.string.permission_required);
        alertDialog.setMessage(text);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        openSettings(activity);
                    }
                });
        alertDialog.show();
    }

    /**
     * Call this to give an explanation about the permission asked after the user refuse it.<br>
     * It will ask the user if he wants to have the possibility to give the permission ("Retry")
     * or not ("I am sure")
     *
     * @param activity the current activity
     * @param text     the text to display (begin with need_permission_). We add "ask_if_sure_to_deny_permission" question text at the end.
     * @param callback the PermissionCallback.<br>
     *                 retry() is called when the user wants to have the possibility to give the permission.<br>
     *                 refused() is called when the user does not want to give the permission at all.
     */
    public static void displayPermissionInfoMessage(Activity activity, String text, final PermissionCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        alertDialog.setTitle(R.string.error);
        alertDialog.setMessage(text + " " + activity.getString(R.string.ask_if_sure_to_deny_permission));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.retry),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        callback.retry();
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.i_am_sure),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        callback.refused();
                    }
                });
        alertDialog.show();
    }

    /**
     * Create and start an intent to redirect the user to the app settings. The user need to go to the "Permissions" section
     * and enable the asked permissions.
     *
     * @param activity
     */
    public static void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    public static void displayLocationSettingsAlert(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        alertDialog.setTitle(activity.getBaseContext().getText(R.string.near_resto_popup_gps_title));
        alertDialog.setMessage(activity.getBaseContext().getText(R.string.near_resto_popup_gps_content));
        alertDialog.setPositiveButton(activity.getBaseContext().getText(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton(activity.getBaseContext().getText(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * !!! Use this function only for notificaitons like toast and snackbar. Because below
     * KITKAT, it will always return false to force use a custom view instead the toast.
     * @param context the current context.
     * @return true if the notifications are enabled and we can display a toast.
     */
    public static boolean isNotificationEnabled(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            //Source: http://stackoverflow.com/questions/11649151/android-4-1-how-to-check-notifications-are-disabled-for-the-application/30108004#30108004

            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);


            ApplicationInfo appInfo = context.getApplicationInfo();

            String pkg = context.getApplicationContext().getPackageName();

            int uid = appInfo.uid;

            Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

            try {

                appOpsClass = Class.forName(AppOpsManager.class.getName());

                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);

                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        // We don't have any way to found if notification are enable before KITKAT. So we consider it's not.
        return false;
    }

}