package de.schmitt.michael.lebensmittelwarnung.de;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskRegisterForMultiCast extends AsyncTask<String, Void, String>{

	private String id;
	private final static String URL="DEINEURL/register_for_multi_cast.php?id=";
	private final static String TAG="register_multicast";
	
	public AsyncTaskRegisterForMultiCast(String id){
		this.id=id;
	}
	@Override
	protected String doInBackground(String... arg0) {
		String url =URL + id;
		Log.v(TAG, url);
		
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse response;
		try {
			response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			Log.v(TAG, String.valueOf(statusCode));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
