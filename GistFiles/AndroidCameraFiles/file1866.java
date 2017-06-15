package com.example.jenny.myapplication.service;

import android.support.annotation.Nullable;

import com.example.jenny.myapplication.client.PhotoAdapter;

import java.util.List;

import dagger.Module;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Simple Flickr api service.
 */
public interface FlickrService {

    String FLICKR_BASE_URL = "https://api.flickr.com/services/rest/";
    String GETLIST = "?method=flickr.photos.search";
    String API_KEY = "&api_key=949e98778755d1982f537d56236bbb42&tags=sharkfeed&format=json&nojsoncallback=1&page=1&extras=url_t,url_c,url_l,url_o";

    /**
     * Make an async call to get list of photos.
     */
    void getPhotosSearchData(PhotoAdapter photoAdapter);
}
