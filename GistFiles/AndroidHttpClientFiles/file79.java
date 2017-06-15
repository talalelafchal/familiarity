package com.example.jen6.tester;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jen6 on 2015-07-13.
 */

class LoginSession {
    static String Session = "";
    static public String get_session() {
        if (Session == ""){
            HttpClient http = new DefaultHttpClient();
            CookieStore cookieStore = new BasicCookieStore();
            String url = "http://makeall.ml:8989/login";
            try{
                HttpContext context = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);

                ArrayList<NameValuePair> nameValuePairs =
                        new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("Id", "jen6"));
                nameValuePairs.add(new BasicNameValuePair("Pw", "abcd"));   //post 파라미터 셋팅
                UrlEncodedFormEntity entityRequest =
                        new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                httpPost.setEntity(entityRequest);

                context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
                HttpGet httpGet = new HttpGet("http://makeall.ml:8989/");
                HttpResponse response = http.execute(httpGet, context);
                CookieStore store = ((DefaultHttpClient)http).getCookieStore();
                List<Cookie> cookies = store.getCookies();
                if (cookies != null) {
                    for (Cookie c : cookies) {
                        Session = c.getValue();
                    }
                }
            }catch(Exception e){e.printStackTrace();}
    }
        return Session;
}

//글쓰기용
public class BusPostSetter {

    public HttpClient http = new DefaultHttpClient();
    public CookieStore cookieStore = new BasicCookieStore();
    //    쿠키스토어랑 httpclient를 퍼블릭으로 선언

    //  프리레퍼런스 사용할 객체 선언
    public BusPostSetter() {
    }

    public BusPostSetter(String Title, String Content, String Want) {
        try {
            HttpContext context = new BasicHttpContext();

            HttpPost httpPost = new HttpPost("http://makeall.ml:8989/board/bus");
            httpPost.addHeader("my_session", LoginSession.get_session());
            ArrayList<NameValuePair> nameValuePairs =
                    new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("Title", Title));
            nameValuePairs.add(new BasicNameValuePair("Content", Content));   //post 파라미터 셋팅
            nameValuePairs.add(new BasicNameValuePair("Want", Want));
            UrlEncodedFormEntity entityRequest =
                    new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            httpPost.setEntity(entityRequest);

            HttpResponse responsePost = http.execute(httpPost);
//          리턴값
            Log.d("msg", "login");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class BusList{
    int Id, Want;
    String Title, Content;
}


public class BusListGetter extends AsyncTask<Void, Void, Void> {
    //실행 new GetContacts().execute();
    private ProgressDialog pDialog;
    // URL to get contacts JSON
    private String url = "http://makeall.ml:8989/board/buslist/1";
    // JSON Node names
    private static final String TAG_TITLE = "Title";
    private static final String TAG_CONTENT = "Content";
    private static final String TAG_WANT = "Want";
    ArrayList<HashMap<String, String>> gulList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
// Showing progress dialog
        //pDialog = new ProgressDialog(main.this);
        //pDialog.setMessage("Please wait...");
        //pDialog.setCancelable(false);
        //pDialog.show();
    }
    @Override
    protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();
// Making a request to url and getting response

        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
        Log.d("Response: ", "> " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONArray gul = new JSONArray(jsonStr);
// Getting JSON Array node
// looping through All Contacts
                for (int i = 0; i < gul.length(); i++) {
                    JSONObject c = gul.getJSONObject(i);
                    String content = c. getString(TAG_CONTENT);
                    String title = c. getString(TAG_TITLE);
                    String want = c. getString(TAG_WANT);
// tmp hashmap for single contact
                    HashMap<String, String> gulMap = new HashMap<String, String>();
// adding each child node to HashMap key => value
                    gulMap.put(TAG_WANT, want);
                    gulMap.put(TAG_TITLE, title);
                    gulMap.put(TAG_CONTENT, content);

// adding contact to contact list
                    gulList.add(gulMap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
// Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public BusList[] Get(int idx){}
}

class Bus{
    int Id, WriterId, Want;
    String Writer, Title, Content;
}

class BusGetter {
    public BusGetter(){}
    public Bus Get(int Id){}
}




