package com.mobop.mobopratingapp;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import cz.msebera.android.httpclient.entity.StringEntity;

class RestClient {

    private static String BASE_URL = "base_url"; // dev
    //private static final String BASE_URL = "http://82.197.175.218/ApiProject2/web"; // dev

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();

    static void setCookieStore(PersistentCookieStore cookieStore) {
        client.setCookieStore(cookieStore);
    }

    static void setTimeout(int duration) {
        client.setTimeout(duration);
    }

    public static void cancelAllRequests(boolean mayInterruptIfRunning) {
        client.cancelAllRequests(mayInterruptIfRunning);
    }

    public static void addHeader(String key, String value) {
        client.addHeader(key, value);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(Context ctx, String url, StringEntity params, String mimeType, AsyncHttpResponseHandler responseHandler) {
        client.get(ctx, getAbsoluteUrl(url), params, mimeType, responseHandler);
    }

    public static void getFullUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context ctx, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(ctx, getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context ctx, String url, StringEntity params, String mimeType, AsyncHttpResponseHandler responseHandler) {
        client.post(ctx, getAbsoluteUrl(url), params, mimeType, responseHandler);
    }

    public static void syncPost(Context ctx, String url, StringEntity params, String mimeType, AsyncHttpResponseHandler responseHandler) {
        syncClient.post(ctx, getAbsoluteUrl(url), params, mimeType, responseHandler);
    }

    public static void syncGet(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        syncClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }
}
0
