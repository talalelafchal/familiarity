package com.jumpbyte.webserver;

import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.Map;
import java.util.Properties;


public class NanoServerActivity extends ActionBarActivity {

    private static final String TAG = "LOCAL_SERVER";
    private WebServer server;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        server = new WebServer();
        try {
            server.start();
        } catch(IOException ioe) {
            Log.w(TAG, "The server could not start.");
        }
        Log.w(TAG, "Web server initialized.");

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = android.text.format.Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.i(TAG, "Server running at " + ip + ":8080");
    }


    // DON'T FORGET to stop the server
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

    private class WebServer extends NanoHTTPD {

        public WebServer(){
            super(8080);
        }

        @Override
        public Response serve(String uri, Method method,
                              Map<String, String> header,
                              Map<String, String> parameters,
                              Map<String, String> files) {
            String answer = "";
            InputStream imageFile = null;
            Log.i(TAG, "local server requesting uri " + uri + " with parameters " + parameters.toString() );
            try {

                // Open file from SD Card
                //File root = Environment.getExternalStorageDirectory();
                //FileReader index = new FileReader(new FileReader(root.getAbsolutePath() + "index.html" );

                BufferedReader reader = new BufferedReader(new InputStreamReader( getAssets().open("index.html")  ));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    answer += line;
                }
                reader.close();

                imageFile = getAssets().open("image.jpg");
            } catch(IOException ioe) {
                Log.w(TAG, ioe.toString());
            }


            //return new NanoHTTPD.Response(answer);
            return new Response(Response.Status.OK, "image/jpg", imageFile);
        }
    }
}