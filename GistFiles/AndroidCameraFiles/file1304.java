public class MainActivity extends AppCompatActivity{
    List<String> permissionsList = new ArrayList<>();
    boolean askOnceAgain = false;
    Activity activity;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        checkPermissions();
    }
	
	@Override
    protected void onResume() {
		super.onResume();
		permissionsList.clear();
		if (askOnceAgain) {
			askOnceAgain = false;
			checkPermissions();
		}
    }
    
	private void showCustomDialog(String message, DialogInterface.OnClickListener listener) {
        new android.support.v7.app.AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 99: {
                boolean required = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                        required = true;
                    }
                }
                if (required) {
                    showCustomDialog("You need to allow access to some permissions.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    askOnceAgain = true;
                                }
                            });
                } else {

                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void checkPermissions() {
        int hasLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasCameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int hasPhonePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        List<String> permissions = new ArrayList<>();
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (hasPhonePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), 99);
        }

    }
}

/****************************for getting into fragment, add this in parent activity******************************************/

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    List<Fragment> fragments = getSupportFragmentManager().getFragments();
    if (fragments != null) {
        for (Fragment fragment : fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
