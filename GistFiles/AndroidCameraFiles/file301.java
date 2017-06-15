    @Override
    protected void onHandleIntent(Intent intent) {
        this.nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int jobId = intent.getIntExtra(JOB_ID_EXTRA, -1);
        QypeModel model = intent.getParcelableExtra(MODEL_EXTRA);
        if (model instanceof Place) {
            this.uploadStrategy = new PlacePhotoUploadStrategy((Place) model);
        } else if (model instanceof User) {
            this.uploadStrategy = new UserPhotoUploadStrategy((User) model);
        } else {
            throw new IllegalArgumentException("Can only upload place or user photos");
        }

        Uri imageUri = intent.getData();
        File imageFile = null;

        if (imageUri == null) {
            // the picture was just taken by the camera and written to a tmp file
            imageFile = migrateTempFile(CameraSupport.pathToCameraTempFile(this));

        } else if (ContentResolver.SCHEME_FILE.equals(imageUri.getScheme())) {
            // the picture was just taken by the camera and written to a tmp file, but the Camera
            // app reported the file path (Sony/Ericsson devices do that)
            imageFile = migrateTempFile(imageUri.getSchemeSpecificPart());

        } else if (ContentResolver.SCHEME_CONTENT.equals(imageUri.getScheme())) {
            // the picture came from the Gallery app, so we need to query for meta data
            String[] select = { Media.DATA };
            Cursor results = getContentResolver().query(imageUri, select, null, null, null);
            results.moveToNext();
            imageFile = new File(results.getString(0));
            results.close();
        } else {
            throw new IllegalStateException("Unsupported photo URI: " + imageUri);
        }

        try {
            if (!imageFile.exists()) {
                throw new IllegalImageFile();
            }

            uploadPhoto(jobId, imageFile);
        } catch (Exception e) {
            handleError(jobId, e);
        }
    }