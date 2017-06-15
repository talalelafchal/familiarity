package com.amtera.crudapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import com.amtera.dao.UserDAO;
import com.amtera.domain.User;


public class RegisterActivity extends Activity
{
	private EditText txtLoginRegister;
	private EditText txtPasswdRegister;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
    }

    public void register(View view){
    	this.txtLoginRegister = (EditText) findViewById(R.id.txtLoginRegister);
    	this.txtPasswdRegister = (EditText) findViewById(R.id.txtPasswdRegister);
    	
    	try{
			User user = new User(); 
			user.setLogin(this.txtLoginRegister.getText().toString());  		
    		user.setPassword(this.txtPasswdRegister.getText().toString());
    		UserDAO u = new UserDAO(this.getApplicationContext());
    		String result;

    		if (u.add(user)){
    			result = "Success";
    		}
    		else{
				result = "Fail";
			}
    		Toast toast = Toast.makeText(this, result, Toast.LENGTH_SHORT);
    		toast.show();
    		u.closeConnection();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}

    }
}
