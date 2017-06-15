package com.warodom.httpexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.*;
import org.json.*;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private TextView tvJson;
    private Button btnJsonObj;
    private Button btnJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvJson = (TextView)findViewById(R.id.tv);
        btnJsonObj = (Button) findViewById(R.id.btnJsonObj);
        btnJsonArray = (Button) findViewById(R.id.btnJsonArray);

        btnJsonObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tvJson.setText("");
                    getMessage("jsonObject");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnJsonArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tvJson.setText("");
                    getMessage("jsonArray");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMessage(String path) throws JSONException {
        ClientFacade.get(path, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("----", "JSON Object");
                tvJson.setText("JSON Object \n\n");
                try {
                    Iterator<String> iter = response.keys();
                    while( iter.hasNext()) {
                        String key = iter.next();
                        Object value = response.get(key);
                        Log.d("----", value.toString() );
                        tvJson.append(value.toString() + "\n");
                        JSONObject jo = new JSONObject(value.toString());
                        Iterator<String> iter1 = jo.keys();
                        while( iter1.hasNext()) {
                            String key1 = iter1.next();
                            Log.d("----",  jo.get(key1).toString() );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray message) {
                Log.d("----", "JSON Array");
                tvJson.setText("JSON Array \n\n");
                try {
                    for (int i=0;i< message.length();i++) {
                        JSONObject jsObj = (JSONObject) message.get(i);
                        Log.d("----", jsObj.getString("message"));
                        tvJson.append(jsObj.getString("message") + "\n");
                    }
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.d("----", "" + statusCode);
                Log.d("----", "" + throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("----", ""+ statusCode);
                Log.d("----", "" + throwable);
            }
        });
    }
}
