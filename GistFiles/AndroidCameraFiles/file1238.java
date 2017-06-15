public class Util {
    public static Intent getTakePictureIntent(final Context context) {
        if(context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
                Toast.makeText(context, context.getString(R.string.error_no_camera), Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }
}