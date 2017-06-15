package com.example.user.j_son3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.user.j_son3.JSONParser;

public class MainActivity extends Activity {
    ListView list;
    TextView state;
    TextView name;
    Button Btngetdata;
    TextView textView, textView2;

    int send;
    String com = new String();

    Activity act = this;
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    //URL to get JSON Array
    private static String url = "http://capstonedesign.dothome.co.kr/";

    //JSON Node Names
    private static final String TAG_OS = "tt";
    private static final String TAG_NAME = "name";
    private static final String TAG_STATE = "state";
    JSONArray android = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oslist = new ArrayList<HashMap<String, String>>();
        Btngetdata = (Button)findViewById(R.id.getdata);
        Btngetdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send = 0;
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                new HttpTask().execute();
                new JSONParse().execute();
            }
        });

           /* INTENT 변경 */
            findViewById(R.id.count_salary).setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, count_salary.class);
                    startActivity(intent);
                }
        });

        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            name = (TextView)findViewById(R.id.mName);
            state = (TextView)findViewById(R.id.mState_work);

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
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
                    String state = c.getString(TAG_STATE);

                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_NAME, name);
                    if(state.equals("1"))
                        map.put(TAG_STATE, "YES");
                    else
                        map.put(TAG_STATE, "NO");
                    oslist.add(map);
                    list=(ListView)findViewById(R.id.list);
                    ListAdapter adapter = new SimpleAdapter(MainActivity.this, oslist, R.layout.list_v, new String[] {TAG_NAME, TAG_STATE}, new int[] {
                            R.id.mName,R.id.mState_work});
                    list.setAdapter(adapter);

                    //출퇴근 값 변경
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(MainActivity.this, oslist.get(position).get("name"), Toast.LENGTH_SHORT).show();
                            send = 1;
                            com = "1;" + oslist.get(position).get("name");
                            Toast.makeText(MainActivity.this, com, Toast.LENGTH_SHORT).show();
                            new HttpTask().execute();
                            oslist.clear();
                            new JSONParse().execute();
                        }
                    });
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
                HttpPost request = new HttpPost(url);
                //전달할 인자들
                Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
                if(send == 0) {
                    nameValue.add(new BasicNameValuePair("command", "0"));
                }else if(send == 1) {
                    nameValue.add(new BasicNameValuePair("command", com));
                }

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
}