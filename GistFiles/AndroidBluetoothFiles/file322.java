package com.example.raghuram.networkapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager con = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        TextView textView = (TextView)findViewById(R.id.logger);
        /*if(networkInfo!=null && networkInfo.isConnected())
            textView.setText("Wifi connected");
        else
            textView.setText("Wifi not connected");
        networkInfo=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(networkInfo!=null && networkInfo.isConnected())
            textView.append("\nMobile connected");
        else
            textView.append("\nMobile not connected");
        networkInfo=con.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        if(networkInfo!=null && networkInfo.isConnected())
            textView.append("\nBluetooth connected");
        else
            textView.append("\nBluetooth not connected");
        networkInfo=con.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if(networkInfo!=null && networkInfo.isConnected())
            textView.append("\nEthernet connected");
        else
            textView.append("\nEthernet not connected");
*/
        if (networkInfo != null && networkInfo.isConnected()) {
            textView.setText("Connected");
            new DownloadWebpageTask().execute("http://192.168.1.133/froogal-user-app-api");
        } else
            textView.setText("Not Connected");
    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                Log.d("Came:","Came here");
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("Came to:","Post Execute");
            TextView textView = (TextView)findViewById(R.id.logger);
            textView.setText(result);
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 5500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("DEBUG TAG:", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
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
