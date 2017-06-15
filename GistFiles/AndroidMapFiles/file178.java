...        
        
    @Override
    public void onCreate() {
    
    ...
    
    // Volley networking setup
    requestQueue = Volley.newRequestQueue(this, getHttpClientStack());
    imageLoader = new ImageLoader(requestQueue, getBitmapCache());
    VolleyLog.setTag(TAG);

    // http://stackoverflow.com/a/17035814
    imageLoader.setBatchedResponseDelay(0);
    
    ...
    
  }
    
    ...
    
  public static HttpStack getHttpClientStack() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        HurlStack stack = new HurlStack() {
            @Override
            public HttpResponse performRequest(Request<?> request, Map<String, String> headers)
                    throws IOException, AuthFailureError {

                if (request.getUrl() != null && StringUtils.getHost(request.getUrl()).endsWith("files.wordpress.com")) {
                    // Add the auth header to access private WP.com files
                    HashMap<String, String> authParams = new HashMap<String, String>();
                    authParams.put("Authorization", "Bearer " + getWPComAuthToken(mContext));
                    headers.putAll(authParams);
                }

                HashMap<String, String> defaultHeaders = new HashMap<String, String>();
                defaultHeaders.put("User-Agent", "wp-android/" + WordPress.versionName);
                headers.putAll(defaultHeaders);

                return super.performRequest(request, headers);
            }
        };

        return stack;

    } else {
        HttpClientStack stack = new HttpClientStack(AndroidHttpClient.newInstance("volley/0")) {
            @Override
            public HttpResponse performRequest(Request<?> request, Map<String, String> headers)
                    throws IOException, AuthFailureError {

                if (request.getUrl() != null && StringUtils.getHost(request.getUrl()).endsWith("files.wordpress.com")) {
                    // Add the auth header to access private WP.com files
                    HashMap<String, String> authParams = new HashMap<String, String>();
                    authParams.put("Authorization", "Bearer " + getWPComAuthToken(mContext));
                    headers.putAll(authParams);
                }

                HashMap<String, String> defaultHeaders = new HashMap<String, String>();
                defaultHeaders.put("User-Agent", "wp-android/" + WordPress.versionName);
                headers.putAll(defaultHeaders);

                return super.performRequest(request, headers);
            }
        };

        return stack;
      }
    }
  
  ...
  
    public static BitmapLruCache getBitmapCache() {
        if (mBitmapCache == null) {
            // The cache size will be measured in kilobytes rather than
            // number of items. See http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 16; //Use 1/16th of the available memory for this memory cache.
            mBitmapCache = new BitmapLruCache(cacheSize);
        }
        return mBitmapCache;
    }