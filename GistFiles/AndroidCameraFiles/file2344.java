package com.example.fm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AddAdvance extends Activity
{
	Button vaBtnDate,vaBtnPay;
	EditText vaEtVNum,vaEtDName,vaEtCName,vaEtVoucherNum,vaEtAmount;
	TextView vaTvDate;
Calendar c= Calendar.getInstance();
	AnandDB db;

	public static final int Dtplr_Dlg=1;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_adavance);
		db=new AnandDB(this);
		db.open();
//		vaEtVNum=(EditText) findViewById(R.id.vaEtVNum);
////		vaEtDName=(EditText) findViewById(R.id.vaEtDName);
//////		vaEtCName=(EditText) findViewById(R.id.vaEtCName);
//////		vaEtVoucherNum=(EditText) findViewById(R.id.vaEtVoucherNum);
//////		vaEtAmount=(EditText) findViewById(R.id.vaEtAmount);
//////
//////		vaTvDate=(TextView) findViewById(R.id.vaTvDate);
//////
//		vaBtnDate=(Button) findViewById(R.id.vaBtnDate);
////		vaBtnPay=(Button) findViewById(R.id.vaBtnPay);
//		vaBtnDate.setOnClickListener(new View.OnClickListener() {
////
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View v) {
//				MyDatePickerDialog() ;
//				showDialog(Dtplr_Dlg);
//			//	vaTvDate.setText()
////
//			}
//		});
//
//		vaBtnPay.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						String date=vaTvDate.getText().toString();
//						String VNum=vaEtVNum.getText().toString();
//						String DName=vaEtDName.getText().toString();
//						String CName=vaEtCName.getText().toString();
//						String VoucherNum=vaEtVoucherNum.getText().toString();
//						String Amount=vaEtAmount.getText().toString();
//						if(date.equals("") || VNum.equals("") || DName.equals("") || CName.equals("") || VoucherNum.equals("") || Amount.equals(""))
//						{
//							Toast.makeText(getApplicationContext(), "Enter all the values", 0).show();
//						}
//						else
//						{
//							ContentValues cv=new ContentValues();
//							cv.put("Date", date);
//							cv.put("Vechicle_no", VNum);
//							cv.put("Driver_name", DName);
//							cv.put("Cleaner_name", CName);
//							cv.put("Voucher_no", VoucherNum);
//							cv.put("Amount", Amount);
//							db.Insertointotable(cv, AnandDB.T_vechicleadv);
//							Intent intent=new Intent(getApplicationContext(),Advance.class);
//							startActivity(intent);
//						}
//					}
//				});
//	}
//		public void MyDatePickerDialog()
//		{
//
//			 DatePickerDialog dp=new DatePickerDialog(this,dt,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
//			 dp.show();
//		}
//		private DatePickerDialog.OnDateSetListener dt=new DatePickerDialog.OnDateSetListener()
//		{
//			 @Override
//			public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth)
//			{
//				String UzrDate=dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
//				if(dayOfMonth<10 && (monthOfYear+1)<10)
//					UzrDate="0"+dayOfMonth+"-0"+(monthOfYear+1)+"-"+year;
//				else
//				{
//					if(dayOfMonth<10)
//						UzrDate="0"+dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
//					if((monthOfYear+1)<10)
//						UzrDate=dayOfMonth+"-0"+(monthOfYear+1)+"-"+year;
//				}
//				vaTvDate.setText(UzrDate);
//
			}
//		};
//

}
