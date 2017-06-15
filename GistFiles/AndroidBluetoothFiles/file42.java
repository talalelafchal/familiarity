private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 0;

/* Required by Android M for scanning bluetooth devices in background
 * http://stackoverflow.com/questions/33142034/android-6-bluetooth
 */
private void requestPermissionsIfNeeded() {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        return;
    }

    int permissionsNeeded = 0;

    final List<String> permissionsList = new ArrayList<>();
    if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
        permissionsNeeded++;
    if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
        permissionsNeeded++;
    if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        permissionsNeeded++;

    if (permissionsList.size() > 0) {
        if (permissionsNeeded > 0) {
            mSnacks.add(Snackbar.make(mEmbeddedContentContainer, R.string.permissions_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(DatecsPrintShareActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }
                    }));
            return;
        }
        ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }
}

private boolean addPermission(List<String> permissionsList, String permission) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        return true;
    }

    if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        permissionsList.add(permission);
        // Check for Rationale Option
        return !ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }
    return true;
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        mSnacks.add(Snackbar.make(mEmbeddedContentContainer, R.string.permissions_required_but_not_granted, Snackbar.LENGTH_LONG));
    }
}