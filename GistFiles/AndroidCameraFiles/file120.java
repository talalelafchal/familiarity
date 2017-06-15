/* Looking to make your Ribbit app compatible with Android Marshmallow and above?
Go ahead and add these methods to MainActivity, don't worry about the errors for now: */

private boolean checkWriteExternalPermission() {
    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        // We do not have permission to write

        // Should we show an explanation? This method only returns true if the user has previously denied a request.
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Explain to the user why we need the permission, then prompt for it.
            // You can do this how you want, I just like snackbars :)
            showWriteToStorageSnackbar();
        } else {

            // No explanation needed, we can request the permission.

            requestWritePermissionWithCallback();

            // PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        // We don't have permission right now, but the user has been prompted.
        return false;
    }
    return true;
}

private void showWriteToStorageSnackbar() {
    Snackbar.make(mViewPager, "Write to storage is required to store and access photos/videos.",
            Snackbar.LENGTH_INDEFINITE)
            .setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestWritePermissionWithCallback();
                }
            }).show();
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
        case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Permission was granted, yay!

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                showWriteToStorageSnackbar();
            }
            break;
        default:
            Log.e(TAG, "Got request code: " + requestCode + " which is not used in switch.");
            break;
    }
}

private void requestWritePermissionWithCallback() {
    ActivityCompat.requestPermissions(MainActivity.this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
}





/* Then, at the end of onCreate() and also the start of onClick() for the Camera button in the Action Bar
(On the DialogInterface), add this method call: */

// Check permissions
checkWriteExternalPermission();





/* Next, add this constant to the top of your class: */

public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 6;
// This could be any number.





/* Finally, here are additional imports you will need: */
import android.Manifest;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;





/* Notes: You should have your ViewPager as a member variable at the top of the class,
do not remove the original permissions in AndroidManifest.xml to ensure backwards-compatibility,
asumming your activity is called MainActivity. /*