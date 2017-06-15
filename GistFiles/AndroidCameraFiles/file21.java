public class MyActivity
{
    public static ValueCallback<Uri[]> mFilePathCallback;
    public static Uri imageUri = null;
    
    // Get the object from xml file.
    protected WebView wvClient;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Web view debug. 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        
        // Add the Javascript interface for communication between this application and the server.
        this.wvClient.addJavascriptInterface(MyWebAppInterface, getString(R.string.app_name));
        // Setting Javascript can run on the website.
        this.wvClient.getSettings().setJavaScriptEnabled(true);
        // This is for playing a media on the webview.
        this.wvClient.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // Set a client for observe web site state.
        this.wvClient.setWebViewClient(new MyWebViewClient());
        // Set a chrome event for catching various events.
        this.wvClient.setWebChromeClient(new MyWebChromeClient());
        // Set a download listener for downloading a file.
        this.wvClient.setDownloadListener(new MyWebDownloadListener());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode)
        {
            switch (requestCode)
            {
                case 5566:
                    Uri[] results;
                    // If there is not data, then we may have taken a photo
                    if (null == data.getData())
                    {
                        results = new Uri[] {imageUri};
                    }
                    else
                    {
                        results = new Uri[] {Uri.parse(data.getDataString())};
                    }

                    mFilePathCallback.onReceiveValue(results);
                    mFilePathCallback = null;
                    break;
                default:
                    break;
            }
        }
        else if (RESULT_CANCELED == resultCode)
        {
            mFilePathCallback.onReceiveValue(new Uri[] { });
            mFilePathCallback = null;
        }
    }
}
