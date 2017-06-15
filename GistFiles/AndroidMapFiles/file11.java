package pizzaplease.nyu.pizzaorderingpls;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    TextView textview;
    RequestQueue queue;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView)findViewById(R.id.pizza);

        queue = Volley.newRequestQueue(this);
        url = "https://api.syncano.io/v1/instances/pizza-pine-5350/webhooks/pizza-fetch/run/";
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

    public void onclick(View view) {
        Intent intent = new Intent(this, Instruction_Activity.class);
        startActivity(intent);
//        int count = Integer.parseInt(textview.getText().toString());
//        count++;
//        textview.setText(String.valueOf(count));
////        textview.setText("new text");
    }

    public void orderpizza(View view) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        textview.setText("Response is: "+ response);
                        try {
                            JSONObject obj2 = new JSONObject(response);
                            Log.d("summarizeReq", obj2.getJSONObject("result").get("stdout").toString());
                            textview.setText(obj2.getJSONObject("result").get("stdout").toString());
                        } catch (Throwable t) {
                            Log.e("Summarizer", "Could not parse malformed JSON: \"" + response + "\"");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textview.setText("That didn't work!");
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("X-API-KEY","7376106916d5ae8ab578040ae303e860f76c0b50");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
