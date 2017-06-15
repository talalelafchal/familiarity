import com.aitorvs.android.allowme.AllowMe;
import com.aitorvs.android.allowme.AllowMeCallback;
import com.aitorvs.android.allowme.PermissionResultSet;

public class PermissionHelper {
    public static void runIfPossible(final String permission, final Runnable callback) {
        if (!AllowMe.isPermissionGranted(permission)) {
            new AllowMe.Builder()
                    .setPermissions(permission)
                    .setCallback(new AllowMeCallback() {
                        @Override
                        public void onPermissionResult(int requestCode, PermissionResultSet result) {
                            if (result.isGranted(permission)) {
                                callback.run();
                            }
                        }
                    })
                    .request(123);
        } else {
            callback.run();
        }
    }
}