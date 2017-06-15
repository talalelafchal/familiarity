package com.panchicore.morosos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.panchicore.morosos.adapter.DebtorsListAdapter;
import com.panchicore.morosos.model.Debtor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mDebtorsListView;
    private ArrayList<Debtor> mDebtorsList;
    private DebtorsListAdapter mDebtorsListAdapter;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // prepare objects
        mDebtorsList = new ArrayList<>();
        mDebtorsListAdapter = new DebtorsListAdapter(getApplicationContext(), R.id.debtorsListView, mDebtorsList);

        // prepare UI
        mDebtorsListView = (ListView) findViewById(R.id.debtorsListView);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        mDebtorsListView.setAdapter(mDebtorsListAdapter);

        // do API calls
        requestAllDebtors();

        // prepare listeners
        mDebtorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Debtor d = mDebtorsList.get(position);
                Intent i = new Intent(getApplicationContext(), DebtorActivity.class);
                i.putExtra("NAME", d.getName());
                startActivity(i);
            }
        });

        mDebtorsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Debtor d = mDebtorsList.get(position);
                mDebtorsList.remove(position);
                mDebtorsListAdapter.remove(d);

                deleteDebtor(d);
                return true;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_item_add){
            Intent i = new Intent(getApplicationContext(), AddActivity.class);
            i.putExtra("NAME", "LUIS PALLARES");
            startActivity(i);
        }

        if(item.getItemId() == R.id.action_item_refresh){
            requestAllDebtors();
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestAllDebtors(){

        mDebtorsList.clear();
        mDebtorsListAdapter.clear();
        loadingProgressBar.setVisibility(View.VISIBLE);

        final String URL = "https://sheetsu.com/apis/v1.0/00151fd1";
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("RESULT", response.toString());
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject o = (JSONObject) response.get(i);
                        if(!o.getString("id").isEmpty()){
                            Debtor d = new Debtor(o);
                            mDebtorsList.add(d);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mDebtorsListAdapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.getMessage());
            }
        });
        Application.getApplication().addToRequestQueue(request);
    }


    public void deleteDebtor(Debtor d){
        Toast.makeText(getApplicationContext(), "Borrando...", Toast.LENGTH_SHORT).show();
        final String URL = "https://sheetsu.com/apis/v1.0/00151fd1/id/" + d.getId();
        StringRequest request = new StringRequest(Request.Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Borrado OK", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Application.getApplication().addToRequestQueue(request);
    }

}
