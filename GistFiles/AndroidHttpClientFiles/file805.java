package com.sexifit.android;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private final String mainActivityTag = "MainActivity";
	private JSONObject jObject;
	private SignInDbAdapter mSignInDbHelper;
	private ProgressDialog mProgressDialog;
	String loginmessage = null;
	Thread t;
	private SharedPreferences mPreferences;
	Button signin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);
		mSignInDbHelper = new SignInDbAdapter(this);
		mSignInDbHelper.open();

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		if (!checkLoginInfo()) {
			signin = (Button) findViewById(R.id.btn_sign_in);
			signin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(0);
					t = new Thread() {
						public void run() {
							try {
								authenticate();
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					t.start();
				}
			});
		} else {
			/*
			 * Directly opens the Welcome page, if the username and password is
			 * already available in the SharedPreferences
			 */
			
			// Trying to minimize the number of screens
			Intent intent = new Intent(this, MainMenuActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0: {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Please wait while signing in ...");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(true);
			return mProgressDialog;
		}
		}
		return null;
	}

	private void authenticate() throws ClientProtocolException, IOException {
		try {
			String pin = "";
			HashMap<String, String> sessionTokens = signIn();
			

		} catch (Exception e) {
			Intent intent = new Intent(getApplicationContext(), LoginError.class);
            intent.putExtra("LoginMessage", "Unable to login");
            startActivity(intent);
            removeDialog(0);
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String loginmsg = (String) msg.obj;
			if (loginmsg.equals("SUCCESS")) {
				removeDialog(0);
				Intent intent = new Intent(getApplicationContext(), WorkoutDetailsListActivity.class);
				//Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
				startActivity(intent);
				finish();
			}
		}
	};

	private HashMap<String, String> signIn() {

		HashMap<String, String> sessionTokens = null;

		EditText mEmailField = (EditText) findViewById(R.id.email_field);
		EditText mPasswordField = (EditText) findViewById(R.id.password_field);

		String email = mEmailField.getText().toString();
		String password = mPasswordField.getText().toString();

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://yoursite.com/sessions");
		JSONObject holder = new JSONObject();
		JSONObject userObj = new JSONObject();

		try {
			userObj.put("password", password);
			userObj.put("email", email);
			holder.put("user", userObj);
			StringEntity se = new StringEntity(holder.toString());
			post.setEntity(se);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException js) {
			js.printStackTrace();	
		}

		String response = null;
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = client.execute(post, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParsedLoginDataSet parsedLoginDataSet = new ParsedLoginDataSet();
		//ParsedLoginDataSet parsedLoginDataSet = myLoginHandler.getParsedLoginData();
		try {

			/*if (response == null) {
				System.out.println("response is null " + response);
				Exception e = new Exception();
				throw e;
			}*/
			sessionTokens = parseToken(response);
			mSignInDbHelper.createSession(mEmailField.getText().toString(),
					sessionTokens.get("auth_token"));
			// now = Long.valueOf(System.currentTimeMillis());
			// mSignInDbHelper.createSession(mEmailField.getText().toString(),mAuthToken,now);
		} catch (Exception e) {
			e.printStackTrace();
		}
		parsedLoginDataSet.setExtractedString(sessionTokens.get("error"));
		if (parsedLoginDataSet.getExtractedString().equals("Success")) {
            // Store the username and password in SharedPreferences after the successful login
            SharedPreferences.Editor editor=mPreferences.edit();
            editor.putString("UserName", email);
            editor.putString("PassWord", password);
            editor.commit();
            Message myMessage=new Message();
            myMessage.obj="SUCCESS";
            handler.sendMessage(myMessage);
      } else {
            Intent intent = new Intent(getApplicationContext(), LoginError.class);
            intent.putExtra("LoginMessage", "Invalid Login");
            startActivity(intent);            
            removeDialog(0);
      }

		return sessionTokens;

	}

	public HashMap<String, String> parseToken(String jsonResponse)
			throws Exception {
		HashMap<String, String> sessionTokens = new HashMap<String, String>();
		if(jsonResponse != null) {
			jObject = new JSONObject(jsonResponse);
			JSONObject sessionObject = jObject.getJSONObject("session");
			String attributeError = sessionObject.getString("error");
			String attributeToken = sessionObject.getString("auth_token");
			String attributeConsumerKey = sessionObject.getString("consumer_key");
			String attributeConsumerSecret = sessionObject
					.getString("consumer_secret");
			sessionTokens.put("error", attributeError);
			sessionTokens.put("auth_token", attributeToken);
			sessionTokens.put("consumer_key", attributeConsumerKey);
			sessionTokens.put("consumer_secret", attributeConsumerSecret);
		} else {
			sessionTokens.put("error", "Error");
		}
		return sessionTokens;
	}

	// Checking whether the username and password has stored already or not
	private final boolean checkLoginInfo() {
		boolean username_set = mPreferences.contains("UserName");
		boolean password_set = mPreferences.contains("PassWord");
		if (username_set || password_set) {
			return true;
		}
		return false;
	}

}