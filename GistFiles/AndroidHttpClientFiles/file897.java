package com.example.jsondemo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    	
        InputStream inputStream = null;
        String result = "";
        byte[] buffer;
		int bufferSize = 1 * 1024 * 1024;
		Bitmap bm;
		String imagePath = "/mnt/sdcard/BusinessCard/image.jpg";
		String encodedImage = null;
		File file = new File(imagePath);
		String temp = "Vipul Sharma";
		int responseCode=0;
		String responseMessage = "";
        try {
        	
        	Log.i("MINION", "inside try block");
 
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            	
    		
    		//Check if file exists
    		if (!file.isFile()) {        
    	           Log.e("uploadFile", "Source File not exist :"+imagePath);
    	           }
    		
    		else
    		{
    			Log.i("MINION", "image file found");
    			FileInputStream fileInputStream = new FileInputStream(file);
				buffer = new byte[bufferSize];
				bm = BitmapFactory.decodeStream(fileInputStream);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				buffer = baos.toByteArray();	//Image->Byte Array
				
				
				//Byte Array to base64 image string
				encodedImage = Base64.encodeToString(buffer, Base64.DEFAULT);
				Log.i("MINION", "image conversted to Base 64 string");
				
				//Converting encodedImage to String Entity
				//StringEntity se = new StringEntity(encodedImage);
				StringEntity se = new StringEntity(temp);
				Log.i("MINION", "encodedImage to StringEntity");
				
				//httpPost Entity
				httpPost.setEntity(se);
	            // 7. Set some headers to inform server about the type of the content   
	            httpPost.setHeader("Accept", "application/json");
	            httpPost.setHeader("Content-type", "application/json");
	 
	            // 8. Execute POST request to the given URL
	            HttpResponse httpResponse = httpclient.execute(httpPost);
	            Log.i("MINION", "Post request successful");
	            
	            // 9. receive response as inputStream
	            
	            
	            
	            
	            //inputStream = httpResponse.getEntity().getContent();
	            
	            
	            HttpEntity entity = httpResponse.getEntity();

	            String responseText = EntityUtils.toString(entity);
	            
	            responseCode = httpResponse.getStatusLine().getStatusCode();
	            System.out.println("Response Code: " + responseCode);
	            
	            //responseMessage = EntityUtils.toString(httpResponse.getEntity());
	            System.out.println("Response Message: " + responseText);
	            
	            
	           /* // 10. convert inputstream to string
	            if(inputStream != null)
	            {
	            	Log.i("MINION", "Converting Response to String");
	            	result = convertInputStreamToString(inputStream);
	            	Log.i("MINION", "Response to String Sucess");
	            }
	            else
	            	result = "Did not work!";
	            
	            */
    		}
    		
 
        } catch (Exception e) {
            Log.d("InputStream", e.toString());
        }
 
        // 11. return result
        result = "Response Code: " + responseCode + "Response Message: " + responseMessage + result;
        System.out.println("Result: " + result);
        return result;
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
                new HttpAsyncTask().execute("http://businesscardreader.cloudapp.net");
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