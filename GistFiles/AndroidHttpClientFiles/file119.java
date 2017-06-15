/*
 * Project Name: Using AsyncTask in Android
 * Project Date: 04/05/2014
 * Project Author: Matheus Konzen Iser
 * Project Description: This project will demonstrate how to use and AsyncTask
 * 						in Android by having the user enter a couple of pieces of information and submitting
 * 						that information to our server using HttpPost.
 */

package com.example.asynctask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	EditText user_name;
	EditText user_email;
	Button submit_signup;
	
	String userName;
	String userEmail;
	
	ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeElements();
        
        submit_signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Executes the background task that submits the users information to your server.
				new SubmitSignUp().execute();
				
			}
		});
        
    }
    
    public void initializeElements() {
    	
    	user_name = (EditText)findViewById(R.id.user_name);
    	user_email = (EditText)findViewById(R.id.user_email);
    	submit_signup = (Button)findViewById(R.id.submit_signup);
    	
    }
    
    private class SubmitSignUp extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Creating the Progress Dialog
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setTitle("AsyncTask");
			progressDialog.setMessage("Loading...");
			progressDialog.show();
			
			userName = user_name.getText().toString();
			userEmail = user_email.getText().toString();
			
		}

		@Override
		public String doInBackground(Void... params) {
			
			String responseText = null;
			
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("https://www.YourApp.com/SignUp.php");

			try {
				// Add your data as post values
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("Name", userName));
				nameValuePairs.add(new BasicNameValuePair("Email", userEmail));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute Post Request
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				responseText = EntityUtils.toString(entity);

			} catch (ClientProtocolException e) {

			} catch (IOException e) {

			}

			return responseText;
			
		}

		@Override
		protected void onPostExecute(String result) {
			
			//Dismiss the progress dialog
			progressDialog.dismiss();
			
			//Output result of post request to screen
			Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
			
		}

	}

}