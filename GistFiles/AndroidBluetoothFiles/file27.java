package com.healthiot;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class ChatActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String user="";
    TextView tv;
    Spinner sp;
    int height=0,width=0;
    ListView lv;
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> msgs = new ArrayList<String>();
    ArrayList<String> status = new ArrayList<String>();
    ArrayList<String> temp = new ArrayList<String>();
    ArrayList<String> temp1 = new ArrayList<String>();
    ChatAdapter adapter;
    String sendmsg,snddate;
    public static final String URL = "http://patientmonitoringsys.comli.com/patientmonitor/admin/view_patient_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Getting Intent

        Intent i = getIntent();
        Bundle b = i.getExtras();
        user = b.getString("user");


        //Linking Components

        lv = (ListView) findViewById(R.id.list);

        //Getting screen size

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;


        //Setting size to components

        lv.getLayoutParams().height = (int) (0.75*height);


        MyTask m = new MyTask();
        m.execute();



        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    class MyTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog Asycdialog = new ProgressDialog(ChatActivity.this);

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

        final String url = URL+"?req=chat_history&user="+user;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                StringTokenizer st = new StringTokenizer(response,"&&&");
                while(st.hasMoreTokens())
                {
                    StringTokenizer stt = new StringTokenizer(st.nextToken(),"***");
                    while(stt.hasMoreTokens())
                    {
                        msgs.add(stt.nextToken());
                        dates.add(stt.nextToken());
                        status.add(stt.nextToken());
                    }
                }
                /*dates.add("22/01/2016 14:38 PM");
                msgs.add("Ya..Haai");
                status.add("receive");

                dates.add("22/01/2016 14:37 PM");
                msgs.add("Haaaai");
                status.add("sent");*/

                int count = (dates.size());

                adapter = new ChatAdapter(getApplicationContext(),dates,msgs,status,count,height,width);

                adapter.notifyDataSetChanged();

                lv.setAdapter(adapter);




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
            Intent i = new Intent(ChatActivity.this, ViewUserFrofile.class);
            i.putExtra("user",user);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        else if(id == R.id.book)
        {
            Intent i = new Intent(ChatActivity.this,BookAppointment.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();

            return true;
        }

        else if (id == R.id.userprofile) {

            Intent i = new Intent(ChatActivity.this,ViewUserFrofile.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.sensors)
        {
            Intent i = new Intent(ChatActivity.this,ViewSensors.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }

        else if(id == R.id.munit)
        {
            Intent i = new Intent(ChatActivity.this,MedicalDetails.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.chatsend)
        {
            Intent i = new Intent(ChatActivity.this,ChatWindow.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.chat)
        {

            return true;
        }
        else if(id == R.id.logout)
        {
            Intent i = new Intent(ChatActivity.this,MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String s = parent.getItemAtPosition(position).toString();

        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
