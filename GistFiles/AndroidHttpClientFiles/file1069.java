package tw.org.iii.kh;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class KHAPI
	private String TAG = "RequestAPI";
	private String URL;
	private List<NameValuePair> parameters;
	private String method;
	private JSONObject returnResult = new JSONObject();
	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}
	
	public KHAPI(){
		URL = APIConstranst.URL;
	}
	
	public KHAPI(String method){
		URL = APIConstranst.URL ;
//		this.method = method;
	}
	
	//for kh
	public JSONObject byPost(final String data, Handler mHandler){
		mHandler.post(runnable);		
	}

	private Runnable runnable= new Runnable() {    
        public void run() {  
             
            if (run) {  
            	String result = new String();
		JSONObject jresult = null;
		/*
		 * 設定http 的timeout
		 * 資料來源:http://stackoverflow.com/questions/693997/how-to-
		 * set-httpresponse-timeout-for-android-in-java
		 */
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpPost httpPost = new HttpPost(URL);
		
		try {
			// 連線
			StringEntity jsonEntity = new StringEntity(data, HTTP.UTF_8);
	        httpPost.setHeader("Content-type", "application/json");
	        httpPost.setEntity(jsonEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse != null && httpClient != null) {
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = EntityUtils.toString(httpResponse.getEntity());
					jresult = new JSONObject(result);  
				}
			}else{
				Log.e(TAG,  "HttpPost方式請求失敗" ); 
			}
		} catch (UnsupportedEncodingException e) {
			// exception = "UnsupportedEncodingException";
		} catch (ClientProtocolException e) {
			// exception = "ClientProtocolException";
		} catch (IOException e) {
			// exception = "IOException";
		} catch (Exception e) {
			Log.e("Exception", e.toString());
			// exception = "Exception";
		} catch (OutOfMemoryError e) {
		}
		returnResult = jresult;
		Message message = new Message();   
		message.what = 0;
		message.obj = returnResult;
		myHandler.sendMessage(message);
            } 
 
        }  
    };	
}
