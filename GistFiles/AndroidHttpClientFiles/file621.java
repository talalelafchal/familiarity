package com.netomo.httpapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	TextView txtViewIsConnected;
	EditText txtResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		txtViewIsConnected = (TextView) findViewById(R.id.main_isconnected);
		txtResult = (EditText) findViewById(R.id.main_txtresult);
		
		if(isConnected()) {
			txtViewIsConnected.setBackgroundColor(0xFF00CC00);
			txtViewIsConnected.setText("Estás Connectado");
		} else {
			txtViewIsConnected.setText("No hay conneccion");
		}
		
		new HttpAsyncTask().execute("http://192.168.1.34:8000/api/v1/bizCat/?format=json");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	            return true;
	        else
	            return false;   
	}
	
	class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			InputStream inputstream = null;
			String result = "";
			
			Log.d("Info", "Efectuando petición GET =D");
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpresponse = httpclient.execute(new HttpGet(urls[0]));
				inputstream = httpresponse.getEntity().getContent();
				
				if(inputstream != null) {
					String line = "";
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream));
					
					while((line = bufferedReader.readLine()) != null) {
						result += line;
					}
					inputstream.close();
					
				} else {
					result = "No recibimos nada!";
				}
				
			} catch (Exception e) {
				Log.d("inputStream", e.getLocalizedMessage());
			}
			
			return result;
		}
		
		// onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            //txtResult.setText(result);
            JSONObject json;
			try {
				json = new JSONObject(result);
				txtResult.setText(json.toString(1));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
	}
}