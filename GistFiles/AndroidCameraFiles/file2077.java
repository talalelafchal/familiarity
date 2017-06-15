package com.example.fm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class DriverAdvance extends Activity 
{
	Button daBtnDate,daBtnPay;
	EditText daEtVoucherNum,daEtAmount;
	Spinner daSpDName,daSpVeh;
	TextView daTvDate;
	ArrayList<String> alDName,alVNum;
	 ArrayAdapter<String> aaD,aaV;
	// SimpleCursorAdapter sca;
	 AnandDB db;
	 String DName,VName;
	 String UzrDate;
	 Cursor cur;

	Calendar c=Calendar.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_advance);
		db=new AnandDB(this);
		db.open();
//		List<String> listDName=db.quary();
//		List<String> listvName=db.quaryvechicleno();
		daBtnDate=(Button) findViewById(R.id.daBtnDate);
		daBtnPay=(Button) findViewById(R.id.daBtnPay);
		daTvDate=(TextView) findViewById(R.id.daTvDate);
		daEtVoucherNum=(EditText) findViewById(R.id.daEtVoucherNum);
		daEtAmount=(EditText) findViewById(R.id.daEtAmount);
		
		daSpDName=(Spinner) findViewById(R.id.daSpDName);
		daSpVeh=(Spinner) findViewById(R.id.daSpVeh);
		//Cursor c=db.quary();
		alDName=new ArrayList<String>();
		alVNum=new ArrayList<String>();
//		aaD=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listDName);
//		aaV=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listvName);
		//sca=new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c, "Driver_name", alDName);
		daSpDName.setAdapter(aaD);
		daSpVeh.setAdapter(aaV);
		
		
		
		daBtnDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				MyDatePickerDialog();
				 
			}
		});
		daSpDName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				DName=parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		daSpVeh.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				VName=parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	
		daBtnPay.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) { 
						//String date=daTvDate.getText().toString(); 
						String VoucherNum=daEtVoucherNum.getText().toString();
						String Amount=daEtAmount.getText().toString();
						if( VoucherNum.equals("") || Amount.equals(""))
						{
							Toast.makeText(getApplicationContext(), "Enter all the values", 0).show();
						}
						else
						{
							ContentValues cv=new ContentValues();
							cv.put("Date", UzrDate);
							cv.put("Vechicle_no", VName);
							cv.put("Driver_name", DName);
							//cv.put("Cleaner_name", CName);
							cv.put("Voucher_no", VoucherNum);
							cv.put("Amount", Amount);
							db.Insertointotable(cv, AnandDB.T_Driveradv);
						
					}}
				});
		
	}
	public void MyDatePickerDialog() 
	{		
		 DatePickerDialog dp=new DatePickerDialog(this,dt,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
		 dp.show();		 
	}
	public DatePickerDialog.OnDateSetListener dt=new DatePickerDialog.OnDateSetListener() 
	{
		 @Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) 
		{
			 UzrDate=dayOfMonth+"-"+(monthOfYear+1)+"-"+year; 			 
			if(dayOfMonth<10 && monthOfYear<9)
				UzrDate="0"+dayOfMonth+"-0"+(monthOfYear+1)+"-"+year;
			else
			{
				if(dayOfMonth<10)
					UzrDate="0"+dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
				if(monthOfYear<9)
					UzrDate=dayOfMonth+"-0"+(monthOfYear+1)+"-"+year;
			}
			daTvDate.setText(UzrDate);
			
		}
	};
	 

}
