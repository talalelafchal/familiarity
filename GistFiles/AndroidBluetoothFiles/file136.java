public final class PermissionsUtils {

    private static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCameraGranted(Context context) {
        return checkPermission(context, Manifest.permission.CAMERA);
    }

    public static boolean isStorageGranted(Context context) {
        return checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean isBlueToothGranted(Context context) {
        return checkPermission(context, Manifest.permission.BLUETOOTH);
    }

    public static boolean isLocationGranted(Context context) {
        boolean success = true;
        if (!checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
            success = false;

        if (!checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
            success = false;
        return success;
    }

    public static void requestPermissions(Object o, int permissionId, String... permissions) {
        if (o instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, permissionId);
        }
    }
}
