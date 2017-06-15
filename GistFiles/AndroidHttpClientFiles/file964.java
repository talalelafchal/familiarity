package com.moventos.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//Execute Test GetMasterKey
    	getKey();
    	//
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
	public void getKey()
	{
		HttpClient httpclient 			= new DefaultHttpClient();
		HttpPost httpost 			= new HttpPost( "http://www.moventos.com/wapi/apikey/" );
		List<NameValuePair> nameValuePairs 	= new ArrayList<NameValuePair>(2);
		HttpResponse response;
		
		try {
			//Assign parameters Email & Key
			nameValuePairs.add(new BasicNameValuePair("email", "api-test@moventos.com"));
			nameValuePairs.add(new BasicNameValuePair("key", "aaaa"));
			httpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			//Execute Call
			response 		= httpclient.execute(httpost);
			HttpEntity entity 	= response.getEntity();
			if (entity != null){
				//Convert response to JSON
				InputStream instream 	= entity.getContent();
				String result		= convertStreamToString(instream);
				JSONObject json		= new JSONObject(result);
				/*
				 * Result is:
				 * 
				 * {"message":["Succes!"],"response":{"masterKey":"masterKeyForTest-aaaabbbbccccddddeeeeffffgggghhhh"}}
				 */
				
				//Show result
				Log.i("MOVENTOS", "MasterKey: "+json.getJSONObject("response").get("masterKey").toString());
				instream.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {	 
			Log.e("Exception", e.getMessage());	 
		}
	}
	
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 * @Autor: Praeda => http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}