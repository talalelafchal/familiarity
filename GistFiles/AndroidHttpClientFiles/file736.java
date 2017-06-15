package com.amtera.crudapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.amtera.dao.UserDAO;
import com.amtera.domain.User;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void makeLogin(View view){
    	EditText txtLogin = (EditText) findViewById(R.id.txtLogin);
    	EditText txtPasswd = (EditText) findViewById(R.id.txtPasswd);

        try{
            User user = new User();
            user.setLogin(txtLogin.getText().toString());
            user.setPassword(txtPasswd.getText().toString());
            UserDAO u = new UserDAO(this.getApplicationContext());
            String result;
            boolean cond = u.loginCheck(user);

            if (cond){
                Intent intent = new Intent(this, ListUserActivity.class);
                startActivity(intent);
            }
            else{
                Toast toast = Toast.makeText(this, "Fail", Toast.LENGTH_SHORT);
                toast.show();

            }

            u.closeConnection();
        }
        catch(Exception e){
            e.printStackTrace();
        }



    }

    public void newRegister(View view){
    	Intent intent = new Intent(this, RegisterActivity.class);
    	startActivity(intent);
    }
}
