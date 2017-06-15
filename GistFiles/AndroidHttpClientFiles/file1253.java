package com.example.SmsService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dd on 15.11.2014.
 */
 class HttpRequestTask extends AsyncTask<Context, Void, String> {
   static String l2="";
   Context mContext;
    public HttpRequestTask (Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(Context... params) {

        String line = null;
       l2="";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://televizera.net/sms/");
        try {
            /*StringEntity stringEntity = new StringEntity("act=createOrder&phone=79154508472&place=1&pays=1m2");
            post.setEntity(stringEntity);*/
          //  post.setHeader("Content-type", "text/html");
           String[] phone= IncomingSms.strMsgSrc.split("\\+");
           List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("act","createOrder"));
            nameValuePairs.add(new BasicNameValuePair("phone",phone[1]));//phone[1]
            nameValuePairs.add(new BasicNameValuePair("place","1"));
            nameValuePairs.add(new BasicNameValuePair("pays",IncomingSms.strMsgBody));//IncomingSms.strMsgBody


            post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            while ((line = rd.readLine()) != null) {
                l2 += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        l2+="";
       if( l2.contains( "OrderID")==true){
         IncomingSms.check=true;
       }else IncomingSms.check=false;
        return l2;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
       // IncomingSms.ddd(mContext);
    }
}
