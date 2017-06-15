package com.mannir.ebahn2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Interactives extends ActionBarActivity {
	TextView roomno, message;
	Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interactives);
		
		roomno = (TextView)findViewById(R.id.roomno);
		message = (TextView)findViewById(R.id.message);
		
		Button sendbtn = (Button) findViewById(R.id.sendbtn);
		
		sendbtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				String room = roomno.getText().toString();
				String msg = message.getText().toString();
				String ip = "";
				
				try {
				      for (Enumeration<NetworkInterface> en = NetworkInterface
				          .getNetworkInterfaces(); en.hasMoreElements();) {
				        NetworkInterface intf = en.nextElement();
				        for (Enumeration<InetAddress> enumIpAddr = intf
				            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
				          InetAddress inetAddress = enumIpAddr.nextElement();
				          if (!inetAddress.isLoopbackAddress()) {
				            ip = inetAddress.getHostAddress().toString();
				          }
				        }
				      }
				    } catch (SocketException ex) {
				      ex.printStackTrace();
				    }

				
				Log.d("eBahnIPTV", ip);
				Log.d("eBahnIPTV", room);
				Log.d("eBahnIPTV", msg);
				
				new HttpAsyncTask().execute("http://192.168.0.3:8080/eBahnIPTVServer/json?ip=192.168.0.5&video=test.mp4");
				
				//Toast.makeText(ctx, "hello", Toast.LENGTH_LONG ).show();
				//Toast.makeText(ctx, "hi", Toast.LENGTH_LONG ).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.interactives, menu);
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
	
	
	
	
	
	
	public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
 
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
	
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            
            Gson gson = new Gson();
            
            //String rt = result.substring(4);

            
            //tts = new TextToSpeech(ctx, null);
            //tts.speak(user2.getUsername()+"Thank you for using eBahn IPTV", TextToSpeech.QUEUE_FLUSH, null);
            
            //ttobj.speak("Thank you for using Jibril Mobile NFC", TextToSpeech.QUEUE_FLUSH, null);
       }
    }
}
