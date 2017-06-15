package com.vesicant.ActivityTest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


/**
 * Created with IntelliJ IDEA.
 * User: vesicant
 * Date: 13/03/13
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class DisplayMessageActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        //String userName = intent.getStringExtra(Main.USER_NAME);
        //String userPass = intent.getStringExtra(Main.USER_PASS);

        String restResult = null;
    }
}
