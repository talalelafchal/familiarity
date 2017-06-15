package com.sixminute.freeracing;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String url = "https://www.googleapis.com/plus/v1/people/" + Constants.GOOGLE_PLUS_USERID + "?key=";

        Button but = (Button)findViewById(R.id.httpbuttuh_and);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new RequestTask().execute(url + Constants.ANDROID_API_KEY);
            }
        });

        but = (Button)findViewById(R.id.httpbuttuh_ios);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new RequestTask().execute(url + Constants.IOS_API_KEY);
            }
        });

        but = (Button)findViewById(R.id.httpbuttuh_server);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new RequestTask().execute(url + Constants.SERVER_API_KEY);
            }
        });
    }
}

class RequestTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... uri) {
        Log.d("HTTP GET", uri[0]);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            responseString = out.toString();
            out.close();
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("HTTP RESPONSE", result);
    }
}