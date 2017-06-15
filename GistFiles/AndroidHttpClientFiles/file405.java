package com.tukangjava.tutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class DatabaseAndroid extends Activity 
	implements View.OnClickListener  {

	String urlPost = "http://203.247.166.88:8000/NoteWS/notes";
	TextView txtId;
	TextView txtContent;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initializeGUI();
    }
    
    private void initializeGUI() {
    	Button btnPost =(Button)findViewById(R.id.btnPost);
		btnPost.setOnClickListener(this);
		
		Button btnView =(Button)findViewById(R.id.btnView);
		btnView.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnPost)
			postData();
		startActivity(new Intent(this, ViewDatabase.class));
	}
	
    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlPost);

        txtId = (TextView) findViewById(R.id.txtId);
        txtContent = (TextView) findViewById(R.id.txtContent);
        try {
            // Add the data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("noteId", txtId.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("content", txtContent.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("createddate", new Date().getTime() + ""));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            
        } catch (ClientProtocolException e) {
            Log.w("ERROR", e.getMessage());
        } catch (IOException e) {
            Log.w("ERROR", e.getMessage());
        }
    } 
}