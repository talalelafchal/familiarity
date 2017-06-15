package com.student.anurag.student_connect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by anurag on 4/25/2015.
 */
public class Tabasync2 extends AsyncTask<String, String, String> {
    static Posdent tempposdent;
    String result = null;
   private Tabs.MyInterface myInterface;
    //JSONParser jParser;
    JSONArray list;
    String url = new String();
    String text;
    InputStream is;
    ArrayList<Posdent> aResults = new ArrayList<Posdent>();

    public static Posdent send() {
        return tempposdent;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        list = new JSONArray();
    }

    @Override
    protected String doInBackground(String... sText) {
        String url_select = "http://amit2511.byethost22.com/receiver.php";


        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_select);

        this.text=sText[0];

        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(param));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection" + e.toString());
            //Toast.makeText(new ContextWrapper(this), "Please try again", Toast.LENGTH_LONG).show();
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result" + e.toString());
        }
        return result;
    }
    protected void onPostExecute(String v) {
        super.onPostExecute(v);
        //   pd.dismiss();
       aResults = getList(v);
        //Tabs t=new Tabs();
        myInterface.calling(aResults);
    }

    public ArrayList<Posdent> getList(String name) {
        tempposdent = new Posdent();
        String name1 = null;
        try {
           // boolean flag = false;
            JSONArray Jarray = new JSONArray(name);

            int k;
            for (k = 0; k < Jarray.length(); k++) {
                try {
                    JSONObject Jasonobject = null;
                    Jasonobject = Jarray.getJSONObject(k);
                    String msg = Jasonobject.getString("message");

                    name1 = Jasonobject.getString("s_name");
                   // Tabs t1=new Tabs();
                    //t1.testing(name1);

                    tempposdent = new Posdent();

                    tempposdent.setStudent_name(name1);
                    tempposdent.setMessage(msg);
                    for(int j=0;j<aResults.size();j++)
                    {
                       // if(aResults.get(j).hashCode()!=(tempposdent.hashCode())){
                            aResults.add(tempposdent);
                        //}
                    }
                } catch (JSONException js) {
                    js.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data" + e.toString());
        }
        Log.d(" I m here"," "+name1);
        Tabs t=new Tabs();
        t.testing(name1);
        return aResults;
    }
   /* public interface MyInterface{
        public void MyMethod()
    }*/
}
