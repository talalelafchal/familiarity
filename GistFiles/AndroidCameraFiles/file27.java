/*VARIABLE DECLARATIONS*/
private static final int REQUEST_FILE_PERMISSIONS = 1;
private static final String[] FILE_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


///////////////USAGE BLOG//////////////

if(!hasPermissionsGranted(FILE_PERMISSION)){
                    requestFilePermissions();
 }else {
 //////DO WHAT YOU WANT TO DO
 }
///////////////USAGE BLOG//////////////
private void requestFilePermissions() {
        if (shouldShowRequestPermissionRationale(FILE_PERMISSION)) {
            new ConfirmationDialog().show(fragmentManager) ;
        } else {
            ActivityCompat.requestPermissions(getActivity(), FILE_PERMISSION, REQUEST_FILE_PERMISSIONS);
        }
    }
///////////////END OF USAGE BLOG//////////////

/*METHODS */
private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                return true;
            }
        }
        return false;
    }



@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FILE_PERMISSIONS) {
            if (grantResults.length == FILE_PERMISSION.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {

                        break;
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



public static class ConfirmationDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity parent = getActivity();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.photo_permission_request)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(parent, FILE_PERMISSION,
                                    REQUEST_FILE_PERMISSIONS);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                    .create();
        }

    }