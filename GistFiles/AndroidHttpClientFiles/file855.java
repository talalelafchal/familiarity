package com.squaar.comparar;

import java.io.BufferedReader;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Intent;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class ConexionHttpGet {
	
	private DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpGet request = new HttpGet();
    InputStream content = null;

    public int getItems() throws Exception {
    	
    	MylocalData.getInstance().strUrl = "https://api.mercadolibre.com/sites/MLA/search?q=ipad";
    	
    	String url = MylocalData.getInstance().strUrl;
 
    	HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(new HttpGet(url));
        StatusLine statusLine = response.getStatusLine();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getEntity().writeTo(out);
        out.close();
        String responseString = out.toString();
        
        MylocalData.getInstance().itemsCategory = new JSONObject(responseString);
        
        MylocalData.getInstance().arrItems = MylocalData.getInstance().itemsCategory.getJSONArray("results");
        
        //imprime resultados
        System.out.println("content: "+MylocalData.getInstance().arrItems.length());
        
        
        

	       /* List nameValuePairs = new ArrayList(1);
	        nameValuePairs.add(new BasicNameValuePair("action", "loadNoticias"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
 
            HttpResponse response = httpclient.execute(httppost);
 
            InputStream is = response.getEntity().getContent();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
 
            int current = 0;
             
            while((current = bis.read()) != -1){
                baf.append((byte)current);
            }  
 
           
            text = new String(baf.toByteArray());
            
     	   MylocalData.getInstance().jsonNovedades = new JSONObject(text);
    	   
     	   MylocalData.getInstance().jsonNovArr = MylocalData.getInstance().jsonNovedades.getJSONArray("noticias");
 	   
	 	   MylocalData.getInstance().longNovedades = MylocalData.getInstance().jsonNovArr.length();
	 	   
	 	   System.out.println("jsonNovArr: "+MylocalData.getInstance().jsonNovArr);*/
     	   
        return 1;

    }
   
 
 private StringBuilder inputStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();

	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    try {
	        while ((line = rd.readLine()) != null) { 
	            total.append(line); 
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Return full string
	    return total;
	}


}
