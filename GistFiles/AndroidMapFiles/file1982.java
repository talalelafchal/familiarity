package com.vesicant.Services;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: vesicant
 * Date: 21/03/13
 * Time: 09:25
 * To change this template use File | Settings | File Templates.
 */
public class StorageService {
    private static StorageService _instance = new StorageService();

    public static StorageService getInstance() { return _instance; }

    JSONObject _objectStore;

    public void put(String key, JSONObject object) throws JSONException {
        _objectStore.put(key, object);
    }

    public JSONObject get(String key) throws JSONException {
        return _objectStore.getJSONObject(key);
    }

    public void load() {

    }

    public void save() {

    }

    public void close() {

    }
}
