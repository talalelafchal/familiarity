package com.example.aslan.webservicesusinghttp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.StrictMode;




public class MainActivity extends Activity {
    EditText username;
    EditText part;
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//is to override NetworkOnMainThreadException
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.name);//redundant code
        part = (EditText)findViewById(R.id.part_nr);//redundant code
        Button savetext = (Button)findViewById(R.id.save);
        content = (TextView)findViewById(R.id.content);

        savetext.setOnClickListener(new Button.OnClickListener() {
            String toggle = "R01SW01OFF";//initial state is assumed of 
            @Override
            public void onClick(View v) {
                String name = username.getText().toString();//not used
                String part_nr = part.getText().toString();//not used
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://192.168.0.100/cgi-bin/hello.py");//this is the raspberry pi address assigned by router,you may need to paste your own address here
                if(toggle.equals("R01SW01OFF")){toggle = "R01SW01ON";}//toggle value
                else {toggle = "R01SW01OFF";}
                String state = toggle;
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(3);
                pairs.add(new BasicNameValuePair("Name", name));//not used
                pairs.add(new BasicNameValuePair("part_nr", part_nr));//not used
                pairs.add(new BasicNameValuePair("state", state));//toggle value
                try {
                    post.setEntity(new UrlEncodedFormEntity(pairs));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    String ServerResponse = "";
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    ServerResponse = client.execute(post, responseHandler);
                    content.setText(ServerResponse);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();

                }
                catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }
            }


        });


    }
    }
