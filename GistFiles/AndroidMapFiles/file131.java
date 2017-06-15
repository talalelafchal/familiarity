package com.example.nicholasthompson.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.net.Uri;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void go_cab(View view){

        Intent intent = new Intent(this, CabActivity.class);
        startActivity(intent);

    }
    public void go_cock(View view){

        Intent intent = new Intent(this, CockActivity.class);
        startActivity(intent);

    }
    public void go_fav(View view){

        Intent intent = new Intent(this, FavActivity.class);
        startActivity(intent);

    }
    public void go_group(View view){

        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    public void go_scan(View view){

        Uri uri = Uri.parse("http://zxing.appspot.com/scan");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void go_site(View view){

        Uri uri = Uri.parse("http://google.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
