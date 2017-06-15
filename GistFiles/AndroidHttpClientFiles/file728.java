package android.example;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



public class SimpleWebService2Activity extends Activity {
    /** Called when the activity is first created. */
	
  
    TextView tv;
    String text;
   
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	   
	    tv = (TextView)findViewById(R.id.result);
	    text = "";
	   
	    postData();
	}

	public void postData(){  
	   
	    
	                // Create a new HttpClient and Post Header  
					HttpParams params = new BasicHttpParams();
					params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	
	                HttpClient httpclient = new DefaultHttpClient(params);
	                HttpPost httppost = new HttpPost("http://testmobile.dworks.asia/service/hello.php"); 
	                /* notes: sesuaikan alamat url yang menjadi nilai variable input pembuatan httpost
	                 * dengan lokasi file php yang telah dibuat sebelumnya, yg akan mengolah data posting
	                 * yang dikirimkan 
	                */
	
	            try {  
	                    // Add your data  
	                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);  
	                    nameValuePairs.add(new BasicNameValuePair("name", "Adri"));    
	                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	   
	                    // Execute HTTP Post Request  
	                    HttpResponse response = httpclient.execute(httppost);
	                   
	                    InputStream is = response.getEntity().getContent();
	                    BufferedInputStream bis = new BufferedInputStream(is);
	                    ByteArrayBuffer baf = new ByteArrayBuffer(20);
	
	                     int current = 0;  
	                     while((current = bis.read()) != -1){  
	                            baf.append((byte)current);  
	                     }  
	                       
	                    /* Convert the Bytes read to a String. */  
	                    text = new String(baf.toByteArray());
	                    tv.setText(text);
	   
	            } catch (ClientProtocolException e) {  
	                    // TODO Auto-generated catch block  
	            } catch (IOException e) {  
	                    // TODO Auto-generated catch block  
	            }  
	    }
    
    
    
    
}