package com.stochasticstudio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EdwinSL on 8/9/2015.
 */
public class NetworkRequest {
    private static NetworkRequest mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private NetworkRequest(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    RetryPolicy retryPolicy = new RetryPolicy() {
        @Override
        public int getCurrentTimeout() {
            return 5000;
        }

        @Override
        public int getCurrentRetryCount() {
            return DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {
            error.printStackTrace();
        }
    };

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkRequest(context);
        }
    }

    public static synchronized NetworkRequest getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelRequest(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void PostRequest(String url, JSONObject data, Response.Listener<JSONObject> callback) throws JSONException {
        PostRequest(url, data, callback, new HashMap<String, String>(), "");
    }
    public void PostRequest(String url, JSONObject data, Response.Listener<JSONObject> callback, String tag) throws JSONException {
        PostRequest(url, data, callback, new HashMap<String, String>(), tag);
    }
    public void PostRequest(String url, JSONObject data, Response.Listener<JSONObject> callback, final Map<String, String> headers) throws JSONException {
        PostRequest(url, data, callback,headers, "");
    }
    public void PostRequest(String url, JSONObject data, final Response.Listener<JSONObject> callback, final Map<String, String> headers, String tag) throws JSONException {
        JsonObjectRequest request = new JsonObjectRequest(url, data, callback, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                error.printStackTrace();
                callback.onResponse(new JSONObject());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.putAll(super.getHeaders());
                header.putAll(headers);
                return header;
            }
        };
        Log.v("Json", data.toString(4));
        request.setTag(tag);
        request.setRetryPolicy(retryPolicy);
        addToRequestQueue(request);
    }

    public void GetRequest(String url, Response.Listener<JSONObject> callback) {
        GetRequest(url, callback, new HashMap<String, String>(), "");
    }
    public void GetRequest(String url, Response.Listener<JSONObject> callback, String tag) {
        GetRequest(url, callback, new HashMap<String, String>(), tag);
    }
    public void GetRequest(String url, Response.Listener<JSONObject> callback, final Map<String, String> headers) {
        GetRequest(url, callback, headers, "");
    }
    public void GetRequest(String url, final Response.Listener<JSONObject> callback, final Map<String, String> headers, String tag) {
        JsonObjectRequest request = new JsonObjectRequest(url, callback, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                error.printStackTrace();
                callback.onResponse(new JSONObject());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.putAll(super.getHeaders());
                header.putAll(headers);
                return header;
            }
        };
        request.setTag(tag);
        request.setRetryPolicy(retryPolicy);
        addToRequestQueue(request);
    }
}
