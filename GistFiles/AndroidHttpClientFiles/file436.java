package com.example.demo4;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void btnClick(View v) {
    	try {
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost("http://localhost:9080/gleeful.php");
    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    	
    	int i=0;
    	
    	builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    	//builder.addPart("pic", new FileBody(img));
    	builder.addTextBody("username", "nitin");
    	
    	post.setEntity(builder.build());
    	
    	HttpResponse response = client.execute(post);
    	HttpEntity entity = response.getEntity();
    	
    	entity.consumeContent();
    	client.getConnectionManager().shutdown();
    	}
    	catch (Exception ex) {
    	TextView tv = (TextView) findViewById(R.id.textView1);
    	tv.setText(ex.getMessage());
    	}
    }
}
