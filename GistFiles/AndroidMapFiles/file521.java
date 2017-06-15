import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestPermissionActivity extends Activity {

    private static final int REQUEST_GRANT_PERMISSION = 236;

    public static void launch(Activity activity, String[] permissions, PermissionsResultListener listener) {
        Intent intent = new Intent(activity, RequestPermissionActivity.class);
        intent.putExtra("permissions", permissions);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        sListener = listener;
    }

    public interface PermissionsResultListener {
        void onResult(Map<String, Integer> result);
    }

    @Nullable private static PermissionsResultListener sListener;

    private List<String> mPermissions;
    private List<String> mGrantedPermissions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissions = Arrays.asList(getIntent().getStringArrayExtra("permissions"));
        //all granted if api level < 23
        if (!VersionUtils.marshmallowOrLater()) {
            returnAllGranted();
            return;
        }
        //filter granted permission
        List<String> permissionsNotGranted = new ArrayList<>();
        mGrantedPermissions = new ArrayList<>();
        for (String permission : mPermissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission)
                    == PackageManager.PERMISSION_DENIED) {
                permissionsNotGranted.add(permission);
            } else {
                mGrantedPermissions.add(permission);
            }
        }

        if (permissionsNotGranted.size() == 0) {
            returnAllGranted();
            return;
        }

        ActivityCompat.requestPermissions(getActivity(),
                permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),
                REQUEST_GRANT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_GRANT_PERMISSION) return;
        Map<String, Integer> result = new HashMap<>();
        for (String permission : mPermissions) {
            boolean done = false;
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(permission)) {
                    result.put(permission, grantResults[i]);
                    done = true;
                    break;
                }
            }
            if (done) continue;

            if (mGrantedPermissions.contains(permission)) {
                result.put(permission, PackageManager.PERMISSION_GRANTED);
            } else {
                result.put(permission, PackageManager.PERMISSION_DENIED);
            }
        }
        if (sListener != null) {
            sListener.onResult(result);
            sListener = null;
        }
        finish();
        overridePendingTransition(0, 0);
    }

    private void returnAllGranted() {
        Map<String, Integer> result = new HashMap<>();
        for (String permission : mPermissions) {
            result.put(permission, PackageManager.PERMISSION_GRANTED);
        }
        if (sListener != null) {
            sListener.onResult(result);
            sListener = null;
        }
        finish();
        overridePendingTransition(0, 0);
    }
}
