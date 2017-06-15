package com.example.matthew.testvolleypost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Example for Stackoverflow question http://stackoverflow.com/questions/33684244/php-post-not-received-from-android-using-volley-library/33686054#33686054
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCall();
            }
        });
    }

    public void apiCall() {
        Log.d("EXAMPLE", "apiCall()");
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<>();
        params.put("tag", "register");
        params.put("name", "myname");
        params.put("email", "myname@email.com");
        params.put("password", "meow");
        JSONObject o = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "http://10.0.2.2:8000/index.php", // see http://developer.android.com/tools/devices/emulator.html#networkaddresses
                o,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("EXAMPLE", "Register Response: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(jsonObjectRequest);
        queue.start();
    }

}
