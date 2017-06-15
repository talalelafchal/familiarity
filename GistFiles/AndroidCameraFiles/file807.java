package com.test.testbed;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.FocusMode;
import com.commonsware.cwac.cam2.ZoomStyle;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraActivity.IntentBuilder b = new CameraActivity.IntentBuilder(this);
        b.skipConfirm()
                .to(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"cwactest.jpg"))
                .zoomStyle(ZoomStyle.SEEKBAR)
                .focusMode(FocusMode.CONTINUOUS)
                .updateMediaStore()
                .allowSwitchFlashMode();
        Intent i = b.build();

        startActivityForResult(i,58967);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 58967 && resultCode == RESULT_OK) {
            Log.d("TESTBED",data.getData().toString());
        }
    }
}
