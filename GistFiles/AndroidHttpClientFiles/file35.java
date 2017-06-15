AsyncHttpClient client = new AsyncHttpClient(); // import com.loopj.android.http.AsyncHttpClient;
String stringCookies = CookieManager.getInstance().getCookie(url);
Log.e(TAG, stringCookies);
client.addHeader(SM.COOKIE, stringCookies); // import cz.msebera.android.httpclient.cookie.SM;
// décommenter la section suivante quand web debuging avec Fiddler
// on oublie pas l'import com.loopj.android.http.MySSLSocketFactory
/*
try {
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    trustStore.load(null, null);
    MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
    sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    client.setSSLSocketFactory(sf);
}
catch (Exception e) {
    Log.e(TAG, "EXCEPTION "+e);
    return;
}
*/
client.get(urlToGet, new AsyncHttpResponseHandler() {
    public void onStart() {
        Log.e(TAG, "onStart");
    }
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        Log.e(TAG, "onSuccess");
        Log.e(TAG, new String(response));
    }
    // HTTP 4xx
    public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable e) {
        Log.e(TAG, "onFailure");
        Log.e(TAG, new String(response));
    }
    public void onRetry(int retryNo) {
        Log.e(TAG, "onRetry");
    }
});