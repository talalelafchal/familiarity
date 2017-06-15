package dm.com.imagepixelator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        ImageView activity_start_up = (ImageView) findViewById(R.id.activity_start_up);

        Animation animation = AnimationUtils.loadAnimation(StartUpActivity.this, R.anim.startup);
        activity_start_up.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheckwe = ContextCompat.checkSelfPermission(StartUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permissionCheckre = ContextCompat.checkSelfPermission(StartUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    int permissionCheckc = ContextCompat.checkSelfPermission(StartUpActivity.this, Manifest.permission.CAMERA);
                    if (permissionCheckwe != PackageManager.PERMISSION_GRANTED || permissionCheckre != PackageManager.PERMISSION_GRANTED || permissionCheckc != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                    } else {
                        Intent i = new Intent(StartUpActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    startActivity(new Intent(StartUpActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                ArrayList<Integer> granted = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    granted.add(grantResults[i]);
                }
                if (granted.contains(-1)) {
                    Toast.makeText(StartUpActivity.this, "Please give permissions to access app", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Intent i = new Intent(StartUpActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                break;
            }
        }
    }
}
