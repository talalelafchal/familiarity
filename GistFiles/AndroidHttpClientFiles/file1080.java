package com.uet.httptest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class HttpUtils extends AsyncTask<String, Void, String>{
  String pResponse = "";
	@Override
	protected String doInBackground(String... params) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://192.168.125.55/Translate.php");
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("data_bitmap", params[0]));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpClient.execute(httpPost, responseHandler);

			// This is the response from a php application
			pResponse = response;
			Log.e("Result", "Result server: " + response);
		} catch (ClientProtocolException e) {
			return "";
			// TODO Auto-generated catch block
		} catch (IOException e) {
			return "";
			// TODO Auto-generated catch block
		}

		return pResponse;
	}
}
