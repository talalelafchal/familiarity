package com.example.omdb.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.text.TextUtils;
import android.widget.TextView;


public class moviename extends Activity {
    int i,j;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        StringBuilder converted=new StringBuilder(message);
        for(i=0;i<message.length();i++)
        {
        if(message.charAt(i) == 'a'||message.charAt(i) =='A')
          converted.setCharAt(i,'A');
        else  if(message.charAt(i) == 'e'||message.charAt(i) =='E')
            converted.setCharAt(i,'E');
        else  if(message.charAt(i) == 'i'||message.charAt(i) =='I')
            converted.setCharAt(i,'I');
        else  if(message.charAt(i) == 'o'||message.charAt(i) =='O')
            converted.setCharAt(i,'O');
        else  if(message.charAt(i) == 'u'||message.charAt(i) =='U')
            converted.setCharAt(i,'U');
        else  if(message.charAt(i) == ' ')
            converted.setCharAt(i,'/');
        else converted.setCharAt(i,'؀');

        }
        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setText(converted);

        // Set the text view as the activity layout
        setContentView(textView);

    }



}
