package com.example.fm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Manage extends Activity {
    Button BtnVehMan,BtnDriMan;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);

BtnVehMan=(Button)findViewById(R.id.manBtnVeh);
        BtnDriMan=(Button)findViewById(R.id.manBtnDri);


        BtnVehMan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),VehicleManagement.class);
                startActivity(intent);
            }
        });


        //Payment History Button
        BtnDriMan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DriverManagement.class);
                startActivity(intent);
            }
        });
    }

	 

}
