
/**
 * Created by ADMIN on 6/26/2016.
 */
public class Constants {


    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";

    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where user lists are stored (ie "userLists")
     */
    public static final String FIREBASE_LOCATION_USERS = "users";

    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = "REPLACE_URL";
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;

    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String KEY_GOOGLE_EMAIL = "GOOGLE_EMAIL";

    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String GOOGLE_PROVIDER = "google";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";
}
