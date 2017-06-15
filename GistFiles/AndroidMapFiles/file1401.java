package com.tuguldur.z.ex7arr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tuguldur.z.ex7arr.data.Data;

public class Login extends AppCompatActivity {
    String name1, pass1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();

        name1 = getIntent().getStringExtra("name");
        pass1 = getIntent().getStringExtra("pass");

        check();



          }


    public void check() {
        Boolean check=false;
        Data data = new Data();
        for (int i = 0; i < data.name.length; i++) {
            if (data.name[i].equals(name1) && data.pass[i].equals(pass1)) {
               check=true;
                break;
            }
        }
        if(check==true){
            Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    }

