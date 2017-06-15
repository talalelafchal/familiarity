package az.ey.myphotodropper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by elshadyarmetov on 11/17/15.
 */
public class EyPermissions {

    private static final int PERMISSION_REQUEST = 0;

    private AppCompatActivity activity;
    private String permission;
    private Runnable action = null;

    public EyPermissions(@NonNull AppCompatActivity activity, @NonNull String permission, Runnable action) {
        this.activity = activity;
        this.permission = permission;
        this.action = action;
    }

    public EyPermissions(@NonNull Activity activity, @NonNull String permission, Runnable action) {
        this.activity = (AppCompatActivity)activity;
        this.permission = permission;
        this.action = action;
    }

    public EyPermissions doOnce() {
        return this.doOnce(null);
    }

    public EyPermissions doOnce(Runnable action) {
        if (action != null) this.action = action;

        if (ActivityCompat.checkSelfPermission(this.activity, this.permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Toast.makeText(this.activity, "Permission was granted.", Toast.LENGTH_SHORT).show();
            this.startAction();
        } else {
            // Permission has not been granted and must be requested.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.CAMERA)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // Display a SnackBar with a button to request the missing permission.
                Toast.makeText(this.activity, "Permission is required.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this.activity, "Permission is not available. Requesting permission.", Toast.LENGTH_SHORT).show();
                // Request the permission. The result will be received in onRequestPermissionResult().
                ActivityCompat.requestPermissions(this.activity, new String[]{this.permission}, PERMISSION_REQUEST);
            }
        }

        return this;
    }

    public void onRequestPermission(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Toast.makeText(this.activity, "Permission was granted.", Toast.LENGTH_SHORT).show();
                this.startAction();
            } else {
                // Permission request was denied.
                Toast.makeText(this.activity, "Permission request was denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startAction() {
        if (this.action != null) {
            this.activity.runOnUiThread(this.action);
        } else {
            throw new Error("Action not defined");
        }
    }

}
