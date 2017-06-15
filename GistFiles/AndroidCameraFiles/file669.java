import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

public class PermissionUtil {
    private static final String TAG = PermissionUtil.class.getSimpleName();

    /**
     * @param context     Contexto
     * @param permissions permiso individual o bien agrupados con un array
     * @return true si el permiso/s está conecido/s y false si es negado/s
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, permission + " PERMISSION_DENIED ");
                    return false;
                } else {
                    Log.i(TAG, permission + " PERMISSION_GRANTED");
                }
            }
        } else Log.i(TAG, "ALL GRANTED, Minor Build version code M ");
        return true;
    }

    /**
     * @param activity   Actividad
     * @param permission permiso individual a comprobar su petición
     * @return true si se ha mostrado alguna vez el dialogo de solicitud de permisos y se puede volver a solicitar, devuelve false si es la primera vez que se le muestra
     */
    public static boolean shouldWeAskPermission(Activity activity, String permission) {
        return (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission));
    }


    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Abre la pantalla de Información de la aplicación en ajustes del dispositivo
     *
     * @param context contexto
     */
    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }


    /**
     * @param activity    actividad
     * @param view        vista general para mostrar el snackbar
     * @param permission  permiso a solicitar
     * @param requestCode código de respuesta para procesar en onRequestPermissionsResult
     * @todo Falta hacerla que se puede personalizar el texto
     */
    public static void requestPermission(final Activity activity, View view, final String permission, final int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, permission + " PERMISSION_DENIED ");

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

                Log.i(TAG, "Displaying camera permission rationale to provide additional context.");
                Snackbar.make(view, "Dar permiso para acceder a " + permission,
                        Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                            }
                        })
                        .show();

            } else {
                Log.d(TAG, "First Permission Request show dialog ");
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        } else {
            Log.d(TAG, permission + " PERMISSION_GRANTED ");
        }
    }

}