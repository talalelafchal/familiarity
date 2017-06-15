package putugunation.com.mapsroute.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by putuguna on 5/14/2016.
 */
public class Utils {
    public static String getStrings(Context ctx, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String value = sharedPreferences.getString(
                key, null);
        return value;
    }

    //save string
    public static void saveString(Context context, String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
}