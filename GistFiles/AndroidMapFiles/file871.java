package com.example.sijangurung.jsontest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    private TextView txtContainer;

    HashMap<String, String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = new HashMap<String, String>();

        txtContainer = (TextView) findViewById(R.id.txtContainer);

        new GetUser().execute();
    }

    private class GetUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://rayrays-json-deliverer.herokuapp.com/user";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON User node
                    JSONObject user = jsonObj.getJSONObject("user");
                    String created_at = user.getString("created_at");
                    JSONObject family = user.getJSONObject("family");

                            String f_created_at = family.getString("created_at");
                            String f_created_by = family.getString("created_by");
                            String f_id = family.getString("id");

                            JSONArray f_members = family.getJSONArray("members");

                            for (int i = 0; i < f_members.length(); i++) {
                                JSONObject m = f_members.getJSONObject(i);
                                String id = m.getString("id");
                                String first_name = m.getString("first_name");
                                String last_name = m.getString("last_name");
                                Boolean is_authenticated = m.getBoolean("is_authenticated");
                            }

                            String f_name = family.getString("name");
                            String f_updated_at = family.getString("updated_at");


                    String id = user.getString("id");
                    String first_name = user.getString("first_name");
                    String last_name = user.getString("last_name");
                    Boolean is_authenticated = user.getBoolean("is_authenticated");
                    String updated_at = user.getString("updated_at");

                    userData.put("user", user.toString());


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            txtContainer.setText(userData.toString());

        }
    }
}