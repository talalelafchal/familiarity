/**
 * Created by murtaza.khursheed on 2/9/2016
 */
public class PermissionHelper {

    Context context;
    String[] defaultPermissions = new String[]{ Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
    PermissionListener listener;

    /**
     * Check for Permission, by default for Camera and Microphone
     *
     * @param context
     * @param listener
     */
    public PermissionHelper( Context context, PermissionListener listener ) {
        this.context = context;
        this.listener = listener;
        init( defaultPermissions );
    }

    /**
     * @param context
     * @param listener
     * @param includeDefaultPermissions add additional permissions to check with {@link #defaultPermissions}
     * @param permissions {@link @varags}
     */
    public PermissionHelper( Context context, PermissionListener listener, boolean includeDefaultPermissions, String... permissions ) {
        String[] permission = permissions;
        this.context = context;
        this.listener = listener;
        if ( includeDefaultPermissions )
            permission = combine( this.defaultPermissions, permissions );
        init( permission );
        Log.d( "mydebug", "PermissionHelper permission length: " + permission.length );
    }

    private void init( String[] permissions ) {
        if ( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            for ( String permission : permissions ) {
                if ( getTargetSdkVersion() >= Build.VERSION_CODES.M &&
                        ContextCompat.checkSelfPermission( this.context, permission )
                                != PackageManager.PERMISSION_GRANTED ) {
                    listener.onPermissionError( permission );
                    return;
                } else if ( PermissionChecker.checkSelfPermission( this.context, permission )
                        != PackageManager.PERMISSION_GRANTED ) {
                    listener.onPermissionError( permission );
                    return;
                }
            }
            listener.onPermissionAvailable();
        } else {
            listener.onPermissionAvailable();
        }
    }

    private int getTargetSdkVersion() {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0 );
            return info.applicationInfo.targetSdkVersion;
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String[] combine( String[] a, String[] b ) {
        int      length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy( a, 0, result, 0, a.length );
        System.arraycopy( b, 0, result, a.length, b.length );
        return result;
    }
    
    public interface PermissionListener {
    
        public void onPermissionError( String permission );
    
        public void onPermissionAvailable();
    }
}
