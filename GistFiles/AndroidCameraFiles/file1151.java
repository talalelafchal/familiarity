package com.example.fm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity 
{

    EditText lpEtPin,Pin;
	Button lpBtnLogin,lpBtnForget;
    AnandDB db;

    //	String pin;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        db=new AnandDB(this);
        db.open();

        lpEtPin=(EditText) findViewById(R.id.lpEtPin);
		lpBtnLogin=(Button) findViewById(R.id.lpBtnLogin);
		lpBtnForget=(Button) findViewById(R.id.lpBtnForget);                                            
		lpBtnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Do the validation for EditText and go to Menu Screen

                String Pin=lpEtPin.getText().toString();

  				if (Pin.equals("")){
                    Toast.makeText(getApplicationContext(), "Pin Cannot Be Empty", 0).show();
                    }
//				else //if(pin.equals())
//				{
////                   Cursor c = db.getContact(Pin);
////                    if (c.moveToFirst())
//////                        DisplayContact(c);
//////                    else
////                        Toast.makeText(getApplicationContext(), “No contact found”, Toast.LENGTH_LONG).show();
//
                    Intent intent=new Intent(getApplicationContext(),Menus.class);
				startActivity(intent);
//				}

			}
		});
		
		lpBtnForget.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),RetrievePassword.class);
                        startActivity(intent);

                    }
				});
	}


}
