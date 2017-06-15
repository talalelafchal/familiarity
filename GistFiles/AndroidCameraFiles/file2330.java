/*
Artigo mostrando o que precisa se solicitado ou não:
https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
*/

public class MainActivity extends Activity {

    public static String[] androidPermissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    public static int androidPermissionsRequestCode = 200;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askForPermissions();
    }
    
    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int acceptedPermissions = 0;

            for (int x = 0; x < androidPermissions.length; x++) {
                if (checkSelfPermission(androidPermissions[x]) == PackageManager.PERMISSION_GRANTED) {
                    acceptedPermissions++;
                }
            }

            if (acceptedPermissions == androidPermissions.length) {
                return;
            }

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Mensagem")
                    .setMessage("Para usar este aplicativo, você precisa permitir o acesso aos recursos que serão solicitados a seguir.")
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, androidPermissions, androidPermissionsRequestCode);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        int acceptedPermissions = 0;

        if (permsRequestCode == androidPermissionsRequestCode) {
            if (grantResults.length == androidPermissions.length) {
                for (int x = 0; x < grantResults.length; x++) {
                    if (grantResults[x] == PackageManager.PERMISSION_GRANTED) {
                        acceptedPermissions++;
                    }
                }
            }
        }

        if (acceptedPermissions != androidPermissions.length) {
            askForPermissions();
        }
    }
    
}