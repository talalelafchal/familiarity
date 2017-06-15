package com.example.fm;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
 

public class Menus extends Activity 
{
	Button menuBtnAdvance,menuBtnPayment,menuBtnVTracking,menuBtnDashboard,menuBtnPHistory,menuBtnMVehicles;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		menuBtnAdvance=(Button) findViewById(R.id.MenuBtnAdv);
		menuBtnPayment=(Button) findViewById(R.id.MenuBtnPay);
		menuBtnVTracking=(Button) findViewById(R.id.MenuBtnTra);
		menuBtnDashboard=(Button) findViewById(R.id.MenuBtnDas);
		menuBtnPHistory=(Button) findViewById(R.id.MenuBtnPayHist);
		menuBtnMVehicles=(Button) findViewById(R.id.MenuBtnMan);


		menuBtnAdvance.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent=new Intent(getApplicationContext(),Advance.class);
						startActivity(intent);
					}
				});


//Payment Button
        menuBtnPayment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO PAYMENT HANDLING
            }
        });

        //Trackin button

        menuBtnVTracking.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Tracking.class);
                startActivity(intent);
            }
        });


    //DashBoard button
        menuBtnDashboard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent);
            }
        });


        //Payment History Button
        menuBtnPHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PaymentHistory.class);
                startActivity(intent);
            }
        });

        //Manage Button
        menuBtnMVehicles.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Manage.class);
                startActivity(intent);
            }
        });

    }



}