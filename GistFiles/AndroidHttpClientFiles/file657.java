 @Override
    public void initWebView(String src, String postData) {

        binding.wvWebview.setWebChromeClient(new WebChromeClient());
        binding.wvWebview.getSettings().setJavaScriptEnabled(true);
        binding.wvWebview.clearHistory();
        binding.wvWebview.clearCache(true);
        binding.wvWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d(TAG, "shouldOverrideUrlLoading: "+request.getUrl().toString());
                    String urlString = request.getUrl().toString();
                    webViewPresenter.handleUrl(urlString);
                    return true;
                }
                return true;
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d(TAG, "shouldOverrideUrlLoading(DEPRECATED): "+url);
                webViewPresenter.handleUrl(url);
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webViewViewModel.updateLoadingStatus(WebViewViewModel.LoadingStatus.FINISHED);
            }

        });

        Log.d(TAG, "initWebView: "+src+" ### "+postData);
        if(postData == null || postData.isEmpty())
            binding.wvWebview.loadUrl(src);
        else{
           new RetrieveFeedTask().execute(src);
//             binding.wvWebview.postUrl(src, postData.getBytes());
        }
    }

    @Override
    public void setAppbarTitle(String src) {
        binding.appBarLayout.tvToolbarTitle.setText(getWebViewTitleFromUrl(src));
    }


    /**************************************************************************
     // Private
     **************************************************************************/

   class RetrieveFeedTask extends AsyncTask<String, Void, String> {

       private Exception exception;
       private String url;

       protected String doInBackground(String... urls) {
           try {

               url = urls[0];

               List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
               nameValuePairs.add(new BasicNameValuePair("webview", "android"));
               nameValuePairs.add(new BasicNameValuePair("lang", "eng"));

               // Executing POST request
               HttpClient httpclient = new DefaultHttpClient();
               HttpPost httppost = new HttpPost(urls[0]);
               httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
               HttpResponse response = httpclient.execute(httppost);

               // Get the response content
               String line = "";
               StringBuilder contentBuilder = new StringBuilder();
               BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
               while ((line = rd.readLine()) != null) {
                   contentBuilder.append(line);
               }
               String content = contentBuilder.toString();

               // Do whatever you want with the content
               return content;

           }catch (IOException e){
               return null;
           }
       }
//
//        protected void onPostExecute(String content) {
//            if (content == null)
//                return;
//
//            // TODO: check this.exception
//            // TODO: do something with the feed
//            // Show the web page
//            binding.wvWebview.loadDataWithBaseURL(url, content, "text/html", "UTF-8", null);
//        }
//    }