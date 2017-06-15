package com.example.myapplication;

import android.app.Activity;
import android.bluetooth.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity
{
    public boolean good;
    private Thread mThread;
    private String str;
    public TextView text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.textView);
    }

    public void connect(View view) throws InterruptedException
    {
        String hello = getBaseContext().getResources().getString(R.string.hello_world);
        String cn = getBaseContext().getResources().getString(R.string.connect);
        String dc = getBaseContext().getResources().getString(R.string.disconnect);
        String ch = getBaseContext().getResources().getString(R.string.chatter);
        Button con_button = (Button) view;

        final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (dc.equals(con_button.getText()))
        {
            good = false;
            con_button.setText(cn);
        }else
        {
            good = true;
            con_button.setText(dc);

            mThread =  new Thread()
            {
                @Override
                public void run()
                {
                    int i = 1; //Integer.parseInt((String)text.getText());

                    //incoming bluetooth

                    {
                        while (!bluetooth.isEnabled())
                        {
                            try
                            {
                                String state = Integer.toString(bluetooth.getState());
                                str = state;
                                handler.sendEmptyMessage(0);
                                if (state.equals("10")) bluetooth.enable();
                                Thread.sleep(500);
                            }catch(Exception e)
                            {
                                str = "Panic! " + e;
                            }
                        }
                        if (bluetooth.isEnabled())
                        {
                            String mydeviceaddress = bluetooth.getAddress();
                            String mydevicename = bluetooth.getName();
                            str = mydevicename + " : " + mydeviceaddress;
                            handler.sendEmptyMessage(0);
                        }
                        else
                        {
                            str = "Bluetooth is not Enabled.";
                            handler.sendEmptyMessage(0);
                        }
                    }

                    while (good)
                    {
                        try
                        {
                            i++;
                            //str = Integer.toString(i);
                            //handler.sendEmptyMessage(0);
                            Thread.sleep(250);
                        }catch(Exception e)
                        {
                            str = "Panic! " + e;
                        }
                    }
                    if (!good) stopThread(this);
                }
            };
            mThread.start();
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            text.setText(text.getText() + "\n" + str);
        }
    };

    private synchronized void stopThread(Thread theThread)
    {
        if (theThread != null)
        {
            theThread = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
