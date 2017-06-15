/**
 * DL SharedPreference class will hold all methods related to Android SharedPreferences.
 * Methods in this class enable developers to use SharedPreferences with minimal effort across
 * multiple projects.
 *
 * @author Damon
 */
@SuppressWarnings("unused")
public class DLSharedPreference {

    /**
     * @param name    of SharedPreference object.
     * @param context of the application
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getSharedPreferences(String name, Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * save object to shared preference.
     *
     * @param context of the application
     * @param key     is the key of that object value
     * @param obj     is the object
     * @return true if object is saved successfully
     */
    public static boolean saveObjectToSharedPreference(Context context, String key,
                                                                            Serializable obj) {
        if (obj == null){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(key, null).commit();
            return true;
        }
        String res = DLUtil.serializeObjectToString(obj);
        if (res != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(key, res).commit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clear all the data in SharedPreference.
     *
     * @param context of the application
     */
    public static void clearSharedPreference(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Retrieve object from shared preference with specific key.
     *
     * @param context of the application
     * @param key     is the key of that object value
     * @param clazz   of object for type casting
     * @param <T>     the object type
     * @return retrieved object from SharedPreference
     */
    public static <T extends Object> T retrieveObjectFromSharedPreference(
                                                Context context, String key, Class<T> clazz) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String res = sp.getString(key, null);
        if (res != null) {
            Object obj = DLUtil.deserializeObjectFromString(res);
            if (obj != null && obj.getClass().getName().equals(clazz.getName())) {
                return clazz.cast(obj);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * retrieve ArrayList from shared preference with specific key.
     *
     * @param context of the application
     * @param key     is the key of that object value
     * @param clazz   of object for type casting
     * @param <T>     the object type
     * @return retrieved list from SharedPreference
     */
    public static <T extends Object> ArrayList<T> retrieveArrayListFromSharedPreference(
                                                    Context context, String key, Class<T> clazz) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String res = sp.getString(key, null);
        if (res != null) {
            Object obj = DLUtil.deserializeObjectFromString(res);
            if (obj != null && obj instanceof DLCacheValue){
                obj = ((DLCacheValue) obj).getObj();
                if (obj != null && obj instanceof ArrayList) {
                    //cast to generic type ArrayList
                    ArrayList<T> castObject = (ArrayList<T>) obj;
                    //check type
                    if (castObject != null && castObject.size() > 0
                            && castObject.get(0).getClass() == clazz) {
                        return castObject;
                    }
                }
            }

        }
        return null;
    }
}
