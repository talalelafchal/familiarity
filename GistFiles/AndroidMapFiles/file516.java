//
//  FLHTTPUtils.java
//  HTTP Utilities (Synchronous)
//
//  Last Updated:2013-10-29
//
//  Created by Yunzhu Li.
//  Copyright (c) 2013 FatLYZ.COM. All rights reserved.
//

package org.fatlyz.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HTTP Utilities (Synchronous)
 * @author fatlyz
 *
 */
public class FLHTTPUtils {

	public static String FLHTTPParamKeyData = "data";
	public static String FLHTTPParamKeyDataLength = "data_length";
	
	/**
	 * Send synchronous request(HTTP GET)
	 * @param urlString The URL string.
	 * @param params Parameters(GET)
	 * @return HashMap of response data and length
	 */
	public static HashMap<String, Object> flSynchronousRequest(String urlString,
			HashMap<String, String> params) {
		// Check url
//		if (urlString.length() <= 0)
//			return null;

		// Generate final urlString
		StringBuffer sbUrl = new StringBuffer();
		sbUrl.append(urlString);
		Boolean isFirstParam = true;

		// Append parameters
		if (params != null) {
			Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
						.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (isFirstParam) {
					sbUrl.append("?");
					isFirstParam = false;
				} else {
					sbUrl.append("&");
				}
				sbUrl.append(key + "=" + val);
			}
		}
		
		// TODO:Debug
		//Log.d("FLHTTPUtils-Request:", sbUrl.toString());
		
		// Prepare to request
		byte[] data = null;
		Integer contentLength = null;
		try {
			// Create URL object
			URL url = new URL(sbUrl.toString());
			// Opens HttpURLConnection
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			// Set timeout
			urlConn.setConnectTimeout(5 * 1000);
			// Connect
			urlConn.connect();
			// Get response code
			if (urlConn.getResponseCode() == 200) {
				// Get length object
				contentLength = Integer.valueOf(urlConn.getContentLength());
				// Read stream
				data = readStream(urlConn.getInputStream());
			}
			
			// Close connection
			urlConn.disconnect();
		} catch (Exception e) { e.printStackTrace(); }
		
		// Compose result
		HashMap<String, Object> hmapResult = new HashMap<String, Object>(); 
		hmapResult.put(FLHTTPParamKeyData, data);
		hmapResult.put(FLHTTPParamKeyDataLength, contentLength);
		
		return hmapResult;
	}

	/**
	 * Read stream
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		byte[] data = baos.toByteArray();
		inputStream.close();
		baos.close();
		return data;
	}

}
