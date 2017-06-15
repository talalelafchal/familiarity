package me.johnweland.androidrtp;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            PermissionChecker permissions = PermissionChecker.getInstance(this);
            permissions.permissionsCheck();
        } else {
            // Pre-Marshmallow
            demo();
        }

    }
    
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for RECORD_AUDIO
                if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    mainActivity.demo();
                }
                else {
                    // Permission Denied
                    Toast.makeText(mainActivity, R.string.permission_denied_message, Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                mainActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    

    protected void demo() {
        Toast.makeText(this, "Demo toast", Toast.LENGTH_LONG).show();
    }
}
