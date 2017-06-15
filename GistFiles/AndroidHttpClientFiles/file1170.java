package com.demo.php.listview;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;
/*
 * @author Firzan Ghulam
 */
public class MainActivity extends Activity {
  JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb = null; 
	ArrayList<String> al = new ArrayList<String>();
	ArrayList<String> al1 = new ArrayList<String>();
	ArrayList<String> al2 = new ArrayList<String>();
	String targetmonth;
	String targetyear;
	String targetamount;
//	int responseCode;
	//int listItemCount=0;
	ListView listview ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setTheme(Color.WHITE); 
		setTitleColor(Color.rgb(0x74, 0, 0x37));
		setTitle("DEMO PHP");
		requestWindowFeature(Window.FEATURE_RIGHT_ICON); 
		setContentView(R.layout.main);
		listview = (ListView) findViewById(R.id.listView1);
		new LoadData().execute();
			 
	} 
	
	 
	
	private class LoadData extends AsyncTask<Void, Void, Void> { 
		private ProgressDialog progressDialog;  
		@Override
		// can use UI thread here
		protected void onPreExecute() {
		this.progressDialog = ProgressDialog.show(MainActivity.this, ""," Loading...");  
		}
		@Override
		protected void onPostExecute(final Void unused) {  
			try{
	 				    
	 				 listview.setAdapter(new DataAdapter(MainActivity.this,al.toArray(new String[al.size()]),al1.toArray(new String[al1.size()]),al2.toArray(new String[al2.size()])));
				     this.progressDialog.dismiss();
				}
	 			catch(Exception e){ 
	 				Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
	 			}  
		}
		@Override
		protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub  
			// HTTP post 
					try { 
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						HttpClient httpclient = new DefaultHttpClient(); 
						try{
						HttpPost httppost = new HttpPost("http://10.0.2.2/android/test.php");
					
						StringEntity se = new StringEntity("envelope",HTTP.UTF_8);
						httppost.setEntity(se); 
						HttpParams httpParameters = new BasicHttpParams();
						// Set the timeout in milliseconds until a connection is established.
						int timeoutConnection = 3000;
						HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
						// Set the default socket timeout (SO_TIMEOUT) 
						// in milliseconds which is the timeout for waiting for data.
						int timeoutSocket = 3000;
						HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
						
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						is = entity.getContent(); 
						}
						catch(Exception e){
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
						}
						//buffered reader
						try{
						BufferedReader reader = new BufferedReader(new InputStreamReader(
								is, "iso-8859-1"), 80);
						sb = new StringBuilder();
						sb.append(reader.readLine() + "\n");
						String line = "0";
						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
						}
						is.close();
						result = sb.toString();
						}
						catch(Exception e){
							Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
						}
						try{
						jArray = new JSONArray(result);
						JSONObject json_data = null;
						for (int i = 0; i < jArray.length(); i++) {
							json_data = jArray.getJSONObject(i); 
							targetamount=json_data.getString("targetamount");
							targetmonth=json_data.getString("targetmonth");
							targetyear = json_data.getString("targetyear"); 
							al.add(targetmonth); 
							al1.add(targetyear);
							al2.add(targetamount);  
							 
							//listItemCount=al2.size();
						}
					}
					catch(JSONException e){
						Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					}	
					} catch (ParseException e) {
					//	Log.e("log_tag", "Error in http connection" + e.toString());
						Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					}  
					 catch (Exception e) {
							//	Log.e("log_tag", "Error in http connection" + e.toString());
								Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
							}  		 
			return null; 
		}
	} 
	
	 
}
