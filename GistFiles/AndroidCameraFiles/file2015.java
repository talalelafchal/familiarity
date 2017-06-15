public class BarcodeLoggerActivity extends Activity {
    
    private ActivityPermissionDelegate permissionDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        PermissionRequest request = new PermissionRequest(
                "camera",
                new PermissionCallback() {
                    @Override
                    public void handleGranted() {
                        startBarcodeCapture();
                    }

                    @Override
                    public void handleRationale(final PermissionRationaleRetryBehavior behavior) {
                        fragment.showPermissionDetails(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                behavior.requestAgain();
                            }
                        });
                    }

                    @Override
                    public void handleDenied() {
                        fragment.showPermissionDetails(null);
                    }
                },
                android.Manifest.permission.CAMERA
        );

        permissionDelegate = new ActivityPermissionDelegate(this, new PermissionRequest[]{request});

        if (savedInstanceState == null) {
            permissionDelegate.requestPermission("camera");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!permissionDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}