import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.*;

public class Permission {

    public static boolean granted(Context context, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            OkAlert.show(context, permissionGroup(permission) + " Permission", "Please grant <App Name> '" + permissionGroup(permission) + "' permission in 'Settings > Apps > <App Name> > Permissions'.");
            return false;
        }

        return true;
    }
    
    // Permission groups listed at http://developer.android.com/guide/topics/security/permissions.html
    private static String permissionGroup(String permission) {
        switch (permission) {
            case READ_CALENDAR:
            case WRITE_CALENDAR:
                return "Calendar";
            case CAMERA:
                return "Camera";
            case GET_ACCOUNTS:
            case READ_CONTACTS:
            case WRITE_CONTACTS:
                return "Contacts";
            case ACCESS_FINE_LOCATION:
            case ACCESS_COARSE_LOCATION:
                return "Location";
            case RECORD_AUDIO:
                return "Microphone";
            case READ_PHONE_STATE:
            case CALL_PHONE:
            case READ_CALL_LOG:
            case WRITE_CALL_LOG:
            case ADD_VOICEMAIL:
            case USE_SIP:
            case PROCESS_OUTGOING_CALLS:
                return "Phone";
            case BODY_SENSORS:
                return "Sensors";
            case SEND_SMS:
            case RECEIVE_SMS:
            case READ_SMS:
            case RECEIVE_WAP_PUSH:
            case RECEIVE_MMS:
                return "SMS";
            case READ_EXTERNAL_STORAGE:
            case WRITE_EXTERNAL_STORAGE:
                return "Storage";
            default:
                return "";
        }
    }

}
