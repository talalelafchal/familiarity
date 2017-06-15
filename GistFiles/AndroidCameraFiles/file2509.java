public class MainActivity extends Activity {
  private WebView myWebView;
  private String TAG = “MainActivity”;
  // in app/src/main/assets folder
  private String LOCAL_FILE = “file:///android_asset/test.html”;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Find the web view in our layout xml
    myWebView = (WebView) findViewById(R.id.webview);

    // Settings
    WebSettings webSettings = myWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccessFromFileURLs(true);
    webSettings.setAllowUniversalAccessFromFileURLs(true);

    // Add JS interface to allow calls from webview to Android
    // code. See below for WebAppInterface class implementation
    myWebView.addJavascriptInterface(new WebAppInterface(this), “Android”);

    // Set a web view client and a chrome client
    myWebView.setWebViewClient(new WebViewClient());
    myWebView.setWebChromeClient(new WebChromeClient() {
      // Need to accept permissions to use the camera and audio
      @Override
      public void onPermissionRequest(final PermissionRequest request) {
        Log.d(TAG, “onPermissionRequest”);
        MainActivity.this.runOnUiThread(new Runnable() {
          @TargetApi(Build.VERSION_CODES.LOLLIPOP)
          @Override
          public void run() {
    	    // Make sure the request is coming from our file
    	    // Warning: This check may fail for local files
    	    if(request.getOrigin().toString().equals(LOCAL_FILE)) {
              request.grant(request.getResources());
            } 
            else {
             	request.deny();
            }
          }
        });
      }
    });
    // Load the local HTML file into the webview
    myWebView.loadUrl(LOCAL_FILE);
  }

  // Interface b/w JS and Android code
  private class WebAppInterface {
    Context mContext;

    WebAppInterface(Context c) {
      mContext = c;
    }

    // This function can be called in our JS script now
    @JavascriptInterface
    public void showToast(String toast) {
      Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }
  }
}