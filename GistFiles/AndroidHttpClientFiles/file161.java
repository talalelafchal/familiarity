package com.example.jsondemo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity implements OnClickListener {
 
	static String imgPath = "/mnt/sdcard/BusinessCard/image.jpg";
    TextView tvIsConnected;
    EditText etName,etCountry,etTwitter;
    Button btnPost;
    static TextView tvp;
    String disp= "Zooooooooooo";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        tvp = (TextView) findViewById(R.id.tv);
        btnPost = (Button) findViewById(R.id.btnPost);
 
        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }
 
        // add click listener to Button "POST"
        btnPost.setOnClickListener(this);
 
    }
 
    public static String POST(String url)
    {
    	Log.i("MINION", "inside POST()");
    	
    	File imageFile = new File(imgPath);
		FileInputStream fis = null;
		JSONObject jsonObj = null;
		String imgString=null;
		int responseCode=0;
		String responseText = null;
		
		try
		{
			fis = new FileInputStream(imageFile);
		}catch (FileNotFoundException e){
			Log.i("Minions","File Not Found at: " + imgPath);
			e.printStackTrace();
		}
		
		Bitmap bm = BitmapFactory.decodeStream(fis);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 100, baos);
		byte []imgByte = baos.toByteArray();
		
		imgString = Base64.encodeToString(imgByte, Base64.DEFAULT);
		//System.out.println("Base64 String image: " + imgString);
		
		try {
			jsonObj = new JSONObject("{\"image\":\" + imgString + \"}");
			Log.i("Minion","imgString -> JSONObject SUCCESS");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("Minion","Error converting imgString -> JSONObject");
			e.printStackTrace();
		}
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		HttpPost post = new HttpPost(url);
		
		try {
			Log.i("Minion","Inside TRY Block");
			StringEntity se = new StringEntity(jsonObj.toString());
			post.setHeader("Content-type","application/json");
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
			
			Log.i("Minion","Headers Set");
			post.setEntity(se);
			Log.i("Minion","String Entity Posted");
			response = client.execute(post);
			Log.i("Minion","File Uploaded");
			
			responseCode = response.getStatusLine().getStatusCode();
			Log.i("Minion","Server Response Code: " + responseCode);
			
			HttpEntity entity = response.getEntity();

            responseText = EntityUtils.toString(entity);
            Log.i("Minion","Server Response: " + responseText);

			if (response!=null)
			{
				InputStream in = response.getEntity().getContent();
				
				
			}
			

			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.i("Minion","JSON Object coud not be converted to String Entity.. Aww snap!");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.i("Minion","Couldnot send post request.. servers bad!!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i("Minion","File Not uploaded");
			e.printStackTrace();
		}
		return (responseCode + "");
    }
 
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;    
    }
    @Override
    public void onClick(View view) {
 
        switch(view.getId()){
            case R.id.btnPost:
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://businesscardreader.cloudapp.net/api/values");
            break;
        }
 
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
     
            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
       }
    }
 
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
    	Log.i("MINION", "Inside convertINputStreamToString");
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }   
}
