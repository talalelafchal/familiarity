package com.healthiot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicalDetails extends AppCompatActivity {

    TextView OpLabel,DoctorLabel,BloodLabel,DiseaseLabel,ReportLabel,ConsultLabel,op,doctor,blood,disease,report,consult,error,headinglabel;
    int height=0,width=0;
    RelativeLayout relativeLayout;
    String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_details);

        //Getting screen size

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Linking Components

        OpLabel = (TextView) findViewById(R.id.OpLabel);
        DoctorLabel = (TextView) findViewById(R.id.DoctorLabel);
        BloodLabel = (TextView) findViewById(R.id.BloodLabel);
        DiseaseLabel = (TextView) findViewById(R.id.DiseaseLabel);
        ReportLabel = (TextView) findViewById(R.id.ReportLabel);
        ConsultLabel = (TextView) findViewById(R.id.ConsultLabel);
        op = (TextView) findViewById(R.id.op);
        doctor = (TextView) findViewById(R.id.docname);
        blood = (TextView) findViewById(R.id.blood);
        disease = (TextView) findViewById(R.id.disease);
        report = (TextView) findViewById(R.id.report);
        consult = (TextView) findViewById(R.id.consult);
        headinglabel = (TextView) findViewById(R.id.HeadingLabel);
        error = (TextView) findViewById(R.id.error);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        //Setting Size

        op.setWidth((int)(0.30*width));
        doctor.setWidth((int) (0.30 * width));
        blood.setWidth((int)(0.30*width));
        disease.setWidth((int)(0.30*width));
        report.setWidth((int)(0.30*width));
        consult.setWidth((int)(0.30*width));

        OpLabel.setWidth((int) (0.50 * width));
        DoctorLabel.setWidth((int)(0.50*width));
        BloodLabel.setWidth((int)(0.50*width));
        DiseaseLabel.setWidth((int)(0.50*width));
        ReportLabel.setWidth((int)(0.50*width));
        ConsultLabel.setWidth((int)(0.50*width));
        headinglabel.setWidth((int)(0.80*width));
        headinglabel.setHeight((int) (0.10 * height));
        error.setHeight((int)(0.20*height));

        Intent i = getIntent();
        Bundle b = i.getExtras();
        user = b.getString("user");

        DatabaseHelper d = new DatabaseHelper(getApplicationContext());

        if(d.check_patient())
        {
            ArrayList<String> al = d.get_patient_medical_details();

            if(al.size()!=0) {
                op.setText(al.get(0));
                doctor.setText(al.get(1));
                blood.setText(al.get(2));
                disease.setText(al.get(3));
                report.setText(al.get(4));
                consult.setText(al.get(5));
            }
            else
            {
                relativeLayout.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
            }

        }
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_user_frofile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == android.R.id.home)
        {
            Intent i = new Intent(MedicalDetails.this,ViewUserFrofile.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
        }

        else if(id == R.id.book)
        {
            Intent i = new Intent(MedicalDetails.this,BookAppointment.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();

            return true;
        }

        else if (id == R.id.userprofile) {

            Intent i = new Intent(MedicalDetails.this,ViewUserFrofile.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.sensors)
        {
            Intent i = new Intent(MedicalDetails.this,ViewSensors.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }

        else if(id == R.id.chat)
        {
            Intent i = new Intent(MedicalDetails.this,ChatActivity.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.chatsend)
        {
            Intent i = new Intent(MedicalDetails.this,ChatWindow.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.munit)
        {

            return true;
        }
        else if(id == R.id.logout)
        {
            Intent i = new Intent(MedicalDetails.this,MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
