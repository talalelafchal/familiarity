package com.example.SmsService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dd on 13.11.2014.
 */
public class IncomingSms extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    static boolean check=false;
    static String strMsgBody="";
    static String strMsgSrc="";
    String[] time;
    static Integer tt;
    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        strMsgBody="";
        strMsgSrc="";
        String strMessage = "";

        if ( extras != null )
        {
            Object[] smsextras = (Object[]) extras.get( "pdus" );

            for ( int i = 0; i < smsextras.length; i++ )
            {
                SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i]);

                strMsgBody = smsmsg.getMessageBody().toString();
                strMsgSrc = smsmsg.getOriginatingAddress();

                strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;
            }
            try{
           time = strMsgBody.split("m");
            tt=Integer.parseInt(Character.toString(time[1].charAt(0)));}
            catch (Exception e){//Toast.makeText(context,"Не вверно введена смс",Toast.LENGTH_LONG).show();
             }
        }

            Intent go = new Intent(context, com.example.SmsService.VideoView.class);
            go.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(go);
            CountDownTimer time = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    VideoView.fa.finish();
                }
            }.start ();

      /*  new HttpRequestTask(context).execute(context);


    }
    static void ddd(Context context){
        if (check == true) {
            Intent go = new Intent(context, com.example.SmsService.VideoView.class);
            go.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(go);
            CountDownTimer time = new CountDownTimer(60000*tt, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    VideoView.fa.finish();
                }
            }.start ();
        }else {
            Toast.makeText(context,"На стадии разработки возникла ошибка балаб аб ла ла",Toast.LENGTH_LONG).show();
        }
    }*/}

}
