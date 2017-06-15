package com.example.denis.secondprogram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by denis on 9/11/14.
 */
public class RegistratinForm extends Activity implements OnClickListener{

    EditText login, password, email;
    Button signUp;
    CheckBox checkBox;

    static boolean check;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        login = (EditText)findViewById(R.id.loginInput);
        password = (EditText)findViewById(R.id.passwordInput);
        email = (EditText)findViewById(R.id.emailInput);

        signUp = (Button) findViewById(R.id.signUpInRegistration);

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        signUp.setOnClickListener(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    check = true;
                else
                    check = false;
            }
        });

    }

    @Override
    public void onClick(View view) {

        if(checkForEmpty()) {

            if(view.getId() == R.id.signUpInRegistration) {
                if(!check)
                    Toast.makeText(this,"Agree with rules.",Toast.LENGTH_SHORT).show();

                else {

                    Database.setLogins(login.getText().toString());
                    Database.setPasswords(password.getText().toString());
                    Database.setEmails(email.getText().toString());
                    Intent intent = new Intent(RegistratinForm.this, MainActivity.class);
                    startActivity(intent);

                }

            }
        }
        else
            Toast.makeText(this, "Enter the fields", Toast.LENGTH_LONG).show();


    }

    boolean checkForEmpty() {
        if(TextUtils.isEmpty(login.getText().toString())||
                TextUtils.isEmpty(password.getText().toString())
                     ||TextUtils.isEmpty(email.getText().toString()))
            return false;
        return true;
    }

}
