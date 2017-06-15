package com.example.fiddlestick.assignment2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;

public class ListEarthQActivity extends ActionBarActivity implements View.OnClickListener{
    DbOpenHelper dbOpenHelper;
    private static Intent intent;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_earth_q);

        dbOpenHelper = new DbOpenHelper(this,"new.db",null,1);
        Bundle b = getIntent().getExtras();
        OrderByObject obo = (OrderByObject) b.getSerializable("obo");
        //Log.d("TESTT",obo.toString());
      // Cursor cursor = dbOpenHelper.getAllEarthquake();
        Cursor cursor = dbOpenHelper.getFiltered(obo);
        //Log.d("curr",String.valueOf(cursor.getCount()));
        ListView listView = (ListView) findViewById(R.id.QuakeList);

        if(cursor!=null){
            DoCursorAdapter doCursorAdapter = new DoCursorAdapter(this,cursor,0);
            listView.setAdapter(doCursorAdapter);
        }else{
            Toast.makeText(this, "No result", Toast.LENGTH_LONG);
        }

       // new CreateEarthObj(this,cursor).execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        btn = (Button) findViewById(R.id.toMap);
        btn.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Cycle" , "Resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_earth_q, menu);
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

    @Override
    public void onClick(View v) {
        if(v == btn){
            Intent intent = new Intent(this,MapActivity.class);
            startActivity(intent);
        }
    }

    private class CreateEarthObj extends AsyncTask<Void, Void,String>{
        Context con;
        Cursor cursor;
        ArrayList<EarthQuake> quakeArrayList = new ArrayList<EarthQuake>();
        public CreateEarthObj(Context c,Cursor cur){
            con = c;
            cursor = cur;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.d("DDDDDDD", " ListEarth ASYNC");
            cursor.moveToFirst();
            intent = new Intent(con,MapActivity.class);

            while(!cursor.isLast()){
                String time = cursor.getString(1);
                double mag = cursor.getDouble(2);
                double depth = cursor.getDouble(3);
                double lat =  cursor.getInt(4);
                double log = cursor.getInt(5);
                String loc = cursor.getString(6);
                cursor.moveToNext();
                quakeArrayList.add(new EarthQuake(time,mag,depth,lat,log,loc));

            }
          /*  for(EarthQuake e : quakeArrayList){
                Log.d("TTTT",e.toString());
            }
            Log.d("ASYN" , cursor.getString(0));*/
            intent.putExtra("quakeArrayList", quakeArrayList);
            return String.valueOf(quakeArrayList.size());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(con, s,Toast.LENGTH_LONG).show();
        }
    }
}
