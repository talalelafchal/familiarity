package com.example.loginandimageupload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
 
 
public class DonorLogin extends Activity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
 
        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.USER_NAME);
 
        TextView textView = (TextView) findViewById(R.id.textView3);
 
        textView.setText("Welcome "+username);
    }
 
   
}