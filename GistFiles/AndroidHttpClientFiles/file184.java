package com.example.single;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              final ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
            	progress.setVisibility(View.VISIBLE);
            	new Backup().execute("http://kwebapps.com/service.php","get");
            	 
            }
        });
        
        final Button button2 = (Button)findViewById(R.id.BtnSendName);
        button2.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				final ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
            	progress.setVisibility(View.VISIBLE);
            	new Backup().execute("http://kwebapps.com/service.php","post");
				
			}
        	
        });
       
        
    }
    private class Backup extends AsyncTask<String, Void, String> {
    	private HttpPost getPostRequest(String url){
    		
    		HttpPost method = new HttpPost(url);
	   		 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	   		 EditText text =(EditText) findViewById(R.id.TextUsername);
	   		 nameValuePairs.add(new BasicNameValuePair("username",text.getText().toString() ));
	   		 try {
					method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	   		return method;
    	}
    	private HttpGet getGetRequest(String url){
    		
    		
	   		return  new HttpGet(url);
    	}
		@Override
		protected String doInBackground(String... args) {
			HttpClient httpClient = new DefaultHttpClient();
        	HttpContext localContext = new BasicHttpContext();
        	HttpUriRequest m = null;
        	
        		if (args[1] == "post"){
	        		 
        			m =getPostRequest(args[0]);
	        		
        		}else{
        			
        			m =getGetRequest(args[0]);
        			
        		}
        		
        	HttpResponse response = null;
        	String result = "";
        	
        	try {
				
				
				
				response = httpClient.execute(m , localContext);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	 
        	try {
				return EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return "";
		}
		protected void onPostExecute(String result) {
			final ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
			TextView text = (TextView)findViewById(R.id.textView1);
            text.setText(result);
            progress.setVisibility(View.INVISIBLE);
		}
    
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
