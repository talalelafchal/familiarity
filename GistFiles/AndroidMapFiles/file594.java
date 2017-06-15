package com.gmail.fedorenko.kostia.app1lesson1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final HashMap<String, String> LogDB = new HashMap<String, String>();
        LogDB.put("login","password");
        LogDB.put("login1","password1");


        Button button = (Button) findViewById(R.id.click);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextLogin = (EditText) findViewById(R.id.enter_login);
                EditText editTextPassword = (EditText) findViewById(R.id.enter_password);
                TextView textView = (TextView) findViewById(R.id.testtext);
                String login = editTextLogin.getText().toString();
                String password = editTextPassword.getText().toString();
                if (editTextLogin.getText().toString().isEmpty()||editTextPassword.getText().toString().isEmpty()){
                    textView.setText("Please enter login/password");
                }
                else if (LogDB.containsKey(login)&&LogDB.get(login).equals(password)){
                    textView.setText("Login and password are valid!");
                }
                else {
                    textView.setText("Login and password are invalid!");
                }
            }
        });
    }
}