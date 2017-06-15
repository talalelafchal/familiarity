package com.example.tedngok.iphone6;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity {
    TextView txtview;

    private class GetJSON extends AsyncTask<Void, Void, JSONObject> {
        String address = "https://reserve.cdn-apple.com/HK/zh_HK/reserve/iPhone/availability.json";
        StringBuilder builder = new StringBuilder();
        JSONObject jArray;

        @Override
        protected JSONObject doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(address);
            try {
                HttpResponse res = client.execute(request);
                InputStream content = res.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                jArray = new JSONObject(builder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jArray;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            //txtview.setText(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //txtview = (TextView) findViewById(R.id.textView);
        new GetJSON().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
