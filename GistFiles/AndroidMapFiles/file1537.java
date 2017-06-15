package com.example.JSONDemo3;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 本例子主要实现了以下几个功能：
 * 1. 通过网络连接获取JSON（String 类型）
 * 2. 读取和操作JSON 数据
 * 3. 从JSON 数据中读取联系人姓名，电子邮箱，移动电话，并将其显示到ListView 中
 * 
 * 本文参考：[Android JSON Parsing Tutorial](http://www.androidhive.info/2012/01/android-json-parsing-tutorial/)
 */
public class MyActivity extends Activity {
    private static final String TAG = "MyActivity";

    /** 要读取的URL */
    private static final String JSON_URL = "http://api.androidhive.info/contacts/";

    /** 将从JSON 读取的数据放置都此List 中 */    
    List<HashMap<String, String>> contactsList;
    /** 将最后的读取的数据放置在ListView */
    private ListView listView;

    
    /** JSON 的的数据 联系人 */
    private static final String TAG_CONTACTS = "contacts";
    /** JSON 的的数据 Id */
    private static final String TAG_ID = "id";
    /** JSON 的的数据 姓名 */
    private static final String TAG_NAME = "name";
    /** JSON 的的数据 电子邮箱 */
    private static final String TAG_EMAIL = "email";
    /** JSON 的的数据 地址 */
    private static final String TAG_ADDRESS = "address";
    /** JSON 的的数据 性别 */
    private static final String TAG_GENDER = "gender";
    /** JSON 的的数据 电话 */
    private static final String TAG_PHONE = "phone";
    /** JSON 的的数据 电话 移动电话 */
    private static final String TAG_PHONE_MOBILE = "mobile";
    /** JSON 的的数据 电话 家庭电话 */
    private static final String TAG_PHONE_HOME = "home";
    /** JSON 的的数据 电话 工作电话 */
    private static final String TAG_PHONE_OFFICE = "office";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.list_view_contacts_id);
    }
    
    /** 点击按钮执行此方法 */
    public void getUrlBtn(View view) {
        new MyAsyncTask().execute();
    }

    /** 并行任务处理 通过网络连接获取JSON 数据，并从获取的结果，获取详细的Contacts 信息 */
    class MyAsyncTask extends AsyncTask<Void,Void, Void> {

        /** 调用getJSONByUrl() 获取JSON 数据，并传给readAndParseJSON() 方法操作JSON 数据 */
        @Override
        protected Void doInBackground(Void... params) {

            String result = getJSONByUrl();
            // Log.i(TAG, "MyAsyncTask doInBackground" + result);
            readAndParseJSON(result);

            return null;
        }
        
        /** 在 doInBackground() 方法执行后，将其获取的数据显示在ListView 中 */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /** 设置适配器 */
            SimpleAdapter listAdapter = new SimpleAdapter(MyActivity.this,
                    contactsList,
                    R.layout.list_item,
                    new String[]{TAG_NAME, TAG_EMAIL, TAG_PHONE_MOBILE},
                    new int[]{
                            R.id.text_view_contacts_name_id,
                            R.id.text_view_contacts_email_id,
                            R.id.text_view_contacts_mobile_phone_id});
            /** ListView 设置适配器 */
            listView.setAdapter(listAdapter);
        }
    }

    /** 读取和操作JSON 数据 
     *  @param result 通过URL 读取获取的JSON 值（String 类型） 
     */
    public void readAndParseJSON(String result) {

        // Log.i(TAG, "readAndParseJSON");
        contactsList = new ArrayList<HashMap<String, String>>();

        JSONObject jsonObject;
        try {
            // 将字符串传入JSONObject 对象
            jsonObject = new JSONObject(result);

            // 获取子JSONArray contacts
            JSONArray contactsJSONArray = jsonObject.getJSONArray(TAG_CONTACTS);

            // 通过遍历获取每个联系人（JSONObject）的详细纤细
            for (int i = 0; i < contactsJSONArray.length(); i++) {
                JSONObject contactsJSONObject = contactsJSONArray.getJSONObject(i);
                String id = contactsJSONObject.getString(TAG_ID);
                String name = contactsJSONObject.getString(TAG_NAME);
                String email = contactsJSONObject.getString(TAG_EMAIL);
                String address = contactsJSONObject.getString(TAG_ADDRESS);
                String gender = contactsJSONObject.getString(TAG_GENDER);

                JSONObject phoneJSONObject = contactsJSONObject.getJSONObject(TAG_PHONE);
                String home_phone = phoneJSONObject.getString(TAG_PHONE_HOME);
                String mobile_phone = phoneJSONObject.getString(TAG_PHONE_MOBILE);
                String office_phone = phoneJSONObject.getString(TAG_PHONE_OFFICE);

                HashMap<String, String> phoneMap = new HashMap<String, String>();
                phoneMap.put(TAG_ID, id);
                phoneMap.put(TAG_NAME, name);
                phoneMap.put(TAG_EMAIL, email);
                phoneMap.put(TAG_PHONE_MOBILE, mobile_phone);

                // Log.i(TAG, name + " : " + email + " : " + mobile_phone);

                contactsList.add(phoneMap);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** 通过URL 获取JSON 数据 */
    public String getJSONByUrl() {
        // Log.i(TAG, "getJSONByUrl");

        StringBuffer sb = null;
        try {
            URL url = new URL(JSON_URL);
            HttpURLConnection urlConnection
                    = (HttpURLConnection) url.openConnection();

            int statusCode = urlConnection.getResponseCode();
            InputStream in = null;
            if (statusCode == 200) {

                in= urlConnection.getInputStream();
                BufferedReader reader
                        = new BufferedReader(new InputStreamReader(in, "utf-8"));

                sb = new StringBuffer();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    String str = new String(line);
                    // 将读取的数据存放在StringBuffer 中，并返回
                    sb.append(str);
                    // Log.i(TAG, str);
                }
                // Log.i(TAG, "sb = " + sb.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
