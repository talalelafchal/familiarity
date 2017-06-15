/**
 * Copyright (C) 2016 Mikhael LOPEZ
 * Licensed under the Apache License Version 2.0
 */
public class SampleActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //...
    
    if (PermissionsUtils.checkAndRequest(this, Manifest.permission.YOUR_PERMISSION,
                PermissionsUtils.MY_PERMISSIONS_REQUEST_EXAMPLE, "Explain here why the app needs permissions", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // YOUR CANCEL CODE
                    }
                })) {
      // YOUR BASE METHOD
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case PermissionsUtils.MY_PERMISSIONS_REQUEST_EXAMPLE:
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permission was granted
          // YOUR BASE METHOD
        } else {
          // permission denied
          boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
          if (!showRationale) {
            // user denied flagging NEVER ASK AGAIN
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("WHY THIS PERM IS MANDATORY TO USED THIS APP")
                .setPositiveButton(getResources().getString(android.R.string.ok), (dialog, which) -> {
                    PermissionsUtils.startInstalledAppDetailsActivity(getActivity());
                    getActivity().finish();
                }).setCancelable(false).show();
           } else  {
              // user denied WITHOUT never ask again
              // YOUR CANCEL CODE
            }
        }
        break;
    }
  }

}