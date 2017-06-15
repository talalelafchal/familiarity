package chocoyo.selfiedescuentos.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chocoyo.selfiedescuentos.R;

public class PermissionsActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        verify();

        RelativeLayout containerRoot = (RelativeLayout) findViewById(R.id.containerRoot);
        containerRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });
    }


    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("ACCESS_FINE_LOCATION");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("CAMERA");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE_EXTERNAL_STORAGE");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ_EXTERNAL_STORAGE");
        if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("SEND_SMS");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                showMessageOKCancel(Html.fromHtml(getString(R.string.permissions_dialog_message)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PermissionsActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(PermissionsActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(PermissionsActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(Spanned message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PermissionsActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.permissions_dialog_gps_positive_button), okListener)
                .setNegativeButton(R.string.permissions_dialog_gps_negative_button, null)
                .create()
                .show();
    }

    private void verify() {
        int camera = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.CAMERA);
        int readExternalStorage = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStorage = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accessFineLocation = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int sendSms = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.SEND_SMS);

        if (camera == PackageManager.PERMISSION_GRANTED
                && readExternalStorage == PackageManager.PERMISSION_GRANTED
                && writeExternalStorage == PackageManager.PERMISSION_GRANTED
                && accessFineLocation == PackageManager.PERMISSION_GRANTED
                && sendSms == PackageManager.PERMISSION_GRANTED) {

            openActivity();
        } else {
            checkPermissions();
        }
    }

    private void openActivity() {
        // open activity
        Intent intent = new Intent(getApplicationContext(), VenuesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    // verify
                    verify();
                } else {
                    // Permission Denied
                    Toast.makeText(PermissionsActivity.this, getString(R.string.permissions_message_same), Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
