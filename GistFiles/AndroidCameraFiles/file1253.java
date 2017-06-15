public class MainActivity extends Activity {

    private Permissions mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissions = Permissions.with(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.open_camera) {
            openCamera();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
       mPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openCamera() {
        if (mPermissions.canAccessCamera()) {
            // Show Camera
        } else {
            mPermissions.askCameraPermission(new Permissions.PermissionListener() {
                @Override
                public void permissionResult(boolean granted) {
                    if (granted) {
                        openCamera();
                    }
                }
            });
        }
    }
}
