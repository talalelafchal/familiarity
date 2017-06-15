
public class TwitterDialog extends Dialog {

    static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    
    static float[] DIMENSIONS_PORTRAIT_1;
    
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                         						ViewGroup.LayoutParams.MATCH_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    private String mUrl;
    private TwDialogListener mListener;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;

    private static final String TAG = "Twitter-WebView";
    
    public TwitterDialog(Context context, String url, TwDialogListener listener) {
        super(context);
        
        mUrl 		= url;
        mListener 	= listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setCanceledOnTouchOutside(false);
        
        setOnCancelListener(new OnCancelListener()
		{
			
			@Override
			public void onCancel(DialogInterface dialog)
			{
				// TODO Auto-generated method stub
				mListener.onError("cancel");
			}
		});

        mSpinner = new ProgressDialog(getContext());
        mSpinner.setCanceledOnTouchOutside(false);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        
        mContent.setOrientation(LinearLayout.VERTICAL);
        
        setUpTitle();
        setUpWebView();
        
        Display display 	= getWindow().getWindowManager().getDefaultDisplay();
        final float scale 	= getContext().getResources().getDisplayMetrics().density;
        
        float tempWidth, tempHeight;
        if (Global.isTablet(getContext())) {
        	tempWidth =  400;
        	tempHeight = 640;
        	DIMENSIONS_PORTRAIT_1 = new float[]{tempWidth, tempHeight};
        	@SuppressWarnings("deprecation")
    		float[] dimensions 	= (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT_1 : DIMENSIONS_LANDSCAPE;
            
            addContentView(mContent, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f),
            							(int) (dimensions[1] * scale + 0.5f)));
        } else {
        	@SuppressWarnings("deprecation")
    		float[] dimensions 	= (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
            
            addContentView(mContent, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f),
            							(int) (dimensions[1] * scale + 0.5f)));
        }
        
        
    }
    
   

    private void setUpTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Drawable icon = mContent.getResources().getDrawable(R.drawable.ic_launcher);
        
        mTitle = new TextView(getContext());
        
        mTitle.setText("Twitter");
        mTitle.setTextColor(Color.WHITE);
        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mTitle.setBackgroundColor(0xFFbbd7e9);
        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
        mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        
        mContent.addView(mTitle);
    }

    private void setUpWebView() {
        mWebView = new WebView(getContext());
        
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new TwitterWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        
        mContent.addView(mWebView);
    }

    private class TwitterWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	Log.d(TAG, "Redirecting URL " + url);
        	
        	if (url.startsWith(TwitterHelper.CALLBACK_URL)) 
        	{
        		
        		System.out.println("if condition...");
        		mListener.onComplete(url);
        		
        		TwitterDialog.this.dismiss();
        		
        		return true;
        	}  
        	else if (url.startsWith("authorize")) 
        	{
        		System.out.println("else return condition...");
        		return false;
        	}
        	
            return true;
        }
        
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
        		SslError error) {
        	// TODO Auto-generated method stub
        	super.onReceivedSslError(view, handler, error);
        	Log.e("log_tag", "onReceivedSslError");
        	handler.proceed();
        	view.reload();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        	Log.d(TAG, "Page error: " + description);
        	Log.e("log_tag", "onReceivedError");
        	
            super.onReceivedError(view, errorCode, description, failingUrl);
      
            mListener.onError(description);
            
            TwitterDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "Loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }
            mSpinner.dismiss();
        }

    }
}
