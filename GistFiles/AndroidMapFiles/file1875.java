package com.example.HelloWorld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MyActivity extends Activity {
    private static final int REQUEST_LOAD = 0;
    private static final int REQUEST_SAVE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final TextView answerView = (TextView)findViewById(R.id.textView);
        Button getAnswerButton = (Button) findViewById(R.id.button1);
        getAnswerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                answerView.setText("The click worked.");
            }
        });
    }
}
