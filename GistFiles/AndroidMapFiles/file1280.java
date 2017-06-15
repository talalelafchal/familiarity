package com.w4rlock.cacheManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class getURLdata {
  public static final String DEBUG_TAG = "[cache-manager][network]";
	public static final String TIMEOUT_TAG = "[cache-manager][timeout-network]";
	public String result=null;
    private int readTimeOut=20000;
    private int connTimeOut=30000;

	public void getData(String url) {
		new DownloadWebpageText().execute(url);
	}
	protected void postresult_internal(Object result) {
		postresult(result);
	}
	public void postresult(Object result2){
		result = (String) result2;
		Log.d(DEBUG_TAG,"result2 "+result2);
	}
	
	public void onTimeOut(Object result2){
		Log.d(DEBUG_TAG,"TIME - OUT ");
	}
	
	public void onError(Object result2){
		Log.d(DEBUG_TAG,"Error");
	}
    protected int getReadTimeOut() {
        return readTimeOut;
    }

    protected void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    protected int getConnTimeOut() {
        return connTimeOut;
    }

    protected void setConnTimeOut(int connTimeOut) {
        this.connTimeOut = connTimeOut;
    }
	protected class DownloadWebpageText extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String data = (String) params[0];
			try {
				return downloadUrl(data);
			} catch (IOException e) {
				return "unable to get the data sry :(";
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result == null || result == " " || result == "") {
				onError(result);
			} else {
				if( ((String)result).equals(TIMEOUT_TAG) ){
					onTimeOut(result);
				}else{
					postresult_internal(result);
				}
			}
		}

		private String downloadUrl(String myurl) throws IOException {
			InputStream is = null;

			try {
				Log.d(DEBUG_TAG,(myurl));
				URL url = new URL(myurl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
//				conn.setReadTimeout(20000 /*10000 milliseconds */);
//				conn.setConnectTimeout(30000 /*15000 milliseconds */);
                conn.setReadTimeout(getReadTimeOut());
                conn.setConnectTimeout(getConnTimeOut());
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				try {
					conn.connect();
				} catch (Exception e) {
					Log.d(DEBUG_TAG,"Error while connection: " + e);
					return TIMEOUT_TAG;
				}
				Log.d(DEBUG_TAG,"connection request above");
				int response = conn.getResponseCode();
				Log.d(DEBUG_TAG, " " + response);
				try {
					is = conn.getInputStream();
				} catch (Exception e) {
					Log.d(DEBUG_TAG,"error while getting inputstream in do background: "+ e);
					return null;
				}
				String contentAsString = readAll(is);
				return contentAsString;

			} catch (Exception e) {
				Log.d(DEBUG_TAG,e.toString());
				return null;
			} finally {
				if (is != null) {
					is.close();
				} else {
					return null;
				}
			}
		}

		public String readAll(InputStream stream) throws IOException,
				UnsupportedEncodingException {
			StringBuilder sb = new StringBuilder();
			String s;
			try {
				Reader reader = new InputStreamReader(stream, "UTF-8");
				BufferedReader buf = new BufferedReader(reader);
				while (true) {
					s = buf.readLine();
					if (s == null || s.length() == 0)
						break;
					sb.append(s);
				}
				return sb.toString();
			} catch (Exception e) {
				Log.d(DEBUG_TAG,"error on readall ie catching input stream");
				return " ";
			}
		}
	}

}