package com.ets.medecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vish on 8/6/15.
 */
public class SignupActivity2 extends Activity{

    static final String TAG = "signup response";
    Button btnSignupComplete;
    String strResponse;
    String JSON_TAG = "json tag";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Handler handler;
    EditText etMobile, etAddress, etCity;
    Context _context;
    ImageView ivFacebookSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        _context = SignupActivity2.this;
        etMobile = (EditText) findViewById(R.id.et_signup_mobile);
        etAddress = (EditText) findViewById(R.id.et_signup_address);
        etCity = (EditText) findViewById(R.id.et_signup_city);
        btnSignupComplete = (Button) findViewById(R.id.btn_signup2_complete);
        final String name = getIntent().getStringExtra("name");
        final String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");
        final String birthday = getIntent().getStringExtra("birthday");
        handler = new Handler();

        btnSignupComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String validate= validateInputFields(etMobile.getText().toString(),
                        etAddress.getText().toString(),etCity.getText().toString());
                if(validate.equals("ok")) {
                    // do signupProcess
                    if(true) {
                        trySignup(name, email, password, birthday, etMobile.getText().toString(),
                                etAddress.getText().toString(),etCity.getText().toString());
                    } else {
                        showDialog();
                    }

                } else {
                    Toast.makeText(_context,validate,Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private String validateInputFields(String mobile, String address, String city) {
        Log.d(TAG, address);
        if(address.equals("")) {
            return "Address field empty";
        } else if(mobile.equals("")) {
            return "Mobile field empty";
        } else if(city.equals("")) {
            return "City field empty";
        } else {
            return "ok";
        }

    }

    private void trySignup(String name, String email, String password, String birthday, String mobile,
                           String address, String city) {

        final String userName = name;
        final String userEmail = email;
        final String userPassword = password;
        final String userBirthday = birthday;
        final String userMobile = mobile;
        final String userAddress = address;
        final String userCity = city;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("loading...");
        pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse httpResponse = null;
                    HttpPost httpPost = new HttpPost("http://elitetotality.com/medicord/");
                    List<NameValuePair> nameValuePair = new ArrayList<>();
                    nameValuePair.add(new BasicNameValuePair("tag","register"));
                    nameValuePair.add(new BasicNameValuePair("name", userName));
                    nameValuePair.add(new BasicNameValuePair("password", userPassword));
                    nameValuePair.add(new BasicNameValuePair("email",userEmail));
                    nameValuePair.add(new BasicNameValuePair("dob",userBirthday));
                    nameValuePair.add(new BasicNameValuePair("address",userAddress));
                    nameValuePair.add(new BasicNameValuePair("city", userCity));
                    nameValuePair.add(new BasicNameValuePair("mobile",userMobile));
                    try {
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                    } catch(UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        httpResponse = httpClient.execute(httpPost);
                    } catch(IOException e){
                        e.printStackTrace();
                    }


                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                httpResponse.getEntity().getContent(), "UTF-8"));
                        StringBuilder builder = new StringBuilder();

                        for (String line = null; (line = reader.readLine()) != null;) {
                            builder.append(line).append("\n");
                        }

                        String httpResponseString = builder.toString();
                        strResponse = httpResponseString;

                        Log.d(TAG, httpResponseString);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    pDialog.dismiss();

                    JSONObject reader;
                    try {
                        if(strResponse != null){
                            reader = new JSONObject(strResponse);
                            String error = reader.get("error").toString();
                            Log.d(JSON_TAG, error);
                            if(error.equals("false")) {
                                startActivity(new Intent(_context, LoginActivity.class));
                                finish();
                            } else {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(_context,"Try again!"
                                                ,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                    } catch(JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

    private void showDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);
        builder1.setTitle("Connectivity problem!");
        builder1.setMessage("Can't sign up due to internet availability problem");
        builder1.setIcon(getResources().getDrawable(R.drawable.ic_warning));
        builder1.setCancelable(true);
        builder1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert1 = builder1.create();
        alert1.show();
    }

}