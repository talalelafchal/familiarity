package com.example.jenny.myapplication.service;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;

/**
 * Simple image service.
 */
public interface ImageService {

    /**
     * Download photo to the image.
     */
    void downloadPhoto(String url,
                       Response.Listener<Bitmap> successListener,
                       Response.ErrorListener errorListener);
}
