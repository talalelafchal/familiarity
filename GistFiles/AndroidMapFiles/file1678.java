import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Tools to print notifications to the user
 * - Toast and snackbar will never appear if the notifications are disabled for the app
 * - AlertDialog appears
 * So we check if the notifications are disabled. If it is, we use an AlertDialog with juste a “ok” button.
 */
public final class Notification {

    /**
     * Display a toast if possible, an alert dialog otherwise
     * @param context The current context
     * @param message The message to display
     */
    public static void show(Context context, String message) {

        if (PermissionUtil.isNotificationEnabled(context)) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
        else {
            showSimpleAlertDialog(context, message);
        }

    }

    /**
     * Display a toast with custom gravity and position if possible, an alert dialog otherwise
     * @param context The current context
     * @param message The message to display
     */
    public static void showWithGravity(Context context, String message, int gravity, int xOffset, int yOffset) {
        if (PermissionUtil.isNotificationEnabled(context)) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, xOffset, yOffset);
            toast.show();
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
        else {
            showSimpleAlertDialog(context, message);
        }
    }

    /**
     * Display an alert dialog with only an "ok" button
     * @param context The current context
     * @param message The message to display
     */
    private static void showSimpleAlertDialog(Context context, String message) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle("")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();

            // This exception occurred if the context is not good (do not use getApplicationContext() but Class.this)
            // Also, the activity must have android:theme="@style/AppTheme" defined on the AndroidManifest
            if (Constants.DEV_MODE) {
                throw new AssertionError("Invalid context to display the alert dialog");
            }
        }
    }
}