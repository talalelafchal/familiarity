package com.racheats.sigoes.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.racheats.sigoes.R;
import com.racheats.sigoes.home.HomeActivity;
import com.racheats.sigoes.intro.IntroActivity;
import com.racheats.sigoes.mapcoworking.MapCoworkingActivity;
import com.racheats.sigoes.util.UserSessionManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE =  200;
    private final int DELAY_SPLASH  = 3000;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView txt = (TextView) findViewById(R.id.splash_screen_app_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Lobster_1.3.otf");
        txt.setTypeface(font);
        txt.setText("Sigoes");
        session = new UserSessionManager(getApplicationContext());
        if (checkPermission()) {
            Toast.makeText(SplashScreen.this, "Semua permisi telah Anda ijinkan. Fitur lokasi dapat berjalan normal", Toast.LENGTH_SHORT).show();
            nextStep();
        }else if(!checkPermission())
        {
            requestPermission();
        }else
        {
            nextStep();
        }
    }

    private void nextStep()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (session.isLoggedIn()){
                    intent = new Intent(SplashScreen.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    intent = new Intent(SplashScreen.this, IntroActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }, DELAY_SPLASH);
    }

    private boolean checkPermission()
    {
        int checkCoarseLocation     = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int checkFineLocation       = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int checkCamera             = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.CAMERA);
        int checkReadContact        = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_CONTACTS);
        int checkWriteContact       = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_CONTACTS);
        int checkReadStorage        = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int checkWriteStorage       = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int checkPhoneState         = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_PHONE_STATE);

        return checkCoarseLocation == PackageManager.PERMISSION_GRANTED && checkFineLocation == PackageManager.PERMISSION_GRANTED &&
                checkCamera == PackageManager.PERMISSION_GRANTED && checkReadContact == PackageManager.PERMISSION_GRANTED &&
                checkWriteContact ==  PackageManager.PERMISSION_GRANTED && checkReadStorage == PackageManager.PERMISSION_GRANTED &&
                checkWriteStorage == PackageManager.PERMISSION_GRANTED && checkPhoneState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(SplashScreen.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    /* PERMISSION GRANTED */
                    Log.d("list grant result ", grantResults.toString());

                    boolean coarseLocationAccepted          = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean fineLocationAccepted            = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted                  = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean readContactAccepted             = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean writeContactAccepted            = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean readStorageAccepted             = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted            = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneAccepted               = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    if(coarseLocationAccepted && fineLocationAccepted && cameraAccepted
                            && readContactAccepted && writeContactAccepted && readStorageAccepted
                            && writeStorageAccepted && readPhoneAccepted)
                    {
                        Toast.makeText(SplashScreen.this, "Terima Kasih Telah Mengijinkan Beberapa Permission Untuk Aplikasi SIGOES", Toast.LENGTH_SHORT).show();
                        nextStep();
                    }else
                    {
                        Toast.makeText(SplashScreen.this, "Beberapa permisi Anda tolak. Anda harus mengijinkan aplikasi untuk memakai fitur - fitur tersebut", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder add = new AlertDialog.Builder(SplashScreen.this);
                        AlertDialog alert = add.create();
                        alert.setTitle("Permission dibutuhkan!");
                        alert.setMessage("SIGOES membutuhan beberapa permission. Silahkan ke Menu Setting -> Manajer Aplikasi -> SIGOES dan perbolehkan semua permission yang dibutuhkan SIGOES");
                        alert.show();
                        if(!checkPermission())
                        {
                            requestPermission();
                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
//                                showMessageOKCancel("Anda perlu mengijinkan beberapa permission agar fitur SIGOES dapat berjalan",
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                    requestPermissions(new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, CAMERA}, PERMISSION_REQUEST_CODE);
//                                                }
//                                            }
//                                        });
//                                return;
//                            }
//                        }
                    }
                } else {
                    /* PERMISSION DENIED */
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashScreen.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
