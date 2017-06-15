
        final WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(this.getClass().getName(), "onPageFinished url = " + url);

                if (url.equals(LOGIN_COMPLETE_URL)) {
                    // 認証が完了したら、ページ内からOAuth Verifierコードを抜き出してアラートとして表示する
                    webView.loadUrl("javascript:window.alert(document.getElementsByTagName(\'code\')[0].innerHTML);");
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "url = " + url);
                Log.d(TAG, "message = " + message);

                // 上のonPageFinishedで埋め込んだjavascriptでOAuth Verifierのコードが取得できる。
                // このコードを利用してAccessTokenを取得する。
                String oauthVerifier = message;
                OAuthAccessAsyncTask asyncTask = new OAuthAccessAsyncTask(oauthVerifier, new AccessCallback() {
                    @Override
                    public void onComplete() {
                        // OAuthConsumerからTokenとTokenSecretが取得できる
                        Log.d(TAG, "ACCESS_TOKEN : " + mConsumer.getToken());
                        Log.d(TAG, "ACCESS_TOKEN_SECRET : " + mConsumer.getTokenSecret());
                    }
                });
                asyncTask.execute();

                return true;
            }
        });

        // 認証ページのURLを取得する
        OAuthRequestAsyncTask asyncTask = new OAuthRequestAsyncTask(CALLBACK, new RequestCallback() {
            @Override
            public void onComplete(String authUrl) {
                Log.d(TAG, "authUrl = " + authUrl);

                // 取得したURLを表示する
                webView.loadUrl(authUrl);
            }
        });
        asyncTask.execute();
