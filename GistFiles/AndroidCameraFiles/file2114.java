public class FileUtil {

    private static final String APP_FOLDER_NAME = ".skillconnect";
    private static final String TAG = "FileUtil";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmmssms", Locale.getDefault());
    private static final String PREFIX = "IMG_";
    private static final String EXTENSION = ".jpg";
    private static final File dirPictures = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), APP_FOLDER_NAME);

    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";

    public static File createBufFileInExternalStorage(String filename) throws IOException {
        File bufFile = new File(dirPictures, filename);
        if (bufFile.exists()) {
            bufFile.delete();
        }
        return bufFile;
    }

    public static File copy(@NonNull final InputStream is, final File dst) {
        if (dst == null) {
            Log.d(TAG, "File destination can't be null");
            return null;
        }
        Log.d(TAG, "Copy(is,file) to " + dst.getAbsolutePath());
        try {
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        return dst;
    }

    public static File copy(@NonNull final Uri uri, final Context context, final File dst) {
        Log.d(TAG, "Copy(uri,file) from " + uri + " to " + dst.getAbsolutePath());
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is != null) {
                copy(is, dst);
            } else {
                Log.d(TAG, "Error copy from " + uri + " to " + dst.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error copy from " + uri + " to " + dst.getAbsolutePath());
            e.printStackTrace();
            return null;
        }

        return dst;
    }

    public static File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        try {
            return File.createTempFile(imageFileName, EXTENSION, albumF);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File createTempFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = PREFIX + timeStamp + "_";
        File cacheDir = context.getCacheDir();
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return new File(cacheDir, imageFileName + EXTENSION);
    }

    private static File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + APP_FOLDER_NAME);
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    Log.e(TAG, "failed to create directory");
                    return null;
                }
            }

        } else {
            Log.e(TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }
}