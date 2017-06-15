package com.example.jenny.myapplication.service;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.jenny.myapplication.AppController;
import com.example.jenny.myapplication.R;
import com.example.jenny.myapplication.util.FlickrJSONParserUtil;

import org.json.JSONObject;

import javax.inject.Inject;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Implementation of {@link ImageService}.
 */
public class ImageServiceImpl implements ImageService {

    @Inject
    public ImageServiceImpl() {
    };

    @Override
    public void downloadPhoto(String url,
                              Response.Listener<Bitmap> successListener,
                              Response.ErrorListener errorListener) {
        ImageRequest request = new ImageRequest(url, successListener, 0, 0, null, errorListener);
        RequestQueue queue = AppController.getInstance().getRequestQueue();
        queue.add(request);
    }
}
