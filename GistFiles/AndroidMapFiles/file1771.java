package com.finepointmobile.hashmaptutorialblog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private HashMap<String, String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mData = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            mData.put("key_" + i, "value_" + i);
        }

        Log.d(TAG, "onCreate: " + mData);
    }
}
