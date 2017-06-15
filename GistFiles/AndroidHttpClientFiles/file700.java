package aplication.start.main;



import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

//import com.google.gson.Gson;

public class Connection {

	String URL = "http://iwarn-staging.herokuapp.com/";
	String result = "";
	String deviceId = "xxxxx" ;
	final String tag = "Your Logcat tag: ";

	/** Called when the activity is first created. */
    
    public void onCreate(Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
      //  setContentView(R.layout.main);

/*        final EditText txtSearch = (EditText)findViewById(R.id.editText2);
        txtSearch.setOnClickListener(new EditText.OnClickListener(){
        	public void onClick(View v){txtSearch.setText("");}
    	});

        final Button btnSearch = (Button)findViewById(R.id.editText2);
        btnSearch.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				String query = txtSearch.getText().toString();
				callWebService(query);

			}
        }); */

    } // end onCreate()

    public void callWebService(String q){
    	HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL + q);
		request.addHeader("deviceId", deviceId);
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			result = httpclient.execute(request, handler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		Log.i(tag, result);
    } // end callWebService()
 
    public void callWebServiceForGetData(String id){
    	HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL +"events/"+id+".json");
		request.addHeader("deviceId", deviceId);
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			result = httpclient.execute(request, handler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		Log.i(tag, result);
    } // end callWebService()
    
    public void callWebServiceForSendData(String jsonData){
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost sender= new HttpPost(URL+"events.json");
    	
		//HttpGet request = new HttpGet(URL + q);
		
		HttpParams p=new BasicHttpParams();
		p.setParameter("Method:", "POST");
		p.setParameter("Accept:"," application/json");
		p.setParameter("Content-Type:", "application/json");
		 //Body: {"event": {"description": null, "latitude": 70.1, "longitude": 11.0, "state": "registered", "type": "simple"}}
		p.setParameter("Body:",jsonData);
		
		sender.setParams(p);
		
		sender.addHeader("deviceId", deviceId);
		ResponseHandler<String> handler = new BasicResponseHandler();
		
		
		try {
			//result = httpclient.execute(sender, handler);
			HttpResponse responset = httpclient.execute(sender);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result=e.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
			result=e.toString();
		}
		httpclient.getConnectionManager().shutdown();
		Log.i(tag, result);
    } // end callWebService()

}