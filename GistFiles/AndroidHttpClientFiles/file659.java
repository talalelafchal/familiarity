package com.fotoespiritu.twcurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class HttpRest {

	public HttpRest() {
		// TODO Auto-generated constructor stub
	}
	
	private static String inputStreamToString(InputStream is) {
		 
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            Log.e("IOException", e.getLocalizedMessage(), e);
        }

        // Return full string
        return total.toString();
    }

	
	public static JSONArray doGet(String url) {
		JSONArray json = null;
	    HttpClient httpclient = new DefaultHttpClient();
	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url);
	    // Accept JSON
	    httpget.addHeader("accept", "application/json");
	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        // Get the response entity
	        HttpEntity entity = response.getEntity();
	        // If response entity is not null
	        if (entity != null) {
	            // get entity contents and convert it to string
	            InputStream instream = entity.getContent();
	            String result= inputStreamToString(instream);
	            // construct a JSON object with result
	            json=new JSONArray(result);
	            // Closing the input stream will trigger connection release
	            instream.close();
	        }
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    // Return the json
	    return json;
	
	}
	
	
	public static JSONObject doGetJson(String url) {
		JSONObject json = null;
	    HttpClient httpclient = new DefaultHttpClient();
	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url);
	    // Accept JSON
	    httpget.addHeader("accept", "application/json");
	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        // Get the response entity
	        HttpEntity entity = response.getEntity();
	        // If response entity is not null
	        if (entity != null) {
	            // get entity contents and convert it to string
	            InputStream instream = entity.getContent();
	            String result= inputStreamToString(instream);
	            // construct a JSON object with result
	            json=new JSONObject(result);
	            // Closing the input stream will trigger connection release
	            instream.close();
	        }
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    // Return the json
	    return json;
	
	}
	
	public static void doGetWithIgnoreResponse(String url) {
		JSONObject json = null;
	    HttpClient httpclient = new DefaultHttpClient();
	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url);
	    // Accept JSON
	    httpget.addHeader("accept", "application/json");
	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        // Get the response entity

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return;
	
	}
	
	public static HttpResponse doPost(String url, JSONObject c) throws ClientProtocolException, IOException 
	{
		Log.i("POST>>", url);
		Log.i("JSON>>", c.toString());
		
		JSONObject json = null;
        HttpClient httpclient = new DefaultHttpClient();
        
        HttpPost request = new HttpPost(url);
//        StringEntity s = new StringEntity(c.toString());
//        s.setContentEncoding("UTF-8");
//        s.setContentType("application/json");
//        request.setEntity(s);
		

        request.setEntity(new StringEntity(c.toString(), HTTP.UTF_8));
        
        request.setHeader("Content-Type", "application/json; charset=utf-8");

        request.addHeader("accept", "application/json");

        return httpclient.execute(request);

	    
    }
	
	public static JSONObject doPostResponseJson(String url, JSONObject c) throws ClientProtocolException, IOException 
	{
		Log.i("POST>>", url);
		Log.i("JSON>>", c.toString());
		
		JSONObject json = null;
        HttpClient httpclient = new DefaultHttpClient();
        
        HttpPost request = new HttpPost(url);
//        StringEntity s = new StringEntity(c.toString());
//        s.setContentEncoding("UTF-8");
//        s.setContentType("application/json");
//        request.setEntity(s);
		

        request.setEntity(new StringEntity(c.toString(), HTTP.UTF_8));
        
        request.setHeader("Content-Type", "application/json; charset=utf-8");

        request.addHeader("accept", "application/json");

//        return httpclient.execute(request);
	    HttpResponse response;
	    try {
	        response = httpclient.execute(request);
	        // Get the response entity
	        HttpEntity entity = response.getEntity();
	        // If response entity is not null
	        if (entity != null) {
	            // get entity contents and convert it to string
	            InputStream instream = entity.getContent();
	            String result= inputStreamToString(instream);
	            // construct a JSON object with result
	            json=new JSONObject(result);
	            // Closing the input stream will trigger connection release
	            instream.close();
	        }
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    // Return the json
	    return json;
	    
    }
	
	
	
	
	public static HttpResponse doPost(String url, ArrayList<NameValuePair> c) throws ClientProtocolException, IOException 
	{
 
        HttpClient httpclient = new DefaultHttpClient();
        
        HttpPost request = new HttpPost(url);
//        StringEntity s = new StringEntity(c.toString());
//        s.setContentEncoding("UTF-8");
//        s.setContentType("application/json");
//        request.setEntity(s);
//		Log.d("LOG", c.toString());

        request.setEntity(new UrlEncodedFormEntity(c, HTTP.UTF_8));
        
        request.setHeader("Content-Type", "application/json; charset=utf-8");

        request.addHeader("accept", "application/json");

        return httpclient.execute(request);
    }

	
	
	
	
	public static HttpResponse doPut(String url, JSONObject c) throws ClientProtocolException, IOException
	{
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPut request = new HttpPut(url);

	        
//	        StringEntity s = new StringEntity(c.toString(), HTTP.UTF_8);
	        //s.setContentEncoding("UTF-8");
//	        s.setContentType("application/json");
	        
//	        Log.i("PUT JSON", c.toString());
	        
	        request.setEntity(new StringEntity(c.toString(), HTTP.UTF_8));
	        
	        request.setHeader("Content-Type", "application/json; charset=utf-8");
	        request.addHeader("accept", "application/json");

	        return httpclient.execute(request);
	}
	
	//
	public static JSONArray doPostWithResponse(String url, JSONObject c) throws ClientProtocolException, IOException 
	{
		JSONArray json = null;
		
        HttpClient httpclient = new DefaultHttpClient();
        
        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(c.toString(), HTTP.UTF_8));
        request.setHeader("Content-Type", "application/json; charset=utf-8");
        request.addHeader("accept", "application/json");

	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(request);
	        // Get the response entity
	        HttpEntity entity = response.getEntity();
	        // If response entity is not null
	        if (entity != null) {
	            // get entity contents and convert it to string
	            InputStream instream = entity.getContent();
	            String result= inputStreamToString(instream);
	            // construct a JSON object with result
	            json=new JSONArray(result);
	            // Closing the input stream will trigger connection release
	            instream.close();
	        }
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    // Return the json
	    return json;
    }

	
	
	public static HttpResponse doDelete(String url) throws  ClientProtocolException, IOException{
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpDelete delete = new HttpDelete(url);
    	delete.addHeader("accept", "application/json");
    	return httpclient.execute(delete);
	}
	
	public static String responseToString(HttpResponse response)
	{
		String result = null;
        HttpEntity entity = response.getEntity();
        // If response entity is not null
        if (entity != null) {
            // get entity contents and convert it to string
            InputStream instream;
			try {
				instream = entity.getContent();
	            result= inputStreamToString(instream);
	            // construct a JSON object with result
	            instream.close();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
        }
        return result;
	}

	
	
	public static JSONObject responseToJson(HttpResponse response)
	{
		JSONObject json = null;
        HttpEntity entity = response.getEntity();
        // If response entity is not null
        if (entity != null) {
            // get entity contents and convert it to string
            InputStream instream;
			try {
				instream = entity.getContent();
	            String result= inputStreamToString(instream);
	            
	            Log.i("JSON",result);
	            
	            // construct a JSON object with result
	            json=new JSONObject(result);
	            // Closing the input stream will trigger connection release
	            instream.close();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("JSON", e.toString());
			}
			
        }
        return json;
	}

	public static boolean checkInternet(Context context) 
	{
	    boolean result = false;
	    try {
	        // get the ConnectivityManager for handling management of network connections.
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        //
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
	 

	        if (mNetworkInfo == null && !mNetworkInfo.isConnectedOrConnecting()) {
	        	result = false;
	        } else {
	            //isNetError = true;
	        	if (!mNetworkInfo.isAvailable())
	    		{
	    			result =false;
	    		}
	    		else
	    		{
	    			result = true;
	    		}
	        }/* end of if */
	 
	    } catch (Exception e) {
	    	result = false;
	    } /* end of try-catch */
	 
	    return result;
	}
	
}
