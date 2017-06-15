package com.model.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @description For communication for Web Service API
 * 
 */
public class HttpConnectionUtils {
	
	private static String server_url = "http://api.opendevicelab.com/?countries=Belgium,France,England,South%20Africa&brands=Samsung,RIM&types=resident,mobile";
	
	public static JSONArray getODL_Data(){

		String res = connectGetToServer(server_url);
		if(res == null)
			return null;
		try {
			JSONArray result = new JSONArray(res);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	private static String connectGetToServer(String url) {
		
		HttpParams params = new BasicHttpParams();
		
		HttpClient httpclient = new DefaultHttpClient(params);
		
		HttpGet httpget = new HttpGet(url);
		
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
			InputStream istream = response.getEntity().getContent();
			return convertStreamToString(istream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String convertStreamToString(InputStream is)
		throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}
}
