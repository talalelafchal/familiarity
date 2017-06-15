package com.example.omdb.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.example.omdb.app" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void onClick(View view)
    {
        Intent intent=new Intent(this, moviename.class);
        EditText eT = (EditText) findViewById(R.id.movie);
        String message = eT.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }




}
