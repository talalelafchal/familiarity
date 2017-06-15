package com.example.denis.secondprogram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

    private EditText login, password;
    Button signIn, signUp;
    TextView message;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        setContentView(R.layout.activity_1);

        login = (EditText) findViewById(R.id.Login);
        password = (EditText) findViewById(R.id.Password);

        message = (TextView) findViewById(R.id.messageText);

        signIn = (Button)findViewById(R.id.signInButton);
        signUp = (Button)findViewById(R.id.signUpButton);

        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(1,1,1,"Settings");
        menu.add(1,2,2,"Sign Up");
        menu.add(1,3,3,"Forgot data");
        menu.add(1,4,4,"Exit");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int i, MenuItem item) {

        int id=0;

        switch (item.getItemId()) {

            case 1:
                id = 1;

                break;
            case 2:
                id = 2;
                Intent intent = new Intent(MainActivity.this, RegistratinForm.class);
                startActivity(intent);
                break;
            case 3:
                id = 3;

                break;
            case 4:
                id = 4;

                break;
        }

        return super.onMenuItemSelected(id, item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.signInButton:
            if(!checkForEmpty())
                Toast.makeText(this,"Не заполнены все поля",Toast.LENGTH_SHORT).show();
            else {
                if(autorization()) {
                    message.setText("Логин и пароль успешно приняты");
                    Intent intent = new Intent(MainActivity.this, MainScreen.class);
                    startActivity(intent);
                }
                else
                    message.setText("Логин или пароль - неверны.");
            }
                break;

            case R.id.signUpButton:
                Intent intent = new Intent(MainActivity.this, RegistratinForm.class);
                startActivity(intent);
                break;

        }


    }

    boolean checkForEmpty() {
        if(TextUtils.isEmpty(login.getText().toString())||
                TextUtils.isEmpty(password.getText().toString()))
            return false;
        return true;
    }

    public boolean autorization() {

        for (int i = 0; i < Database.getSize(); i++) {
            if(Database.getLogin(i).equals(login.getText().toString())
                    && Database.getPassword(i).equals(password.getText().toString()))
                return true;
        }
        return false;
    }

}