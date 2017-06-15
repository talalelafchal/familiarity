package com.example.fm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPassword extends Activity 
{
	Button fpBtnRetrieve;
	EditText fpEtEmail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		fpBtnRetrieve=(Button) findViewById(R.id.fpBtnRetrieve);
		fpEtEmail=(EditText) findViewById(R.id.fpEtEmail);
		
		fpBtnRetrieve.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				 // Get the String from database and send it to Email.
				
			}
		});
	}

	 

}
