/***
	src/com.launchhouse.demo.zxing/ZXingDemo.java
	Copyright (c) 2008-2010 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.launchhouse.demo.zxing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
// I edited this line from "com.commonsware.android.zxing.R" to "com.launchhouse.demo.zxing.R"
import com.launchhouse.demo.zxing.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ZXingDemo extends Activity {
	TextView format=null;
	TextView contents=null;
	// I added this
	TextView apiReturn=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		format=(TextView)findViewById(R.id.format);
		
		contents=(TextView)findViewById(R.id.contents);
		// I added this line
		apiReturn=(TextView)findViewById(R.id.apiReturn);
	}
	
	public void doScan(View v) {
		(new IntentIntegrator(this)).initiateScan();
	}
	
	// all these lines were added to the commonsware start package START
	public void onClick(View v) {
		
	}
	
	//*
	public void contactServer(View v) {
		Log.d("TAG", "To The LogCat!");
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("https://www.rustbeltrebellion.com/testpostscript.php");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        
	        nameValuePairs.add(new BasicNameValuePair("stringdata", contents.getText().toString()));
	        
	        //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        int responseCode = response.getStatusLine().getStatusCode();
	        switch(responseCode) {
	        case 200:
	        HttpEntity entity = response.getEntity();
	            if(entity != null) {
	            	apiReturn.setText(EntityUtils.toString(entity));
	            }
	            break;
	        }
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
			Log.d("TAG", "To The LogCat! CPE");
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
			Log.d("TAG", "To The LogCat! IOE");
	    }

	}
	//*/
	//all these lines were added to the commonsware start package END
	
	public void onActivityResult(int request, int result, Intent i) {
		IntentResult scan=IntentIntegrator.parseActivityResult(request, result, i);
		
		if (scan!=null) {
			format.setText(scan.getFormatName());
			contents.setText(scan.getContents());
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
		state.putString("format", format.getText().toString());
		state.putString("contents", contents.getText().toString());
		// I added this line
		state.putString("apiReturn", apiReturn.getText().toString());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle state) {
		format.setText(state.getString("format"));
		contents.setText(state.getString("contents"));
		// I added this line
		apiReturn.setText(state.getString("apiReturn"));
	}
}
