package com.gmail.masuken.httpdemo;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.net.Uri;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

//import android.os.AsyncTask;
//import android.net.http.AndroidHttpClient;


/*
 * References on http://terurou.hateblo.jp/entry/20110702/1309541200
 */
public class MainActivity extends Activity {	
	TextView resultTv = null; // 結果を表示するためのTextView
	private static String SCHEMA = "https";
	private static String URL = "pigg.ameba.jp";
	private static String URL_PATH = "/";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	
    	// GET Requestを構築する。
    	// Uri.Builderを使うとURIエンコードも適切にやってくれる。
    	Uri.Builder builder = new Uri.Builder();
    	builder.scheme(SCHEMA);
    	builder.encodedAuthority(URL);
    	builder.path(URL_PATH);
    	//builder.appendQueryParameter("format", "xml");
    	
    	HttpGet request = new HttpGet(builder.build().toString());

    	// HttpClientインタフェースではなくて、実クラスのDefaultHttpClientを使う。
    	// 実クラスでないとCookieが使えないなど不都合が多い。
    	DefaultHttpClient httpClient = new DefaultHttpClient();

    	try {
    	    String result = httpClient.execute(request, new ResponseHandler<String>() {
    	    	
    	        public String handleResponse(HttpResponse response)
    	        		throws ClientProtocolException, IOException {
	    	        		// response.getStatusLine().getStatusCode()でレスポンスコードを判定する。
	    	            	// 正常に通信できた場合、HttpStatus.SC_OK（HTTP 200）となる。
	    	        	
	    	        		switch (response.getStatusLine().getStatusCode()) {
	    	        		case HttpStatus.SC_OK:
	    	        			return EntityUtils.toString(response.getEntity(), "UTF-8");
	    	        		case HttpStatus.SC_NOT_FOUND:
	    	                    throw new RuntimeException("データないよ！");
	    	                default:
	    	                    throw new RuntimeException("なんか通信エラーでた");
	    	        		}
    	        		}
    	    	});

    	    	//Log.d("test", result);
    	    	// 結果を表示するためのテキストビューを取得
            	resultTv = (TextView)findViewById(R.id.resultTv);
    	    	resultTv.setText(result.toString());

    	} catch (ClientProtocolException e) {
    		throw new RuntimeException(e);
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	} finally {    
    		httpClient.getConnectionManager().shutdown();
    	}
    }

}
