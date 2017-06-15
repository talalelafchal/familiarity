package com.example.fm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by MKSoft01 on 10/17/13.
 */
public class VehicleManagement extends Activity {



    ImageButton InsuButton,Permitbutton,ExploButtton,PCButton,TaxButton;
    Button ManVehbtnNewV,manVBtnSave,manVBtncancel;
    EditText ManVehVNo,ManVehVType,ManVehVInsu,ManVehVPermit,ManVehVExplo,ManVehVPC,ManVehVTax;
    AnandDB db;
    Calendar c=Calendar.getInstance();
    String UzrDate;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vehicles);
        final CheckBox InsucheckBox = (CheckBox) findViewById(R.id.ManVehCbInsu);
        InsuButton=(ImageButton)findViewById(R.id.manVImBtnInsu);
        Permitbutton=(ImageButton)findViewById(R.id.manVImBtnPermit);
        ExploButtton=(ImageButton)findViewById(R.id.manVImBtnExplo);
                PCButton=(ImageButton)findViewById(R.id.manVImBtnPc);
                TaxButton=(ImageButton)findViewById(R.id.manVImBtnTax);


//        InsucheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                //    MyDatePickerDialog();
//                }
//            }
//        });

        db=new AnandDB(this);
        db.open();
        ManVehbtnNewV=(Button)findViewById(R.id.ManVehbtnNewV);

        manVBtnSave=(Button)findViewById(R.id.manVBtnSave);
        manVBtncancel=(Button)findViewById(R.id.manVBtncancel);

        ManVehVNo=(EditText)findViewById(R.id.ManVehVehNum);
        ManVehVType=(EditText)findViewById(R.id.ManVehEtVehType);
        ManVehVInsu=(EditText)findViewById(R.id.ManVehEtInsu);
        ManVehVPermit=(EditText)findViewById(R.id.ManVehEtPermit);
        ManVehVExplo=(EditText)findViewById(R.id.ManVehEtExplo);
        ManVehVPC=(EditText)findViewById(R.id.ManVehEtPc);
        ManVehVTax=(EditText)findViewById(R.id.MAnVehEtTax);

        InsuButton.setOnClickListener(new View.OnClickListener() {

    public void onClick(View arg0) {
        MyDatePickerDialog();
        }
});
        Permitbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //TODO Auto
            }
        });
        ExploButtton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //TODO Auto
            }
        });
        PCButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //TODO Auto

            }
        });
        TaxButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
               //TODO Auto

            }
        });




        manVBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Get the email and password and save it to Database into a seperate table (for retrieval)

                String VehNo = ManVehVNo.getText().toString();
                String VehType = ManVehVType.getText().toString();
                String VehInsu = ManVehVInsu.getText().toString();
                String VehPer = ManVehVPermit.getText().toString();
                String VehExplo = ManVehVExplo.getText().toString();
                String VehPC = ManVehVPC.getText().toString();
                String VehTax = ManVehVTax.getText().toString();


                if (VehNo.equals("") || VehType.equals("") || VehInsu.equals("") || VehPer.equals("") || VehExplo.equals("") || VehPC.equals("") || VehTax.equals("")) {
                    Toast.makeText(getApplicationContext(), "Fields Cannot be Empty", 0).show();
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put("KEY_Vehicle_No", VehNo);
                    cv.put("KEY_Vechicle_Type", VehType);
                    cv.put("KEY_Veh_Insu", VehInsu);
                    cv.put("KEY_Veh_Permit", VehPer);
                    cv.put("KEY_Veh_Explo", VehExplo);
                    cv.put("KEY_Veh_PC", VehPC);
                    cv.put("KEY_Veh_Tax", VehTax);
                    db.Insertointotable(cv, AnandDB.T_ManageVehicle);
                    Intent intent = new Intent(getApplicationContext(), Manage.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Data Saved Successfull", 0).show();
                }
            }
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
            ManVehVInsu.setText(UzrDate);

        }
    };


}
