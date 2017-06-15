
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tuanchauict on 3/8/16.
 * <p/>
 * This Activity helps the permission request easier than using the sample code of Android Training (https://developer.android.com/training/permissions/requesting.html).
 */
public class PermissionActivity extends AppCompatActivity {
    private static int sLastRequestCode = 0;
    private static SparseArray<RequestPermissionsStorage> sMapCallbacks;
    private List<RequestPermissionsStorage> mWaitingCallbackResume;
    private List<RequestPermissionsStorage> mWaitingCallbackPostResume;

    @Override
    protected void onResume() {
        super.onResume();
        if (mWaitingCallbackResume != null) {
            for (RequestPermissionsStorage c : mWaitingCallbackResume) {
                c.callback.onResult(c.permissions, c.realRequestPermissions, c.granted);
            }
            mWaitingCallbackResume.clear();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (mWaitingCallbackPostResume != null) {
            for (RequestPermissionsStorage c : mWaitingCallbackPostResume) {
                c.callback.onResult(c.permissions, c.realRequestPermissions, c.granted);
            }
            mWaitingCallbackPostResume.clear();
        }
    }

    private static int nextRequestCode() {
        int result = (sLastRequestCode + 1) % 256;
        sLastRequestCode = result;
        return result;
    }

    public void requestPermission(@NonNull RequestPermissionsCallback callback, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            callback.onResult(permissions, null, true);
            return;
        }
        List<String> shouldRequestPermissions = new ArrayList<>(permissions.length);
        for (int i = 0; i < permissions.length; i++) {
            String p = permissions[i];
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                shouldRequestPermissions.add(p);
            }
        }
        if (shouldRequestPermissions.isEmpty()) {
            callback.onResult(permissions, null, true);
        } else {
            int requestCode = nextRequestCode();
            if (sMapCallbacks == null) {
                sMapCallbacks = new SparseArray<>();
            }
            sMapCallbacks.put(requestCode, new RequestPermissionsStorage(permissions, callback));
            String[] arr = new String[shouldRequestPermissions.size()];

            ActivityCompat.requestPermissions(this, shouldRequestPermissions.toArray(arr), requestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode > 255){
            //fragment requested permission, not activity
            return;
        }
        System.out.println("Con heo: requestCode = " + requestCode + "  for " + Arrays.toString(permissions) + "  granted: " + Arrays.toString(grantResults));
        if (sMapCallbacks != null) {
            RequestPermissionsStorage storage = sMapCallbacks.get(requestCode);
            if (storage != null) {
                boolean granted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                        break;
                    }
                }
                storage.granted = granted;
                storage.realRequestPermissions = permissions;

//                storage.callback.onResult(storage.permissions, permissions, granted);
                RequestPermissionsCallback callback = storage.callback;
                if (callback instanceof RequestPermissionsCallbackResume) {
                    if (mWaitingCallbackResume == null) {
                        mWaitingCallbackResume = new ArrayList<>();
                    }
                    mWaitingCallbackResume.add(storage);
                } else if (callback instanceof RequestPermissionsCallbackPostResume) {
                    if (mWaitingCallbackPostResume == null) {
                        mWaitingCallbackPostResume = new ArrayList<>();
                    }
                    mWaitingCallbackPostResume.add(storage);
                } else {
                    storage.callback.onResult(storage.permissions, permissions, granted);
                }
            }
            sMapCallbacks.remove(requestCode);

        }
    }

    private static class RequestPermissionsStorage {
        RequestPermissionsCallback callback;
        String[] permissions;
        String[] realRequestPermissions;
        boolean granted;

        public RequestPermissionsStorage(String[] permission, RequestPermissionsCallback callback) {
            this.permissions = permission;
            this.callback = callback;
        }
    }

    /**
     * This callback will be called intermediately when the result of the activity is called.
     */
    public interface RequestPermissionsCallback {
        /**
         * @param requestPermissions
         * @param realRequestPermissions : real permissions that are used for request permissions. Maybe @null if no permissions needed to be requested
         * @param granted                : default true for android version < 23.
         */
        void onResult(@NonNull String[] requestPermissions, String[] realRequestPermissions, boolean granted);
    }

    /**
     * This kind of callback will be hold until the onResume of the activity is called in case the
     * permissions are needed to request
     */
    public interface RequestPermissionsCallbackResume extends RequestPermissionsCallback {
    }

    /**
     * This kind of callback will be hold until the onPostResume of the activity is called in case
     * the permissions are needed to request
     */
    public interface RequestPermissionsCallbackPostResume extends RequestPermissionsCallback {
    }
}