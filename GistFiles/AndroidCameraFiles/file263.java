package com.example.jenny.myapplication.util;


import android.support.annotation.Nullable;
import android.util.Pair;

import com.example.jenny.myapplication.data.Photo;
import com.google.common.collect.Sets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Util class to parse JSON from Flickr api.
 */
public class FlickrJSONParserUtil {

    public static List<Photo> parseJSON(JSONObject obj) {
        List<Photo> result = new ArrayList();
        try {
        JSONObject photos = obj.getJSONObject("photos");
        JSONArray photosJSONArray = photos.getJSONArray("photo");
            for (int i = 0; i < photosJSONArray.length(); i++) {
                JSONObject jsonObj = photosJSONArray.getJSONObject(i);
                String id = jsonObj.getString("id");
                String title = jsonObj.getString("title");
                String thumbUrl = jsonObj.getString("url_t");
                String url = jsonObj.getString("url_l");
                Photo photo = new Photo(id, thumbUrl, url, title);

                String farmId = "farmId:" + jsonObj.getString("farm");
                String serverId = "serverId: " + jsonObj.getString("server");
                String secret = "secret: " + jsonObj.getString("secret");
                photo.setFarmId(farmId);
                photo.setServerId(serverId);
                photo.setSecret(secret);

                result.add(photo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Photo> parseJSON(@Nullable String data) {
        if (data == null) {
            return null;
        }

        List<Photo> result = new ArrayList();
        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
            result = parseJSON(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Build a static photo **/
    private static String buildStaticPhotoUrl(String farmId, String serverId, String id, String secret) {
        StringBuilder builder = new StringBuilder("http://farm");
        builder.append(farmId).append(".staticflickr.com/");
        builder.append(serverId).append("/");
        builder.append(id).append("_").append(secret).append(".jpg");
        return builder.toString();
    }
}
