package com.healthiot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ViewUserProfile extends AppCompatActivity {

    String user = "",n= "",ad= "",ag= "",con= "",aa= "";
    TextView name,address,age,contact,aadhar,namelabel,addresslabel,agelabel,contactlabel,aadharlabel,headinglabel,error;
    public static final String URL = "http://patientmonitoringsys.comli.com/patientmonitor/admin/view_patient_status.php";
    ArrayList<String> al = new ArrayList<String>();
    int height=0,width=0;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_frofile);

        //Getting screen size

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;


        //Linking Components

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        age = (TextView) findViewById(R.id.age);
        contact = (TextView) findViewById(R.id.contact);
        aadhar = (TextView) findViewById(R.id.aadhar);
        namelabel = (TextView) findViewById(R.id.NameLabel);
        agelabel = (TextView) findViewById(R.id.AgeLabel);
        addresslabel = (TextView) findViewById(R.id.AddressLabel);
        contactlabel = (TextView) findViewById(R.id.ContactLabel);
        aadharlabel = (TextView) findViewById(R.id.AadharLabel);
        headinglabel = (TextView) findViewById(R.id.HeadingLabel);
        error = (TextView) findViewById(R.id.error);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        //Setting Size

        name.setWidth((int)(0.30*width));
        address.setWidth((int)(0.30*width));
        age.setWidth((int)(0.30*width));
        contact.setWidth((int)(0.30*width));
        aadhar.setWidth((int)(0.30*width));

        namelabel.setWidth((int)(0.50*width));
        addresslabel.setWidth((int)(0.50*width));
        agelabel.setWidth((int)(0.50*width));
        contactlabel.setWidth((int)(0.50*width));
        aadharlabel.setWidth((int)(0.50*width));
        headinglabel.setWidth((int)(0.80*width));
        headinglabel.setHeight((int) (0.10 * height));
        error.setHeight((int)(0.20*height));

        Intent i = getIntent();
        Bundle b = i.getExtras();
        user = b.getString("user");

        DatabaseHelper d = new DatabaseHelper(getApplicationContext());


        if(d.check_patient())
        {
            ArrayList<String> al = d.get_patient_details();

                name.setText(al.get(0));
                address.setText(al.get(1));
                age.setText(al.get(2));
                contact.setText(al.get(3));
                aadhar.setText(al.get(4));



        }
        else if(isNetworkAvailable()) {

            MyTask m = new MyTask();
            m.execute();

        }
        else
        {
            relativeLayout.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        }



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
        if (id == R.id.userprofile) {


            return true;
        }

        else if(id == R.id.munit)
        {
            Intent i = new Intent(ViewUserFrofile.this,MedicalDetails.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.chat)
        {
            Intent i = new Intent(ViewUserFrofile.this,ChatActivity.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.book)
        {
            Intent i = new Intent(ViewUserFrofile.this,BookAppointment.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();

            return true;
        }
        else if(id == R.id.sensors)
        {
            Intent i = new Intent(ViewUserFrofile.this,ViewSensors.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.chatsend)
        {
            Intent i = new Intent(ViewUserFrofile.this,ChatWindow.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.logout)
        {
            Intent i = new Intent(ViewUserFrofile.this,MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //JSON Class for Update

    public void samp(){


        final String TAG = " View Details";

        String url = URL+"?req=patient_details&user="+user;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                StringTokenizer st = new StringTokenizer(response,"***");
                while(st.hasMoreTokens())
                {
                    String s= st.nextToken();
                    al.add(s);
                }

                    int op = Integer.parseInt(al.get(0));
                    n = al.get(1);
                    ad = al.get(2);
                    con = al.get(3);
                    String lastc = al.get(4);
                    ag = al.get(5);

                    String bldgrp = al.get(6);
                    String dob = al.get(7);
                    String report = al.get(8);
                    String doc_name = al.get(9);
                    String disease = al.get(10);
                    aa = al.get(11);


                    name.setText(n);
                    address.setText(ad);
                    contact.setText(con);
                    age.setText(ag);
                    aadhar.setText(aa);

                    DatabaseHelper d = new DatabaseHelper(getApplicationContext());

                    if(d.check_patient())
                    {
                        Toast.makeText(getApplicationContext(),"Already Available!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        long l = d.insert_patient(op, n, ad, con, lastc, ag, bldgrp, dob, report, doc_name, disease, aa);

                        if (l > -1) {
                            Toast.makeText(getApplicationContext(), "Insertion Successfull!", Toast.LENGTH_SHORT).show();
                        }
                    }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(strReq);
    }
    //Checking network connection

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog Asycdialog = new ProgressDialog(ViewUserFrofile.this);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Asycdialog.setMessage("Loading please wait...");
            Asycdialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // do the task you want to do. This will be executed in background.

            samp();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            Asycdialog.dismiss();

        }
    }

}
