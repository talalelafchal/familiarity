package dns.adhaarscanner.Screen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureActivity;

import dns.adhaarscanner.R;

/**
 * Created by nayan on 12/31/16.
 */

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    Button scanButton;

    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";

    private static final int CAM_REQUEST_CODE = 11;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        scanButton = (Button) findViewById(R.id.scan_btn);
        scanButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int permission = checkCallingOrSelfPermission(CAMERA_PERMISSION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra(Intents.Scan.BEEP_ENABLED, false);
            intent.putExtra(Intents.Scan.PROMPT_MESSAGE, "");
            intent.setAction("com.google.zxing.client.android.SCAN");
            intent.putExtra("SAVE_HISTORY", false);
            startActivityForResult(intent, 0);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, CAM_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (permissions.length > i && permissions[i].equals(CAMERA_PERMISSION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    onClick(scanButton);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            Intent intent = new Intent(this, ResultScreen.class);
            intent.putExtra("data", contents);
            startActivity(intent);
        }
    }
}
