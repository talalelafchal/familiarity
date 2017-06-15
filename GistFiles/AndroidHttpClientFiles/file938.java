package com.ped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class TwitterConsumer {

	private final URL searchURL;
	private final String url;
	
	public TwitterConsumer(String searchURL) {
		try {
			this.searchURL = new URL(searchURL);
			this.url = searchURL;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getRequest() { 
		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(new HttpGet(this.url));
		
			return inputStreamToString(response.getEntity().getContent()); 
		} catch (Exception e) {
			Log.e("twitter", e.getMessage()); 
			
		}	
		return null; 
	}

	public static String inputStreamToString(InputStream receiveString) throws IOException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(receiveString));
    	StringBuilder sb = new StringBuilder();
    	String line;
    	
    	while ( (line = br.readLine()) != null) {
    		sb.append(line + "\n");
    	}
    	
    	receiveString.close();
    	return sb.toString();
    }
	
}
