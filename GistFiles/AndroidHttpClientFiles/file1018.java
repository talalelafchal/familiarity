package com.example.TMTtest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class TestActivity extends Activity implements OnClickListener{
    Button btn;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener((OnClickListener) this);
    }

    public void onClick(View view){
        new TMTDownloadOperation().execute("");
    }

    private class TMTDownloadOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

              DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://www.tmtworld.it/congress/");
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(get);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity httpEntity = httpResponse.getEntity();
            String responseStr = null;
            try {
                responseStr = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(responseStr);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView txt = (TextView) findViewById(R.id.output);
            txt.setText("Download is finished");
        }

        @Override
        protected void onPreExecute() {
            TextView txt = (TextView) findViewById(R.id.output);
            txt.setText("Downloading....");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}