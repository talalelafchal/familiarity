package com.damon.android;

/**
 * Created by damon on 7/2/15.
 */

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This DLAppSession class is created for persistence some simple data objects.<br/>
 * This class will be init inside of Android Application subclass.<br/>
 * It can persistence some simple data objects during Android Application lifecycle.<br/>
 * <br/>
 * It can persistence 2 type of data. <br/>
 * <tr/>1. Data is valid along with Android Application. --Data will be cleared after app been
 * killed.
 * <tr/>2. Data is valid all the time. --Data will be stored in SharedPreference.
 * <br/>
 * <br/>
 * <br/>
 * - TBD<br/>
 * - 1. Now just return the data object. If we need we can return the data object with last
 * update timestamp.
 * <p/>
 *
 * @author Damon
 */
@SuppressWarnings("unused")
public class DLAppSession {

    /**
     * Constant String representing class name for logs.
     */
    protected String _tag = ((Object) this).getClass().getSimpleName();

    private static DLAppSession _self;
    private Context _context;
    private Map<String, DLCacheValue> _cacheValueMap;

    private DLAppSession(Context context) {
        this._context = context.getApplicationContext();
        _cacheValueMap = new HashMap<String, DLCacheValue>();
    }

    /**
     * Retrieve Singleton object of DLAppSession.
     *
     * @param context of the application.
     * @return DLAppSession object
     */
    public static DLAppSession getInstance(Context context) {
        if (_self == null) {
            _self = new DLAppSession(context);
        }
        return _self;
    }

    /**
     * Cache data object with specific key value.
     *
     * @param key             the key of the cached value
     * @param obj             the real object which we need to cache.
     * @param timeExpiry      set the cached value expiry time
     * @param flagPersistence if it is true data object will be persisted both map
     *                        and shared preference
     * @param constraint      the constraint string
     * @return result. true is success and false is failed.
     */
    public boolean cacheValue(String key, Object obj, long timeExpiry,
                              Boolean flagPersistence, String constraint) {
        if (obj == null) {
            //clear value
            return DLSharedPreference.saveObjectToSharedPreference(_context, key, null);
        }
        DLCacheValue value = new DLCacheValue(key, obj, timeExpiry, flagPersistence, constraint);

        if (flagPersistence) {
            if (obj instanceof Serializable) {
                return DLSharedPreference.saveObjectToSharedPreference(_context, key, value);
            } else {
                DLLogger.e(_tag, "Object[" + obj.getClass().getName() + "] is not serializable. " +
                        "Not support to store it into SharedPreference");
                return false;
            }
        } else {
            _cacheValueMap.put(key, value);
            return true;
        }
    }

    /**
     * Cache data object with specific key value.
     *
     * @param key             is the key of that object value
     * @param obj             is the object
     * @param timeExpiry      expiry time of object
     * @param flagPersistence tells if object should be saved in
     *                        SharedPreference (persistent memory)
     * @return retrieved object from Session or SharedPreference
     */
    public boolean cacheValue(String key, Object obj, long timeExpiry, Boolean flagPersistence) {
        return cacheValue(key, obj, timeExpiry, flagPersistence, null);
    }

    /**
     * Cache data object with specific key value.
     *
     * @param key        is the key of that object value
     * @param obj        is the object
     * @param timeExpiry expiry time of object
     * @return retrieved object from Session or SharedPreference
     */
    public boolean cacheValue(String key, Object obj, long timeExpiry) {
        return cacheValue(key, obj, timeExpiry, false);
    }

    /**
     * Cache data object with specific key value.
     *
     * @param key             is the key of that object value
     * @param obj             is the object
     * @param flagPersistence tells if object should be saved in
     *                        SharedPreference (persistent memory)
     * @return retrieved object from Session or SharedPreference
     */
    public boolean cacheValue(String key, Object obj, boolean flagPersistence) {
        return cacheValue(key, obj, 0, flagPersistence);
    }

    /**
     * Cache data object with specific key value.
     *
     * @param key is the key of that object value
     * @param obj is the object
     * @return retrieved object from Session or SharedPreference
     */
    public boolean cacheValue(String key, Object obj) {
        return cacheValue(key, obj, 0, false);
    }

    /**
     * Clear the cached value with provided key, both from Session and SharedPreference.
     *
     * @param key is the key of that object value
     */
    public void clearCacheValue(String key) {
        cacheValue(key, null, true);
        _cacheValueMap.remove(key);
    }

    /**
     * Clear all the cached values, both from Session and SharedPreference.
     */
    public void clearAll() {
        _cacheValueMap = new HashMap<String, DLCacheValue>();
        DLSharedPreference.clearSharedPreference(_context);
    }

    /**
     * Retrieve cached data object from DLAppSession.
     *
     * @param key        is the key of that object value
     * @param constraint namespace or pool of DLCacheValue
     * @param type       class of object for type casting
     * @param <T>        the object type
     * @return retrieved object from Session or SharedPreference
     */
    public <T extends Object> T getCachedValue(String key, String constraint, Class<T> type) {
        DLCacheValue value;
        if (_cacheValueMap.containsKey(key)) {
            //hit in cache map
            value = _cacheValueMap.get(key);
        } else {
            //try to hit in SharedPreference
            value = DLSharedPreference.retrieveObjectFromSharedPreference(_context, key,
                    DLCacheValue.class);
        }
        if (value == null || value.isExpired() || !value.isValid(constraint)) {
            return null;
        }
        Object obj = value.getObj();
        if (obj != null && obj.getClass().getName().equals(type.getName())) {
            return type.cast(obj);
        }
        return null;
    }

    /**
     * Retrieve cached data object from DLAppSession.
     *
     * @param key  is the key of that object value
     * @param type class of object for type casting
     * @param <T>  the object type
     * @return retrieved object from Session or SharedPreference
     */
    public <T extends Object> T getCachedValue(String key, Class<T> type) {
        return getCachedValue(key, null, type);
    }

    /**
     * Retrieve cached data ArrayList from DLAppSession.
     *
     * @param key  is the key of that object value
     * @param type class of object for type casting
     * @param <T>  the object type
     * @return retrieved ArrayList from Session or SharedPreference
     */
    public <T extends Object> ArrayList<T> getCachedList(String key, Class<T> type) {
        if (_cacheValueMap.containsKey(key)) {
            //hit in cache map
            DLCacheValue value = _cacheValueMap.get(key);
            if (value.isExpired()) {
                return null;
            }
            Object obj = value.getObj();
            if (obj != null && obj instanceof ArrayList) {
                //cast to generic type ArrayList
                ArrayList<T> castObject = (ArrayList<T>) obj;
                //check type
                if (castObject != null && castObject.size() > 0
                        && castObject.get(0).getClass() == type) {
                    return castObject;
                }
            }
        } else {
            //try to hit in SharedPreference
            return DLSharedPreference.retrieveArrayListFromSharedPreference(_context, key, type);
        }
        return null;
    }
}
