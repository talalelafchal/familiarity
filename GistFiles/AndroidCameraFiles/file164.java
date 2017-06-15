mLayout = findViewById(R.id.fullscreen_content);

if (ContextCompat.checkSelfPermission(mContext,
        android.Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {

    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
            android.Manifest.permission.CAMERA)) {

        // Show an expanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

        Snackbar.make(mLayout, "Camera access is required to attach pictures to tasks.",
                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request the permission
                ActivityCompat.requestPermissions(FullscreenActivity.this,
                        new String[]{android.Manifest.permission.CAMERA},
                        159);
            }
        }).show();


    } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions((Activity)mContext,
                new String[]{android.Manifest.permission.CAMERA},
                159);

        // 159 is an app-defined int constant. The callback method gets the
        // result of the request.
    }
}