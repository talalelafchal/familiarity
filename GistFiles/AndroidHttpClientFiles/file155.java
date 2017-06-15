package com.ozateck.jsonparser;

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

/*
 * インターネット上から、JSON形式のデータを取得して解析するクラス
 * 
 * AndroidManifest.xmlに、下記パーミッションを追記しておく事
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 */

public class MainActivity extends Activity{
	
	private static String TAG = "myTag";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate()");
		
		// Android3.0からの端末に必須になる、StrictModeの設定(StrictModeクラスは、Android2.3以降に存在)
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				
		// DefaultHttpClient
		HttpClient httpClient = new DefaultHttpClient();
		
		// 発行したいHTTPリクエストを生成
		StringBuilder url = new StringBuilder(
				"http://free.worldweatheronline.com/feed/weather.ashx?q=0.00,0.00&format=json&num_of_days=5&key=cefd674746023359121308");
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
					
					// JSONオブジェクトの取得
					try{
						
						JSONObject rootObj = new JSONObject(data);
						JSONObject dataObj = rootObj.getJSONObject("data");
						JSONArray dataArray = dataObj.getJSONArray("current_condition");
						Log.d(TAG, "count:" + dataArray.length());
						
						for(int i=0; i<dataArray.length(); i++){
							JSONObject obj = dataArray.getJSONObject(i);
							// ヒント
							Log.d(TAG, "cloudcover:" + obj.getString("cloudcover"));
							// 他の情報を取るには。。。？？？
						}
						
						
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
			Log.e(TAG, "httpResponse:null");
		}
	}
}
