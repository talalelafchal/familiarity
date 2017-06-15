package com.example.inettest;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.view.View.*;

public class MainActivity extends Activity {

    private final String EMPTY = "No text loaded, press textsection!";
    private final String URL = "https://www.random.org/strings/?num=10&len=15&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(final WatchViewStub stub) {
                final TextView mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("URL: "+ URL.substring(8,32));

                final TextView editTextView = (TextView) stub.findViewById(R.id.editText);
                editTextView.setText(EMPTY);

                editTextView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        editTextView.setText(readUrl(URL));
                    }
                });
            }
        });
    }

    private String readUrl(String url){
        URLConnection feedUrl = null;
        String value ="";
        try {
            feedUrl = new URL(url).openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "ERROR URL";
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR CONNECTION";
        }
        try {
            InputStream in = feedUrl.getInputStream();
            value = convertStreamToString(in);
        }catch(Exception e){
            e.printStackTrace();
            return "ERROR READING";
        }
        return value;
    }

    private String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR CONVERT";
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
