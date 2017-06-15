package com.example.user.j_son3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by user on 2015-05-15.
 */
public class count_salary extends ActionBarActivity{
    private final String urlPath = "http://capstonedesign.dothome.co.kr";
    private final String TAG = "PHPTEST";
    private static final String TAG_OS = "tt";
    private static final String TAG_NAME = "name";
    private static final String TAG_SALARY = "salary";
    JSONArray android = null;

    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView list;
    TextView salary;
    TextView name;
    Button Btngetdata;
    TextView textView, textView2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countsalary);

        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        Btngetdata = (Button)findViewById(R.id.get_data);
        Btngetdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                new HttpTask().execute();
                new JSONParse().execute();
            }
        });
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            name = (TextView)findViewById(R.id.mName);
            salary = (TextView)findViewById(R.id.mSalary);

            pDialog = new ProgressDialog(count_salary.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(urlPath);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_OS);
                int count = 0;
                for(int i = 0; i < android.length(); i++){
                    JSONObject c = android.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String name = c.getString(TAG_NAME);
                    String salary = c.getString(TAG_SALARY);

                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_NAME, name);
                    map.put(TAG_SALARY, salary);
                    oslist.add(map);
                    list=(ListView)findViewById(R.id.list);
                    ListAdapter adapter = new SimpleAdapter(count_salary.this, oslist, R.layout.list_v, new String[] {TAG_NAME, TAG_SALARY}, new int[] {
                            R.id.mName,R.id.mState_work});
                    list.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class HttpTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // TODO Auto-generated method stub
            try{
                HttpPost request = new HttpPost(urlPath);
                //전달할 인자들
                Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
                    nameValue.add(new BasicNameValuePair("command", "0"));

                //웹 접속 - utf-8 방식으로
                HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
                request.setEntity(enty);

                HttpClient client = new DefaultHttpClient();
                HttpResponse res = client.execute(request);
                //웹 서버에서 값받기
                HttpEntity entityResponse = res.getEntity();
                InputStream im = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));

                String total = "";
                String tmp = "";
                //버퍼에있는거 전부 더해주기
                //readLine -> 파일내용을 줄 단위로 읽기
                while((tmp = reader.readLine())!= null)
                {
                    if(tmp != null)
                    {
                        total += tmp;
                    }
                }
                im.close();
                //결과창뿌려주기 - ui 변경시 에러
                //result.setText(total);
                return total;
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            //오류시 null 반환
            return null;
        }
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
}
