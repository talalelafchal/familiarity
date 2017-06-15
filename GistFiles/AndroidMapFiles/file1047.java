package com.example.abhishek.assignmentwiredelta;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.assignmentwiredelta.Adaprter.ListViewAdapter;
import com.example.abhishek.assignmentwiredelta.Model.CompanyDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Ramya on 09-04-2016.
 */
public class MainActivity extends Activity {

    // Declare Variables
    ListView list;
    ListViewAdapter adapter;
    EditText editsearch;
    private Context mContext;
    Spinner spinner;
    ImageView serchimg;
    static JSONArray jObj = null;
    ArrayList<CompanyDetails> arraylist = new ArrayList<CompanyDetails>();
    public static final String url = "https://api.myjson.com/bins/2ggcs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listview);
        // Locate the EditText in listview_main.xml
        editsearch = (EditText) findViewById(R.id.search);
        serchimg=(ImageView)findViewById(R.id.serchimg);
        spinner = (Spinner) findViewById(R.id.spinner);
        servercallnew();
    }

    private void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());


    }

    private void servercallnew() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response
                .Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mSignInProgress.setVisibility(View.GONE);
                captureResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mSignInProgress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Unable to fetch company details", Toast
                        .LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    private void captureResponse(String response)  {
        try {
            JSONArray json = new JSONArray(response);
            arraylist = new ArrayList<>();
            Log.i("Response", String.valueOf(json));
            for (int i = 0; i < json.length(); i++) {
                JSONObject historyJson = json.getJSONObject(i);
                Log.i("Responsenew", String.valueOf(historyJson));
                if (historyJson != null) {
                    String CompanyId=historyJson.getString("companyID");
                       String  CompanyName=historyJson.getString("comapnyName");
                        String  CompanyOwner=  historyJson.getString("companyOwner");
                         String  CompanyStartDate= historyJson.getString("companyStartDate");
                         String CompanyDescription=historyJson.getString("companyDescription");
                          String   CompanyDepartments=historyJson.getString("companyDepartments");
                    arraylist.add(new CompanyDetails(CompanyId,CompanyName,CompanyOwner,CompanyStartDate,CompanyDescription,CompanyDepartments));


                }
            }
            Swodetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void Swodetails() {
        adapter = new ListViewAdapter(this, arraylist);
        list.setAdapter(adapter);
        // Capture Text in EditText
       editsearch.addTextChangedListener(new TextWatcher() {
           @Override
           public void afterTextChanged(Editable arg0) {
               // TODO Auto-generated method stub
               String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
               adapter.filter(text);
           }

           @Override
           public void beforeTextChanged(CharSequence arg0, int arg1,
                                         int arg2, int arg3) {
               // TODO Auto-generated method stub
           }

           @Override
           public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                     int arg3) {
               // TODO Auto-generated method stub
           }
       });

        addListenerOnSpinnerItemSelection();

    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override

        public void onItemSelected(AdapterView parent, View view, int pos, long id) {
            parent.getItemAtPosition(pos);
            //Toast.makeText(parent.getContext(), "Selected Option : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
            String one="";
            if(pos==0){
                one="Accounting";
            } if(pos==1){
                one="Advertising";
            }if(pos==2){
                one="Asset Management";
            }if(pos==3){
                one="Customer Relations";
            }
            if(pos==4){
                one="Customer Service";
            }
            if(pos==5){
                one="Finances";
            }
            if(pos==6){
                one="Human Resources";
            }if(pos==7){
                one="Legal Department";
            }
            if(pos==8){
                one="Media Relations";
            }
            if(pos==9){
                one="Payroll";
            }
            if(pos==10){
                one="Public Relations";
            }
            if(pos==11){
                one="Quality Assurance";
            }
            if(pos==12){
                one="Sales and Marketing";
            }
            if(pos==13){
                one="Research and Development";
            }
            if(pos==14){
                one="Tech Support";
            }
            adapter.filter1(one);
        }

        @Override

        public void onNothingSelected(AdapterView parent) {

        }

    }
}