package com.example.sakshi.offlinesecuritypage1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    EditText EmailId, PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmailId= (EditText)findViewById(R.id.editText6);
        PhoneNumber = (EditText)findViewById(R.id.editText2);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        boolean isInternetPresent = false;
        String emailid = " ", phonenumber = " ";
        boolean flag = false;
        if (!isInternetPresent) {
            Toast.makeText(getApplicationContext(),
                    "Internet not present", Toast.LENGTH_SHORT).show();
        } else if (isInternetPresent) {

            flag = true;

            // find the radiobutton by returned id
            emailid =EmailId.getText().toString();
            phonenumber = PhoneNumber.getText().toString();

            if (emailid.equals("") || emailid == "") {
                flag = false;
                Toast.makeText(getApplicationContext(),
                        "Email is required.", Toast.LENGTH_SHORT).show();
                EmailId.setFocusable(true);

            } else if (emailid.contains("@") != true) {
                flag = false;
                Toast.makeText(getApplicationContext(), "Invalid email.",
                        Toast.LENGTH_SHORT).show();
                EmailId.setFocusable(true);

            } else if (phonenumber.equals("") || phonenumber.length() < 10) {
                flag = false;
                Toast.makeText(getApplicationContext(),
                        "Enter the correct mobile number.", Toast.LENGTH_SHORT).show();
                PhoneNumber.setFocusable(true);
            }
        }

        if (flag) {
            String url;
            //  Toast.makeText(getApplicationContext(), "working", Toast.LENGTH_SHORT).show();
            url = "http://sakshi.byethost14.com" + "register.php?tag=register&email=" + emailid + "&phone=" + phonenumber;
            url = url.replaceAll(" ", "%20");
            Log.d("url", url);
            new ReadJSONFeedTask().execute(url);

        }


    }



    public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
        } catch (Exception e) {
            return stringBuilder.toString();
        }



        public class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
            private boolean clicked = false;
            ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
                // Set progressdialog title
                mProgressDialog.setMessage("Registring User");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                // Show progressdialog
                mProgressDialog.show();

            }

            @Override
            protected String doInBackground(String... urls) {

                return readJSONFeed(urls[0]);
            }

            protected void onPostExecute(String result) {

                try {
                    if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    mProgressDialog = null;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String checkerror = jsonObject.optString("error");
                    String errormsg = jsonObject.optString("error_msg");
                    Log.d("this is error", checkerror);
                    Log.d("this is error", errormsg);


                    if (checkerror.matches("false")) {
                        JSONObject jsonObject1 = jsonObject.optJSONObject("user");
                        String checkemail = jsonObject1.optString("emailid");
                        String checkphone = jsonObject1.optString("phonenumber");
                        Toast.makeText(getApplicationContext(), "Moving to otp ", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), activity2.class);
                        i.putExtra("useremail", checkemail);
                        i.putExtra("userphone", checkphone);
                        i.putExtra("button", clicked);
                        startActivity(i);


                    } else {
                        Log.d(" INCORRECT", "INCORRECT");
                        Toast.makeText(getApplicationContext(), "Email or phone already existed", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {

                }
            }


        }