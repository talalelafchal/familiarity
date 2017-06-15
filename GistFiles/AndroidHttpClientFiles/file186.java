/*
* IoT Dimmer Android client
*
* This program implements:
*   - An Android app to send control requests to the control server. The app shows a slider 
*     with values from 0 to 7.
* 
* Created July 9th, 2015
* by Ulises Ruiz, Juan Romero and Xavier Guzman.
* 
* Copyright (c) 2015 Grupo Flextronics, S.A. de C.V.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
* 
*/
package com.flextronics.iot.Dimmer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class DimmerActivity extends Activity {

    TextView txtResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SeekBar bar = (SeekBar)findViewById(R.id.seekBar);
        final TextView txt = (TextView)findViewById(R.id.txtValue);
        txt.setText(String.valueOf(bar.getProgress()));
        txtResponse = (TextView)findViewById(R.id.txtResponse);


        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String progress = String.valueOf( seekBar.getProgress() );
                txt.setText(progress);
                txtResponse.setText("Please wait...");
                new SendRequestTask().execute(getString(R.string.host), progress);
            }
        });

    }

    private class SendRequestTask extends AsyncTask&lt;String, Void, String&gt;{

        private static final String token = "letmein";
        private static final String target = "device1";

        @Override
        protected String doInBackground(String... params) {

            String queryString = String.format("?intensity=%s&token=%s&device=%s", params[1], token, target);
            String requestUrl = params[0] + queryString;

            // params comes from the execute() call: params[0] is the url.
            String res = "";
            String responseCode ="" ;
            try {
                Log.d("Dimmer", "Sending request " + requestUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(requestUrl).openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ( (line = reader.readLine()) != null){
                    builder.append(line);
                }
                res = builder.toString();
                urlConnection.disconnect();
                responseCode = urlConnection.getResponseMessage();
            } catch (IOException e) {
                res = e.getMessage();
            } finally{
                return responseCode;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Dimmer", "Response was " + result);
            txtResponse.setText(result);
        }
    }
}