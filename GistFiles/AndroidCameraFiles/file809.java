public class PhotoSelector implements MediaSelector {

    private Uri outputFileUri;
    private Context context;

    public PhotoSelector(Context context) {
        this.context = context;
    }

    @Override
    public Intent getIntentChooser() {
        outputFileUri = createNewOutputFileUri();

        List<Intent> cameraIntents = getCameraIntents(context);
        Intent galleryIntent = getGalleryIntent();

        final Intent chooserIntent = Intent.createChooser(galleryIntent,
                context.getString(R.string.media_sharing_select_picture_title));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));

        return chooserIntent;
    }

    private Uri createNewOutputFileUri() {
        final File root = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator);
        root.mkdirs();
        final String fname = generateUniqueFilename();
        final File sdImageMainDirectory = new File(root, fname);
        return Uri.fromFile(sdImageMainDirectory);
    }

    private String generateUniqueFilename() {
        return "img_" + System.currentTimeMillis() + ".jpg";
    }

    private List<Intent> getCameraIntents(Context context) {
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        return cameraIntents;
    }

    private Intent getGalleryIntent() {
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);
        return galleryIntent;
    }

    @Override
    public Uri getMediaUriFromActivityResult(int requestCode, Intent data) {
        final boolean isCamera;
        if (data == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            if (action == null) {
                isCamera = false;
            } else {
                isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }

        Uri selectedImageUri;
        if (isCamera) {
            selectedImageUri = outputFileUri;
        } else {
            selectedImageUri = data == null ? null : data.getData();
        }

        return selectedImageUri;
    }

}
