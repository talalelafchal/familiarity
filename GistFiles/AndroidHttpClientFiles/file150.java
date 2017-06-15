package com.ozateck.notification;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

public class WeatherDetectJson{

	private static String TAG = "WeatherDetectJson";
	
	private String nameStr;
	private String mainStr;
	private String descriptionStr;
	
	public WeatherDetectJson(double lat, double lon){

		// Android3.0からの端末に必須になる、StrictModeの設定(StrictModeクラスは、Android2.3以降に存在)
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
		
		detect(lat, lon);
	}
	
	private void detect(double lat, double lon){
		
		// DefaultHttpClient
		HttpClient httpClient = new DefaultHttpClient();
		
		// 発行したいHTTPリクエストを生成
		String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon;
		HttpGet request = new HttpGet(url.toString());
		
		// Getリクエストを送信
		HttpResponse httpResponse = null;
		try{
			httpResponse = httpClient.execute(request);
		}catch(ClientProtocolException e){
			Log.e(TAG, "CPE:" + e.toString());
		}catch(IOException e){
			Log.e(TAG, "IOE:" + e.toString());
		}catch(Exception e){
			Log.e(TAG, "Ex:" + e.toString());
		}
		
		// httpResponseが空でなかったら処理を続行
		if(httpResponse != null){
			int status = httpResponse.getStatusLine().getStatusCode();
			if(status == HttpStatus.SC_OK){
				try{
					// データの取得
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					httpResponse.getEntity().writeTo(baos);
					String data = baos.toString();
					Log.d(TAG, "-data-\n" + data);
					
					try{
						JSONObject rootObj = new JSONObject(data);
						
						JSONObject coordObj = rootObj.getJSONObject("coord");
						Log.d(TAG, "lat:" + coordObj.getString("lat"));
						Log.d(TAG, "lon:" + coordObj.getString("lon"));
						
						JSONArray weatherArray = rootObj.getJSONArray("weather");
						JSONObject weatherObj = weatherArray.getJSONObject(0);
						Log.d(TAG, "id:" + weatherObj.getString("id"));
						Log.d(TAG, "main:" + weatherObj.getString("main"));
						Log.d(TAG, "description:" + weatherObj.getString("description"));
						Log.d(TAG, "icon:" + weatherObj.getString("icon"));
						
						Log.d(TAG, "name:" + rootObj.getString("name"));
						
						nameStr = rootObj.getString("name");
						mainStr = weatherObj.getString("main");
						descriptionStr = weatherObj.getString("description");
						
					}catch(JSONException e){
						Log.e(TAG, "JSONE:" + e.toString());
					}
				}catch(IOException e){
					Log.e(TAG, "IOE:" + e.toString());
				}
			}else{
				Log.e(TAG, "status:" + status);
			}
		}else{
			//splashTypeStr = "0";
			Log.e(TAG, "httpResponse:null");
		}
	}
	
	public String getNameStr(){
		return nameStr;
	}
	
	public String getMainStr(){
		return mainStr;
	}
	
	public String getDescriptionStr(){
		return descriptionStr;
	}
}