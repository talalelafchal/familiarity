package com.example.denis.secondprogram;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by denis on 9/11/14.
 */
public class MainScreen extends Activity {

    TextView info;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        info = (TextView)findViewById(R.id.info);
        info.setText("Hello, " + Database.getLogin(0) +
                ", you got to main Screen. Your password is: " +
                    Database.getPassword(0)+". Your e-mail is: "+Database.getEmail(0));

    }
}
