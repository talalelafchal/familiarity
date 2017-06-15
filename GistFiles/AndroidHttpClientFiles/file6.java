package com.example.nawfal.caridata;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends ListActivity {
    EditText editSearch;
    private static final String TAG_TOILETS = "toilets";
    private static final String TAG_NAME = "name";
    private static final String TAG_TOILET_ID = "toilet_id";
    //private static final String TAG_TYPE = "type";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_PRICE = "price";
//url untuk melakukan get, parameter name saya kosongkan untuk nantinya diisi dengan keywords tertentu
    private static String url= "http://192.168.1.5:8080/caridata/cari.php?name=";
    //urlget digunakan untuk url full yang dipanggil , url+keywords
    private static String urlget= null;
    static boolean a=false;
    JSONArray toilets = null;
    //deklarasi progressdialog
    ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> toiletList = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
    public void searchtoi(View view)
    {

        editSearch = (EditText) findViewById(R.id.edit1);
        //Mengambil keywords, dijadikan string
        String src = editSearch.getText().toString();
        src = src.replaceAll(" ", "%20");
        urlget= url+src;
        //Log.e("a",urlget);
        new JSONParse().execute();
        toiletList.clear();

    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        @Override
        //Menampilkan progress dialog
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(Search.this);
            pDialog.setMessage("Tunggu ya ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args)
        {
            //Membuat JSON Parser instance
            JSONParser jParser = new JSONParser();

            //mengambil JSON String dari urlget, url+keywords
            JSONObject json = jParser.getJSONFromUrl(urlget);
            if(json==null)
            {
                a=false;
            }
            else a=true;
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if(a==true)
            {
                try{
                    Log.e("status",a+"");
                    //mengambil array toilets
                    toilets = json.getJSONArray(TAG_TOILETS);
                    //loop pada toilets
                    for(int i=0; i<toilets.length();i++)
                    {
                        JSONObject a = toilets.getJSONObject(i);
                        //simpan di variable
                        String toilet_id = a.getString(TAG_TOILET_ID);
                        String name = a.getString(TAG_NAME);
                        //String type = a.getString(TAG_TYPE);
                        String latitude = a.getString(TAG_LATITUDE);
                        String longitude = a.getString(TAG_LONGITUDE);
                        String price = a.getString(TAG_PRICE);
                        Log.e("name",name);
                        //buat hashmap baru untuk store String
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_TOILET_ID, toilet_id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_LATITUDE, latitude);
                        map.put(TAG_LONGITUDE, longitude);
                        map.put(TAG_PRICE, price);
                        toiletList.add(map);
                        //ProgressDialog dihilangkan jika sudah selesai mengambil data
                        pDialog.dismiss();
                        tampilkandata();
                    }
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(), "error getting data", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
                Log.e("status",a+"");}
        }
    }
    public void tampilkandata()
    {
        //membuat ListView dari data JSON yang ada
        ListAdapter adapter = new SimpleAdapter(this, toiletList,
                R.layout.list_view, new String[]{TAG_TOILET_ID,TAG_NAME,TAG_LATITUDE,TAG_LONGITUDE,TAG_PRICE}, new int[]{
                R.id.toilet_id,R.id.name,R.id.latitude, R.id.longitude,R.id.price});
        setListAdapter(adapter);

        // selecting single ListView item
        ListView lv=getListView();

        // Memberikan Event Click Listener pada List View
        lv.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Mengambil nilai dari ListView yang di Click
                String toilet_id = ((TextView) view.findViewById(R.id.toilet_id)).getText().toString();
                String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
                String latitude = ((TextView) view.findViewById(R.id.latitude)).getText().toString();
                String longitude = ((TextView) view.findViewById(R.id.longitude)).getText().toString();
                String price = ((TextView) view.findViewById(R.id.price)).getText().toString();
                //Membuat intent untuk menampilkan activity Detail
                //Selain itu Intent ini juga digunakan untuk mengirimkan suatu data
                Intent i = new Intent(getApplicationContext(), Detail.class);
                //Memasukkan data yang akan dikirimkan melalui intent
                i.putExtra(TAG_TOILET_ID, toilet_id);
                i.putExtra(TAG_NAME, name);
                i.putExtra(TAG_LATITUDE, latitude);
                i.putExtra(TAG_LONGITUDE, longitude);
                i.putExtra(TAG_PRICE, price);
                startActivity(i);
                //Menampilkan data dari ListView yang di Click dalam bentuk popup
                //Toast.makeText(getApplicationContext(), "toilet "+name, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
