package com.extralam.api;

import android.app.TaskStackBuilder;
import android.os.SystemClock;
import android.util.Log;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AndroidHttpService
 * <p/>
 * Custom Http Service
 * <p/>
 * Copyright (c) 2014 @author extralam @ HongKong (http://ah-lam.com)
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p/>
 * Usage [1] :
 * AndroidHttpService.request(url, new AndroidHttpService.SimpleHttpRequestListener() { ... });
 * <p/>
 * Usage [2] :
 * AndroidHttpService.request(url, new AndroidHttpService.SimpleHttpRequestListener() { ... } ,
 * new SimpleHttpRequestRetryPolicy() { ... });
 *
 * version 0.2.1    -- Bug Fix on remove thread in HashMap
 * version 0.2      -- Add HttpPost Method
 * version 0.1      -- First init
 *
 */
public class AndroidHttpService {

    // TODO : Make a Queue process
    // private static final Queue<String> mQueue = new PriorityBlockingQueue<String>();

    private static final String TAG = "AndroidHttpService";
    private static final String VERSION = "0.2.1";
    private static final boolean D = true;
    private static final int MAX_THREADING = 4;
    private final static HashMap<String,Thread> mRequestUrlList = new HashMap<String, Thread>();

    /**
     * Http Response Listner
     */
    public static interface SimpleHttpRequestListener {
        public void onResponse(JSONObject response);
        public void onError();
    }

    /**
     * CustomHttpService Retry Policy
     */
    public static interface SimpleAndroidHttpServiceRetryPolicy {
        public int getTimeOut();
        public int getRetryTime();
    }

    /**
     * @param url Request Http Url
     * @param l   {@link AndroidHttpService.SimpleHttpRequestListener }
     * @return Return Current HttpRequest Thread
     */
    public static Thread request(String url, SimpleHttpRequestListener l) {
        return init(url,l, new DefaultAndroidHttpServiceRetryPolicy());
    }

    /**
     * @param url Request Http Url
     * @param l   {@link AndroidHttpService.SimpleHttpRequestListener }
     * @param rL  {@link com.fw.luvline.api.AndroidHttpService.SimpleAndroidHttpServiceRetryPolicy }
     * @return Return Current HttpRequest Thread
     */
    public static Thread request(String url, SimpleHttpRequestListener l, SimpleAndroidHttpServiceRetryPolicy rL) {
       return init(url,l,rL);
    }

    private static Thread init(String url, SimpleHttpRequestListener l, SimpleAndroidHttpServiceRetryPolicy rL){
        if(mRequestUrlList.get(url) != null){
            return mRequestUrlList.get(url);
        }
        Thread mThread = new Thread(new SimpleHttpRequestTask(url, l , rL));
        mThread.start();
        mRequestUrlList.put(url , mThread);
        l("Thread size : " + mRequestUrlList.size());
        return mRequestUrlList.get(url);
    }


    /**
     * The Main Http Request Runnable
     */
    private static class SimpleHttpRequestTask implements Runnable {

        private int CONNECTION_TIMEOUT_MS = 6000;
        private int RETRY_TIME = 1;

        private String mRequestUrl;
        private long mLastTimestamp;
        private HttpClient customHttpClient;
        private List<NameValuePair> mParams;
        private SimpleAndroidHttpServiceRetryPolicy mSimpleAndroidHttpServiceRetryPolicy;
        private SimpleHttpRequestListener mSimpleHttpRequestListener;

        private int mCurrentRetryTime = 0;
        private boolean isRetrying = false;
        private String mId;

        public SimpleHttpRequestTask(String url, SimpleHttpRequestListener l) {
            init(url, l, new DefaultAndroidHttpServiceRetryPolicy(CONNECTION_TIMEOUT_MS, RETRY_TIME));
        }

        public SimpleHttpRequestTask(String url, SimpleHttpRequestListener l, SimpleAndroidHttpServiceRetryPolicy mL) {
            init(url, l, mL);
        }

        public SimpleHttpRequestTask(String url, List<NameValuePair> params,  SimpleHttpRequestListener l) {
            init(url, l, new DefaultAndroidHttpServiceRetryPolicy(CONNECTION_TIMEOUT_MS, RETRY_TIME));
            addParams(params);
        }

        public SimpleHttpRequestTask(String url, List<NameValuePair> params, SimpleHttpRequestListener l, SimpleAndroidHttpServiceRetryPolicy mL) {
            init(url, l, mL);
            addParams(params);
        }

        private void addParams(List<NameValuePair> p){
            if(p != null){
                mParams = new ArrayList<NameValuePair>();
                mParams.addAll(p);
            }
        }

        private void init(String url, SimpleHttpRequestListener l, SimpleAndroidHttpServiceRetryPolicy mL) {
            mId = UUID.randomUUID().toString().substring(0, 8);
            mRequestUrl = url;
            mSimpleHttpRequestListener = l;
            mLastTimestamp = SystemClock.elapsedRealtime();//System.currentTimeMillis();
            mSimpleAndroidHttpServiceRetryPolicy = mL;
        }

        public String getId(){
            return mId;
        }

        public synchronized HttpClient getHttpClient() {
            if (customHttpClient != null) {
                return customHttpClient;
            }
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setStaleCheckingEnabled(params, false);
            params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, false);
            ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT_MS);
            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_MS);
            HttpConnectionParams.setSoTimeout(params, mSimpleAndroidHttpServiceRetryPolicy.getTimeOut());
            HttpConnectionParams.setSocketBufferSize(params, 8192);
            HttpClientParams.setRedirecting(params, false);

            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

            customHttpClient = new DefaultHttpClient(conMgr, params);

            return customHttpClient;
        }

        @Override
        public void run() {
//            // -- Test Http Connection
//            try {
//                URL obj = new URL(mRequestUrl);
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                con.setConnectTimeout(40000);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            // --
            addTimeChecker(mLastTimestamp, "Start Process");
            HttpClient mHttpClient = getHttpClient();
            HttpUriRequest request;

            if(mParams == null) {
                HttpGet httpGet = new HttpGet(mRequestUrl + "#" + System.currentTimeMillis());
                request = httpGet;
            }else{
                HttpPost httpPost = new HttpPost(mRequestUrl + "#" + System.currentTimeMillis());
                request = httpPost;
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(mParams));
                }catch(UnsupportedEncodingException e){
                    addTimeChecker(mLastTimestamp, "UnsupportedEncodingException");
                    restart();
                }
            }


            if (checkKillSelf()) {
                return;
            }

            isRetrying = false;

            try {
                addTimeChecker(mLastTimestamp, "Before Http execute");
                request.addHeader("Accept-Encoding", "gzip");
                HttpResponse httpResponse = mHttpClient.execute(request);
                HttpEntity httpEntity = null;

                int status = httpResponse.getStatusLine().getStatusCode();

                addTimeChecker(mLastTimestamp, "After Http execute , Http Status " + status + " ");

                if (httpResponse != null) {
                    httpEntity = httpResponse.getEntity();
                } else {
                    restart();
                }

                if (checkKillSelf()) {
                    l("Kill Self");
                    addTimeChecker(mLastTimestamp, "Kill Self" + " and End Process");
                    return;
                }

                if (httpEntity != null) {
                    addTimeChecker(mLastTimestamp, "Before get Response Body");
                    String responseBody = EntityUtils.toString(httpEntity, HTTP.UTF_8);
                    l(responseBody);
                    addTimeChecker(mLastTimestamp, "After get Response Body");
                    if (mSimpleHttpRequestListener != null) {
                        mRequestUrlList.remove(mRequestUrl);
                        mSimpleHttpRequestListener.onResponse(new JSONObject(responseBody));
                    }
                }

            }catch (SocketTimeoutException e) {
                addTimeChecker(mLastTimestamp, "SocketTimeoutException");
                restart();
            } catch (JSONException e) {
                addTimeChecker(mLastTimestamp, "JSONException");
                restart();
            } catch (ClientProtocolException e) {
                addTimeChecker(mLastTimestamp, "ClientProtocolException");
                restart();
            } catch (IOException e) {
                addTimeChecker(mLastTimestamp, "IOException");
                restart();
            }

            addTimeChecker(mLastTimestamp, "End Process");

            try {
                mRequestUrlList.remove(mRequestUrl);
            }catch (Exception e){

            }
        }

        private boolean checkKillSelf() {
            if (Thread.currentThread().isInterrupted() && !isRetrying) {
                if (mSimpleHttpRequestListener != null) {
                    mRequestUrlList.remove(mRequestUrl);
                    mSimpleHttpRequestListener.onError();
                }
                return true;
            }
            return false;
        }

        /**
         * Restart current Thread
         */
        private boolean restart() {
            if (isRetry()) {
                Thread mCurrentThread = Thread.currentThread();
                mCurrentThread.interrupt();
                // Retry Time +1
                mCurrentRetryTime++;
                // Restart Current Thread and use current Runnable
                mCurrentThread = new Thread(this);
                mCurrentThread.start();
                return true;
            } else {
                if (mSimpleHttpRequestListener != null) {
                    mSimpleHttpRequestListener.onError();
                    mRequestUrlList.remove(mRequestUrl);
                    Thread.currentThread().interrupt();
                }
                return false;
            }
        }

        /**
         * Check Retry Policy
         *
         * @return check exist retry policy or not
         */
        private boolean isRetry() {
            l("isRetry : " + mCurrentRetryTime + " , " + mSimpleAndroidHttpServiceRetryPolicy.getRetryTime());
            if (mCurrentRetryTime >= mSimpleAndroidHttpServiceRetryPolicy.getRetryTime()) {
                return false;
            }
            isRetrying = true;
            return true;
        }

        private final void addTimeChecker(long lasttimecheck, String msg) {
            if (!D)
                return;
            l(msg + " , " + TimeUnit.MILLISECONDS.toMillis(SystemClock.elapsedRealtime() - lasttimecheck) + "ms");
        }

        private final void l(String str) {
            if (D)
                Log.d(TAG, "[" + mId + "] [" + mCurrentRetryTime + "] " + str);
        }
    }

    /**
     * A default Retry Policy Class
     */
    public static class DefaultAndroidHttpServiceRetryPolicy implements SimpleAndroidHttpServiceRetryPolicy {

        private final int DEFAULT_TIMEOUT = 1000 * 12;
        private final int DEFAULT_RETRY_TIME = 1;

        private int mTimeout = 3000;
        private int mRetryTime = 1;

        public DefaultAndroidHttpServiceRetryPolicy() {
            mTimeout = DEFAULT_TIMEOUT;
            mRetryTime = DEFAULT_RETRY_TIME;
        }

        public DefaultAndroidHttpServiceRetryPolicy(int timeout, int retryTime) {
            mTimeout = timeout;
            mRetryTime = retryTime;
        }

        @Override
        public int getTimeOut() {
            return mTimeout;
        }

        @Override
        public int getRetryTime() {
            return mRetryTime;
        }
    }

    private static final void l(String str) {
        if (D)
            Log.d(TAG, "[Log]" + str);
    }

}
