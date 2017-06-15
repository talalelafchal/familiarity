package com.example.fm;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ManageDrivers extends Activity {
    Button regBtnRegister;
    EditText DriverName, Type,ContactNo, LicenseNumber, Address,Insurance,LicenseEndDate;
    AnandDB db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_drivers);
        db=new AnandDB(this);
        db.open();

DriverName=(EditText)findViewById(R.id.ManDriDriName);
        Type=(EditText)findViewById(R.id.ManDriDriType);
        ContactNo=(EditText)findViewById(R.id.ManDriPhNo);
        LicenseNumber=(EditText)findViewById(R.id.ManDriLic);
        LicenseEndDate=(EditText)findViewById(R.id.ManDriLicEndDate);
        Address=(EditText)findViewById(R.id.ManDriAddr);
Insurance=(EditText)findViewById(R.id.ManDriInsu);

regBtnRegister=(Button)findViewById(R.id.regBtnRegister);
    regBtnRegister.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View arg0)
        {
            // Get the email and password and save it to Database into a seperate table (for retrieval)

            String driver=DriverName.getText().toString();
            String type=Type.getText().toString();
            String contactNo=ContactNo.getText().toString();
            String licenseno=LicenseNumber.getText().toString();
            String licenseend=LicenseEndDate.getText().toString();
            String address= Address.getText().toString();
            String insurance=Insurance.getText().toString();




                ContentValues cv=new ContentValues();
                cv.put("Name",driver);
            cv.put("Type",type);
            cv.put("Contact",contactNo);
            cv.put("LicenseNo",licenseno);

            cv.put("LicenseEnd",licenseend);
            cv.put("Insurance",insurance);
            cv.put("Address",address);

            db.Insertointotable(cv, AnandDB.T_ManageDriver);
            }

    });
}
	 

}
