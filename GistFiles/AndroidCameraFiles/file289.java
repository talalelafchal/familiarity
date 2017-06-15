package com.example.jenny.myapplication.service;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jenny.myapplication.AppController;
import com.example.jenny.myapplication.client.PhotoAdapter;
import com.example.jenny.myapplication.util.FlickrJSONParserUtil;

import org.json.JSONObject;

import javax.inject.Inject;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Implementation of {@link FlickrService}.
 */
public class FlickrServiceImpl implements FlickrService {

    private PhotoAdapter adapter;

    @Inject
    public FlickrServiceImpl() {
    };

    @Override
    public void getPhotosSearchData(PhotoAdapter adapter) {
        this.adapter = adapter;
        String urlStr = new StringBuilder(FLICKR_BASE_URL).append(GETLIST).append(API_KEY).toString();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                        urlStr,
                        null,
                        successListener(),
                        errorListener());
        RequestQueue queue = AppController.getInstance().getRequestQueue();
        queue.add(jsonRequest);
    }

    private Response.Listener<JSONObject> successListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                adapter.setPhotos(FlickrJSONParserUtil.parseJSON(response));
                adapter.notifyDataSetChanged();
            }
        };
    }

    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
    }
}