package com.sampah_ku.sampahku.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sampah_ku.sampahku.R;
import com.sampah_ku.sampahku.function.SQLiteHandler;
import com.sampah_ku.sampahku.function.SampahkuRestClient;
import com.sampah_ku.sampahku.function.SessionManager;
import com.sampah_ku.sampahku.service.GPSTracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.entity.mime.Header;

public class NewTrashActivity extends AppCompatActivity {

    private static final String TAG = NewTrashActivity.class.getSimpleName();
    private ImageView imagePreview;
    private Button buttonSubmit;
    private EditText editDescription;
    private ProgressDialog pDialog;
    private GPSTracker gps;
    private File image;

    private SQLiteHandler db;
    private SessionManager session;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trash);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            Toast.makeText(NewTrashActivity.this, "Please grant GPS permission", Toast.LENGTH_SHORT).show();
        } // return boolean true/false

        // Progress dialog
        pDialog = new ProgressDialog(NewTrashActivity.this);
        pDialog.setCancelable(false);

        buttonSubmit = (Button) findViewById(R.id.button_submit);
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        editDescription = (EditText) findViewById(R.id.edit_description);

        // Spinner
        typeSpinner = (Spinner) findViewById(R.id.trash_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.trash_type, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        Log.d(TAG, "onCreate: " + getIntent().getStringExtra("pathImage"));
        image = new File(getIntent().getStringExtra("pathImage"));
        Picasso.with(NewTrashActivity.this).load(image).into(imagePreview);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTrash();
            }
        });
    }

    private void submitTrash() {
        HashMap<String, String> user = db.getUserDetails();

        pDialog.setMessage("Mengirim data tempat sampah baru...");
        showDialog();

        RequestParams params = new RequestParams();
        params.put("description", editDescription.getText().toString());
        try {
            params.put("photo", image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(typeSpinner.getSelectedItem().equals("TPS")) {
            params.put("trash_type_id", 1);
        } else {
            params.put("trash_type_id", 2);
        }
        params.put("latitude", gps.getLatitude());
        params.put("longitude", gps.getLongitude());
        params.put("accuracy", gps.getAccuracy());
        params.put("user_id", user.get("id"));

        SampahkuRestClient.post("trash/new", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "Response: " + responseString.substring(3000));
                Log.d(TAG, "Response error: " + throwable.toString());
                hideDialog();
                Toast.makeText(NewTrashActivity.this, "Unexpected error!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                Log.d(TAG, "Response: " + responseString);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(responseString);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(NewTrashActivity.this, "Sukses menambahkan tempat sampah baru", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(NewTrashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
