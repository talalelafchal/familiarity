public class MyWebChromeClient extends WebChromeClient
{
    // From javascript alert() function.
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result)
    {
        new AlertDialog.Builder(context(), R.style.MyAlertDialogStyle).setTitle("title")
                                                                      .setMessage(message)
                                                                      .setPositiveButton("ok", (dialog, which) -> result.confirm())
                                                                      .setCancelable(false)
                                                                      .create()
                                                                      .show();
        return true;
    }

    // From javascript confirm() function.
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
    {
        new AlertDialog.Builder(context(), R.style.MyAlertDialogStyle).setTitle("title")
                                                                      .setMessage(message)
                                                                      .setPositiveButton("ok", (dialog, which) -> result.confirm())
                                                                      .setNegativeButton("cancel", (dialog, which) -> result.cancel())
                                                                      .setCancelable(false)
                                                                      .create()
                                                                      .show();
        return true;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
    {
        // Directlt use this callback in anywhere.
        if (mFilePathCallback != null)
        {
            mFilePathCallback.onReceiveValue(new Uri[] { });
            mFilePathCallback = null;
        }
        mFilePathCallback = filePathCallback;

        // Create camera intent for taking a picture.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        if (file.exists())
        {
            file.delete();
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        }
        imageUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // Create intent for picking any data type of file.
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");

        // Create intent for picking a photo from the gallery
        Intent photosSelectionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an action");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePictureIntent, photosSelectionIntent});
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);

        startActivityForResult(chooserIntent, 5566);

        return true;
    }
}
