package com.healthiot;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.StringTokenizer;

public class ViewSensors extends AppCompatActivity {

    TextView hb,hbLabel,temp,tempLabel,ecg,ecgLabel,headingLabel;
    int height=0,width=0;
    String user="",ecgvalue="",tempvalue="",hbvalue="";
    boolean b = false;
    public static final String URL = "http://patientmonitoringsys.comli.com/patientmonitor/admin/view_patient_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sensors);

        //Getting Intent

        Intent i = getIntent();
        Bundle b = i.getExtras();
        user = b.getString("user");

        //Linking Components

        hb = (TextView) findViewById(R.id.hb);
        hbLabel = (TextView) findViewById(R.id.hbLabel);
        temp = (TextView) findViewById(R.id.temp);
        tempLabel = (TextView) findViewById(R.id.tempLabel);
        ecg = (TextView) findViewById(R.id.ecg);
        ecgLabel = (TextView) findViewById(R.id.ecgLabel);
        headingLabel = (TextView) findViewById(R.id.headingLabel);
++
        //Getting screen size

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Setting Size

        headingLabel.setWidth((int) (0.80 * width));
        headingLabel.setHeight((int) (0.10 * height));
        hb.setWidth((int) (0.30 * width));
        ecg.setWidth((int)(0.30*width));
        temp.setWidth((int)(0.30*width));
        hbLabel.setWidth((int)(0.60*width));
        ecgLabel.setWidth((int)(0.60*width));
        tempLabel.setWidth((int)(0.60*width));

        MyTask m = new MyTask();
        m.execute();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //JSON Class for Update

    public void samp(){


        final String TAG = " View Details";

        String url = URL+"?req=sensor&user="+user;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                StringTokenizer st = new StringTokenizer(response,"***");

                ecgvalue = st.nextToken().toString();
                tempvalue = st.nextToken().toString();
                hbvalue = st.nextToken().toString();

                int hbb = Integer.parseInt(hbvalue);
                int ecgg = Integer.parseInt(ecgvalue);
                int tempp = Integer.parseInt(tempvalue);

                ecg.setText(ecgvalue);
                temp.setText(tempvalue);
                hb.setText(hbvalue);

                if((hbb>=85)||(hbb<=57))
                {
                    //Call Notification
                    long when = System.currentTimeMillis();

                    NotificationManager notificationManager = (NotificationManager) MainActivity.context
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(MainActivity.context, MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.context, 0,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Uri ringtoneUri = Uri.parse("/sdcard/Health/ringtones/alarm.mp3");
                    Uri alarmSound = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.context, RingtoneManager.TYPE_NOTIFICATION);

                    NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                            MainActivity.context).setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Sensor Alert")
                            .setContentText("You have abnormal heartbeat!!!").setSound(alarmSound)
                            .setAutoCancel(true).setWhen(when)
                            .setContentIntent(pendingIntent)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    notificationManager.notify(999, mNotifyBuilder.build());
                }
                else if((tempp>=45))
                {
                    //Call Notification
                    long when = System.currentTimeMillis();

                    NotificationManager notificationManager = (NotificationManager) MainActivity.context
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(MainActivity.context, MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.context, 0,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Uri ringtoneUri = Uri.parse("/sdcard/Health/ringtones/alarm.mp3");
                    Uri alarmSound = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.context, RingtoneManager.TYPE_RINGTONE);

                    NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                            MainActivity.context).setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Sensor Alert")
                            .setContentText("You have abnormal temperature!!!").setSound(alarmSound)
                            .setAutoCancel(true).setWhen(when)
                            .setContentIntent(pendingIntent)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    notificationManager.notify(888, mNotifyBuilder.build());

                }
                else if((ecgg>=45))
                {
                    //Call Notification
                    long when = System.currentTimeMillis();

                    NotificationManager notificationManager = (NotificationManager) MainActivity.context
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(MainActivity.context, MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.context, 0,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Uri ringtoneUri = Uri.parse("/sdcard/Health/ringtones/alarm.mp3");
                    Uri alarmSound = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.context, RingtoneManager.TYPE_RINGTONE);

                    NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                            MainActivity.context).setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Sensor Alert")
                            .setContentText("You have abnormal temperature!!!").setSound(alarmSound)
                            .setAutoCancel(true).setWhen(when)
                            .setContentIntent(pendingIntent)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    notificationManager.notify(777, mNotifyBuilder.build());

                }

                b = true;

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
            }
        });
        int MY_SOCKET_TIMEOUT_MS = 30000;
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(strReq);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog Asycdialog = new ProgressDialog(ViewSensors.this);

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
            if(b) {
                Asycdialog.dismiss();
                setvaluesFunction();
            }

        }
    }

    public void setvaluesFunction()
    {
        ecg.setText(ecgvalue);
        temp.setText(tempvalue);
        hb.setText(hbvalue);
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
            Intent i = new Intent(ViewSensors.this, ViewUserFrofile.class);
            i.putExtra("user",user);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        else if(id == R.id.book)
        {
            Intent i = new Intent(ViewSensors.this,BookAppointment.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();

            return true;
        }

        else if (id == R.id.userprofile) {

            Intent i = new Intent(ViewSensors.this,ViewUserFrofile.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.sensors)
        {

            return true;
        }

        else if(id == R.id.munit)
        {
            Intent i = new Intent(ViewSensors.this,MedicalDetails.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.chat)
        {
            Intent i = new Intent(ViewSensors.this,ChatActivity.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();

            return true;
        }
        else if(id == R.id.chatsend)
        {
            Intent i = new Intent(ViewSensors.this,ChatWindow.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
            return true;
        }
        else if(id == R.id.logout)
        {
            Intent i = new Intent(ViewSensors.this,MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
