package com.example.townvoice;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new testPost());
	}
	
	class testPost implements OnClickListener {
		@Override
		public void onClick(View v) {
			String url="http://localhost:3000";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			 
			ArrayList <NameValuePair> params = new ArrayList <NameValuePair>();
			params.add( new BasicNameValuePair("content", "test"));
			 
			HttpResponse res = null;
			 
			try {
			    post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			    res = httpClient.execute(post);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}
}
