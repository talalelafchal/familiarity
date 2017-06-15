package com.example.androidcallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class HttpGetAsyncTask extends
		AsyncTask<BasicNameValuePair, String, String> {

	public final String url;

	private OnHttpGetListener listener;

	public HttpGetAsyncTask(String url) {
		this.url = url;
	}

	@Override
	protected String doInBackground(BasicNameValuePair... params) {

		String returnValue = "";

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(url);

		for (BasicNameValuePair pair : params) {
			urlBuilder.append(pair.getName());
			urlBuilder.append("=");
			urlBuilder.append(pair.getValue());
		}

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlBuilder.toString());

		try {
			StringBuilder responseBuilder = new StringBuilder();
			HttpResponse response = client.execute(get);

			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content));
			String line;
			while ((line = reader.readLine()) != null) {
				responseBuilder.append(line);
			}

			returnValue = responseBuilder.toString();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public void setOnHttpGetListener(OnHttpGetListener l) {
		listener = l;
	}

	public void onPreExecute() {
		if (listener != null)
			listener.onHttpGetPreExecute();
	}

	public void onPostExecute(String result) {
		if (listener != null)
			listener.onHttpGetSuccess(result);
	}

	public interface OnHttpGetListener {
		public void onHttpGetSuccess(String result);

		public void onHttpGetPreExecute();
	}
}
