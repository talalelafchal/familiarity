/**
 * @see https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
 */
 
// SETTING VALUES
private static final Logger logger = LoggerFactory.getLogger(DefaultWindow.class);
private CommonDialogs mDialog;
private ValueCallback<Uri[]> mFilePathCallback;
private String mCameraPhotoPath;
public static final int INPUT_FILE_REQUEST_CODE = 1;

// CONSTRUCTOR
getWebView().getSettings().setJavaScriptEnabled(true);
getWebView().getSettings().setDomStorageEnabled(true);

getWebView().setWebChromeClient(new UserWebChromClient(context));
getWebView().setWebViewClient(new UserWebViewClient(context, this));

// METHODS
private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File imageFile = File.createTempFile(imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
    );
    return imageFile;
}

private class UserWebChromClient extends WebChromeClient {
    public UserWebChromClient(Context context) {
        super(context);
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
            WebChromeClient.FileChooserParams fileChooserParams) {

        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }

        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[] {takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        activity.startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

        return true;
    }
}

private class UserWebViewClient extends WebViewClient {
    public UserWebViewClient(Context context, WebInterface webWindow) {
        super(context, webWindow);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true; // super.shouldOverrideUrlLoading(view, url);
    }
}

// CALLBACK
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
        return;
    }

    logger.debug("===================================================================");
    logger.debug("RECEIVED ACTIVITY RESULT ");
    logger.debug("===================================================================");

    Uri[] results = null;

    // Check that the response is a good one
    if(resultCode == Activity.RESULT_OK) {
        if(data == null) {
            // If there is not data, then we may have taken a photo
            if(mCameraPhotoPath != null) {
                results = new Uri[]{Uri.parse(mCameraPhotoPath)};
            }
        } else {
            String dataString = data.getDataString();
            if (dataString != null) {
                results = new Uri[]{Uri.parse(dataString)};
            }
        }
    }

    mFilePathCallback.onReceiveValue(results);
    mFilePathCallback = null;
}