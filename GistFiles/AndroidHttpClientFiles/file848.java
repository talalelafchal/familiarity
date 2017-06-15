package com.example.seymen.havadurumu;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Havadurumu extends AppCompatActivity {
    TextView txt_derece;
    Button btn_goster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_havadurumu);
        txt_derece = (TextView) findViewById(R.id.txtgoster);
        btn_goster = (Button) findViewById(R.id.btn_hava);

        btn_goster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getWeatherCondition().execute();
            }
        });

    }
    private class getWeatherCondition extends AsyncTask<Void,Void,Void>{
        String dolar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... Params)  {
            String url = "http://www.doviz.gen.tr/doviz_json.asp?version=1.0.4";
            JSONObject jsonObject = null;
            try {
                String json =  Parse.getJSONFromUrl(url);
                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    dolar = jsonObject.getString("guncelleme");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void args) {
            txt_derece.setText(dolar);
        }
    }

}


