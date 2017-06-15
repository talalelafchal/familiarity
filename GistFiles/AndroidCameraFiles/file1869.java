/**
 * Created by johnshelley on 4/10/14.
 */
public class CameraHelper {

    static final int REQUEST_TAKE_PHOTO = 11111;

    public static Uri openImageIntent(Activity activity) {

        Uri outputFileUri;

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Bawte");
        root.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_.jpg";
        final File sdImageMainDirectory = new File(root, imageFileName);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
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
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[]{}));

        // Calling activity should execute:
        (activity).startActivityForResult(chooserIntent, REQUEST_TAKE_PHOTO);
        return outputFileUri;
    }

    /**
     * Creates the image file to which the image must be saved.
     *
     * @return
     * @throws java.io.IOException
     */
    public static File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        if (activity.getClass() == SearchActivity.class) {
//            if (((SearchActivity) activity).getPhotoType().equals("product")) {
//                ((SearchActivity) activity).setmCurrentProdPhotoPath("file:" + image.getAbsolutePath());
//            } else if (((SearchActivity) activity).getPhotoType().equals("receipt")) {
//                ((SearchActivity) activity).setmCurrentRecPhotoPath("file:" + image.getAbsolutePath());
//            }
//        } else if (activity.getClass() == ProductDetailActivity.class) {
//            ((ProductDetailActivity) activity).setCurrentPhotoPath("file:" + image.getAbsolutePath());
//        }
        return image;
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    public static void addPhotoToGallery(Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = null;
        if (activity.getClass() == ProductDetailActivity.class) {
            f = new File(((ProductDetailActivity) activity).getCurrentPhotoPath());
        } else if (activity.getClass() == ProfileActivity.class) {
            f = new File(((ProfileActivity) activity).getCurrentPhotoPath());
        }
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    public static void addPhotoToGallery(Activity activity, Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = uri;
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    /**
     * Scale the photo down and fit it to our image views.
     * <p/>
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    public static void setFullImageFromFilePath(String imagePath, ImageView imageView, ImageView targetSizeView) {
        // Get the dimensions of the View
        int targetW = targetSizeView.getWidth();
        int targetH = targetSizeView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor / 2;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }


    /**
     * Scale the photo down for sending to server.
     * <p/>
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    public static String shrinkFullImage(String imagePath, Context context) {
        final int maxSize = 1000;
        Bitmap resizedBM = null;
        int targetW;
        int targetH;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Landscape vs portrait
        if (photoW > photoH) {
            targetW = maxSize;
            targetH = (photoH * maxSize) / photoW;
        } else {
            targetH = maxSize;
            targetW = (photoW * maxSize) / photoH;
        }

        final int minSideLength = Math.min(targetW, targetH);
        bmOptions.inSampleSize = computeSampleSize(bmOptions, minSideLength,
                targetW * targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inDither = false;
        bmOptions.inScaled = false;
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        bmOptions.inInputShareable = true;
        bmOptions.inPurgeable = true;

        try {
            resizedBM = BitmapFactory.decodeFile(imagePath, bmOptions);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(imagePath);
            resizedBM.compress(Bitmap.CompressFormat.JPEG, 100, out);
            resizedBM.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}