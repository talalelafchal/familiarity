package com.healthiot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class ChatWindow extends AppCompatActivity {

    EditText msg;
    Button send;
    int height=0,width=0;
    String message = "",user="",snddate="";
    public static final String URL = "http://patientmonitoringsys.comli.com/patientmonitor/admin/view_patient_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        //Linking Components

        msg = (EditText) findViewById(R.id.msg);
        send = (Button) findViewById(R.id.send);

        //Getting screen size

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Setting Size to Components

        msg.setWidth((int) (0.80*width));
        send.setWidth((int)(0.50*width));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Date d = new Date();

                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

                String dd = sd.format(d);

                snddate = dd;
                message = msg.getText().toString();

                msg.setText("");

                MyTask m = new MyTask();
                m.execute();

            }
        });

        //Getting Intent

        Intent i = getIntent();
        Bundle b = i.getExtras();
        user = b.getString("user");


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


    }


    class MyTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog Asycdialog = new ProgressDialog(ChatWindow.this);

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

    //JSON Class for Update

    public void samp(){


        final String TAG = " View Chat Details";
        ArrayList<String> temp = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(snddate," ");
        while(st.hasMoreTokens())
        {
            temp.add(st.nextToken());
        }
        //Toast.makeText(getApplicationContext(),temp.get(0)+"  "+temp.get(1),Toast.LENGTH_LONG).show();
        final String url = URL+"?req=chat&user="+user+"&msg="+message+"&date="+temp.get(0)+"&time="+temp.get(1)+"&status="+temp.get(2);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatWindow.this);
                alertDialogBuilder.setTitle("Chat Message");
                alertDialogBuilder.setMessage("Message Sent");
                alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                    }
                });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();




            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(strReq);
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
            Intent i = new Intent(ChatWindow.this,ViewUserFrofile.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
        }

        else if(id == R.id.book)
        {
            Intent i = new Intent(ChatWindow.this,BookAppointment.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();

            return true;
        }

        else if (id == R.id.userprofile) {

            Intent i = new Intent(ChatWindow.this,ViewUserFrofile.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }

        else if(id == R.id.sensors)
        {
            Intent i = new Intent(ChatWindow.this,ViewSensors.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.chatsend)
        {

            return true;
        }
        else if(id == R.id.chat)
        {
            Intent i = new Intent(ChatWindow.this,ChatActivity.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();

            return true;
        }
        else if(id == R.id.munit)
        {
            Intent i = new Intent(ChatWindow.this,MedicalDetails.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.logout)
        {
            Intent i = new Intent(ChatWindow.this,MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
