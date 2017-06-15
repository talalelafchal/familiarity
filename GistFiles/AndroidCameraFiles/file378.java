package com.example.fm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.Toast;

public class Advance extends Activity 
{
	Button advBtnVehicle;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advance);
		advBtnVehicle=(Button) findViewById(R.id.advBtnVehicle);

		advBtnVehicle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"CLicked",Toast.LENGTH_LONG).show();
				Intent intent=new Intent(getApplicationContext(),AddAdvance.class);
				startActivity(intent);
				
			}
		});
	}
	
	 
 	
	 

}
