package com.example.fm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Registration extends Activity
{

	Button regBtnRegister;
	EditText regEtName,regEtEmail,regEtPhno,regEtPin,regEtConfPin;
    AnandDB db;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
        db=new AnandDB(this);
        db.open();

        regBtnRegister=(Button) findViewById(R.id.regBtnRegister);
        regEtName=(EditText) findViewById(R.id.regEtName);
        regEtEmail=(EditText) findViewById(R.id.regEtEmail);

        regEtPhno=(EditText) findViewById(R.id.regEtPhno);
        regEtPin=(EditText) findViewById(R.id.regEtPinNumber);
        regEtConfPin=(EditText) findViewById(R.id.regEtConfPin);



        regBtnRegister.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				// Get the email and password and save it to Database into a seperate table (for retrieval)

                        String Name=regEtName.getText().toString();

                String Email=regEtEmail.getText().toString();
                String Phone=regEtPhno.getText().toString();
                String Pin=regEtPin.getText().toString();
                String ConfPin=regEtConfPin.getText().toString();

                if(Name.equals("") || Email.equals("") || Phone.equals("")|| Pin.equals("") || ConfPin.equals("")  )
                        {
                            Toast.makeText(getApplicationContext(), "Fields Cannot be Empty", 0).show();
                        }
                        else if(Pin.equals(ConfPin))
                        {
                            ContentValues cv=new ContentValues();
                            cv.put("KEY_Reg_Name", Name);
                            cv.put("KEY_Email", Email);
                            cv.put("KEY_Phone", Phone);
                            cv.put("KEY_Pin", Pin);
                            db.Insertointotable(cv, AnandDB.T_Registration);
                            Intent intent=new Intent(getApplicationContext(),Login.class);
                            startActivity(intent);
                        }
                            else{ Toast.makeText(getApplicationContext(), "Password Doesnt Match", 0).show();}
                }
                });
            }

			}


	 



