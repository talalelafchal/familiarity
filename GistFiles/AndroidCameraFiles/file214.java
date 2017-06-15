private Uri outputFileUri;

private void openImageIntent() {

// Determine Uri of camera image to save.
final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
root.mkdirs();
final String fname = Utils.getUniqueImageFilename();
final File sdImageMainDirectory = new File(root, fname);
outputFileUri = Uri.fromFile(sdImageMainDirectory);

    // Camera.
    final List<Intent> cameraIntents = new ArrayList<Intent>();
    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    final PackageManager packageManager = getPackageManager();
    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
    for(ResolveInfo res : listCam) {
        final String packageName = res.activityInfo.packageName;
        final Intent intent = new Intent(captureIntent);
        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        intent.setPackage(packageName);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        cameraIntents.add(intent);
    }

    // Filesystem.
    final Intent galleryIntent = new Intent();
    galleryIntent.setType("image/*");
    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

    // Chooser of filesystem options.
    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

    // Add the camera options.
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

    startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
    if(resultCode == RESULT_OK)
    {
        if(requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE)
        {
            final boolean isCamera;
            if(data == null)
            {
                isCamera = true;
            }
            else
            {
                final String action = data.getAction();
                if(action == null)
                {
                    isCamera = false;
                }
                else
                {
                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }

            Uri selectedImageUri;
            if(isCamera)
            {
                selectedImageUri = outputFileUri;
            }
            else
            {
                selectedImageUri = data == null ? null : data.getData();
            }
        }
    }
}