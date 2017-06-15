import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * To store user details in shared preferences. At startup, we will not prompt to user to login if a user is already logged in
 * So this login details will be stored in android shared preferences
 * 
 * @author Mohsin Khan
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class SPUtils {

    /**
     * SharedPreferences instance for read and write operations.
     */
    private SharedPreferences sp;

    /*
     * Some common preference tags / labels
     */
    public static final String USERNAME = "username";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_ID = "user_id";
    public static final String IMEI = "device_imei";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String MOBILE = "mobile";
    public static final String ADDRESS = "address";
    public static final String PICTURE = "picture";
    public static final String THUMBNAIL = "thumbnail";
    public static final String LAST_UPDATE = "last_update";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String FCM_ID = "fcm";

    /**
     * To keep API host address
     */
    public static final String HOST_ADDRESS = "api";


    public SPUtils(Context context) {
        if (context != null)
            sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    /**
     * @return value for the specified tag stored in shared preferences.
     */
    public String getString(String tag) {
        return sp.getString(tag, "");
    }

    /**
     * @return value for the specified tag stored in shared preferences.
     */
    public Boolean getBoolean(String tag) {
        return sp.getBoolean(tag, false);
    }

    /**
     * @return value for the specified tag stored in shared preferences.
     */
    public long getLong(String tag) {
        return sp.getLong(tag, Calendar.getInstance().getTimeInMillis());
    }

    /**
     * @return default or custom from/start date for api calls
     */
    public long fromDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -6);
        return sp.getLong(FROM, calendar.getTimeInMillis());
    }

    /**
     * @return default or custom to/end date for api calls
     */
    public long toDate() {
        return sp.getLong(TO, Calendar.getInstance().getTimeInMillis());
    }

    /**
     * It will store key value pair in the shared preferences
     *
     * @param tag   name of the key
     * @param value for the key
     */
    public void setValue(String tag, String value) {
        sp.edit().putString(tag, value).apply();
    }

    /**
     * It will store key value pair in the shared preferences
     *
     * @param tag   name of the key
     * @param value for the key
     */
    public void setValue(String tag, long value) {
        sp.edit().putLong(tag, value).apply();
    }

    /**
     * It will store key value pair in the shared preferences
     *
     * @param tag   name of the key
     * @param value true/false
     */
    public void setValue(String tag, boolean value) {
        sp.edit().putBoolean(tag, value).apply();
    }

    /**
     * Method will store all keys and their corresponding values
     *
     * @param map a hash map to that contains key and values
     */
    public void setMap(HashMap<String, String> map) {
        SharedPreferences.Editor editor = sp.edit();
        //Obtaining random key-values form the HashMap
        for (Map.Entry<String, String> entry : map.entrySet())
            editor.putString(entry.getKey(), entry.getValue());
        editor.apply();
    }

    /**
     * Method to completely remove a key value pair from shared preferences
     *
     * @param key key which is going to be removed
     */
    public void removePair(String key) {
        sp.edit().remove(key).apply();
    }

    /**
     * Method flushPreferences() will remove complete data stored in the shared preferences.
     * So it may be happen that application will start as first time.
     */
    public void clearPreferences() {
        sp.edit().clear().apply();
    }

    /**
     * @return default API address or stored API address
     */
    public String getHostAddress() {
        return sp.getString(HOST_ADDRESS,
                /**Production*/
                //"http://production.demo.com/"
                /**Testing*/
                "http://testing.demo.com/"
                /**Development*/
                //"http://development.demo.com/"
        );
    }
}