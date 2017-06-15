package com.example.android_php_mysql_login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON_Handle {
	
	public String toJSON(Login_BEAN login_bean){
		try {
			JSONObject json_object = new JSONObject();
			json_object.put("tc", login_bean.getTc_No());
			return postDATA(json_object);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String postDATA(JSONObject JSON_object) throws JSONException {
		String kullanici_adi = null;
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost("http://192.168.1.4/android_php_mysql_login/login.php");
			httppost.setEntity(new StringEntity(JSON_object.toString()));
			httppost.setHeader("Content-type", "application/json");
			
			HttpResponse response = httpclient.execute(httppost);
			InputStream is = response.getEntity().getContent();
			String result = is_ToString(is).toString();
			
			JSONObject object = new JSONObject(result);
			kullanici_adi = object.getString("Kullanici_adi");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kullanici_adi;
	}
	
	private StringBuilder is_ToString(InputStream is) {
	String line = "";
        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}