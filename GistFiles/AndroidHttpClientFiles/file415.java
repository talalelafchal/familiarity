import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

@SuppressWarnings("unused")
public class StatsMixAPI{
	
	private String PROFILE_ID, METRIC_ID, API_KEY;
	
	public StatsMixAPI(final String profile_id, final String metric_id, final String api_key){		
		PROFILE_ID = profile_id;
		METRIC_ID  = metric_id;
		API_KEY    = api_key;
	}
	
	 public void postData(String VALUE){
		 
	    HttpClient httpclient = new DefaultHttpClient();  
	    HttpPost httppost = new HttpPost("http://www.statsmix.com/api/v1/stats");  
	   
	     try {  

	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	         
		         nameValuePairs.add(new BasicNameValuePair("value", VALUE));  
		         nameValuePairs.add(new BasicNameValuePair("profile_id", PROFILE_ID));
		         nameValuePairs.add(new BasicNameValuePair("metric_id", METRIC_ID));
		         nameValuePairs.add(new BasicNameValuePair("generated_at", getDateTime()));
		         nameValuePairs.add(new BasicNameValuePair("api_key", API_KEY));
		         
	         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	         HttpResponse response = httpclient.execute(httppost);

	      } 
	     catch (ClientProtocolException e) {}
	     catch (IOException e) {}  
	}  
	 
	public String getDateTime() {
	    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date date = new Date();
	    return dateFormat.format(date);
	}
}
