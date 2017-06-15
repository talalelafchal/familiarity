package com.advaitamtech.userapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class CheakIn extends Activity{
	LocationManager locationManager;
	private GoogleMap googleMap;
	Location l1;
	
	private LatLng latLng;
	Geocoder geocoder;
	List<Address> addresses;
	String add;
	long minTime = 10 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
	long minDistance = 10; 
	private static final String CID = "id";
	private static final String ZONEID = "id";
	private static final String STATEID = "state_id";
	private static final String ZONENAME = "name";
	private static final String CLIENTID = "id";
	private static final String USERID = "user_id";
	private static final String SHOP = "name";
	private static final String QUANTITY = "quantity";
	private static final String PRICE = "price";
	static String response = null;
    public final static int GET = 1;
	public final static int POST = 2;
	String OutputData="";
	private SharedPreferences preferences;
	private String url;
	String chkid;
	HashMap<String, String> contact;
	List<String> list1;
	private Spinner spinner1;
	HashMap<String, String> contacty;
	String cid1;
    String cheak_inid;
	String cidcheakin;
	private Button btnSubmit;
	private Button btnCancel;
	private double lat;
	private double lon;
	String zone;
	String client;
	String product;
	String quantity;
	String price;
	
	String zoneid;
	String stateid;
	String zonename;
	
	String clientid1;
	String userid;
	String shop;
	String quantity1;
	String price1;
	 String cid;
	String android_id;
	String client_id;
	ProgressBar progressBar2;
	ArrayList<HashMap<String, String>> contactList;
	ArrayList<HashMap<String, String>> contactList1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cheakin);
		
		
		android_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID); 
		Log.d("android_id", android_id);
		
		preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
		contact = new HashMap<String, String>();
		spinner1 = (Spinner) findViewById(R.id.spinner1);
	    btnCancel=(Button) findViewById(R.id.btnCancel5);
	    btnSubmit=(Button) findViewById(R.id.btnSubmit5);
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
	      boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER); 
	      if (!enabled) {
	          Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	          startActivity(intent);
	      }
	      
	      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  minTime,
			        minDistance, new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					
				}
			});
	      String provider = LocationManager.NETWORK_PROVIDER;
	      l1 = locationManager.getLastKnownLocation(provider);
			lat = l1.getLatitude();
			lon = l1.getLongitude();
			latLng = new LatLng(lat, lon);

	btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				showalert("Cancel", "Do you really want to Cancel!!");
				
			}
		});
    
    btnSubmit.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 if(cheak_inid.equals("")  )
			   {
				   Toast.makeText(getApplicationContext(), "Client not selected", Toast.LENGTH_SHORT).show();
			   }
			   else
			   {
				System.out.println("lllllllllllllllllll"+cheak_inid);
				new ReadJSONFeedTask().execute(Config.URL+"checkin?device_id="+android_id+"&client_id="+cheak_inid+"&lat="+lat+"&lng="+lon);
				
			 }
		}
	});
	
    ConnectivityManager connectivityService = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	NetworkInfo ni = connectivityService.getActiveNetworkInfo();
	if(!(ni != null && ni.isConnected())){
//		Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
	

	}
	
	else{
		new GetContacts().execute();
	}
	
}
	
	
	 public class ReadJSONFeedTask extends AsyncTask<String, Void, String>
	  {    
		  @Override
		  public String doInBackground(String... urls) 
		    {     
		        return readJSONFeed(urls[0]);   
	     
         	  } 
     
			       
		       
		  
		  @Override
		  public void onPostExecute(String args) 
		  {      if (args != null) {
			  
				try {
					 JSONObject contacts = new JSONObject(args.toString());
					 String status1 = contacts.getString("checkin");
					 if (status1 != null) { 
						 
					JSONObject c1 = new JSONObject(status1);
					cid1 = c1.getString(CID);
					System.out.println("ccccccccccccccccccccccc"+cid1); 

					Editor editor = preferences.edit();
					editor.putString("chkid", cid1);
					editor.commit();
					Intent cheakin=new Intent(CheakIn.this, Home.class);
					startActivity(cheakin);
					finish();
					Toast.makeText(getApplicationContext(), "Checked In Succesfully", Toast.LENGTH_SHORT).show();
					 }
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
				else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

		
			  
		  }
	  }
	 public String readJSONFeed(String URL) 
	 {       
		 StringBuilder stringBuilder = new StringBuilder();    
		 HttpClient client = new DefaultHttpClient();      
		 HttpGet httpGet = new HttpGet(URL);       
		 try
		 {          
			 HttpResponse response = client.execute(httpGet);          
			 StatusLine statusLine = response.getStatusLine();        
			 int statusCode = statusLine.getStatusCode();         
			 if (statusCode == 200) 
			 {               
				 HttpEntity entity = response.getEntity();           
				 InputStream content = entity.getContent();           
				 BufferedReader reader = new BufferedReader
						 (                        
								 new InputStreamReader(content));      
				 String line;                
				 while ((line = reader.readLine()) != null) 
				 {                   
					 stringBuilder.append(line);               
					 }           
				 }
			 else
			 {              
				 android.util.Log.e("JSON", "Failed to download file");        
				 }      
			 }
		 catch (ClientProtocolException e)
		 {           
			 e.printStackTrace();       
			 } 
		 catch (IOException e)
		 {           
			 e.printStackTrace();      
			 }
		       
		 return stringBuilder.toString();  
		 }
	 
	 public void showalert(String Title, String Message) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					CheakIn.this);
			alertDialog.setTitle(Title);
			alertDialog.setMessage(Message);
			alertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to invoke YES event
							dialog.cancel();
							Intent cheakin=new Intent(CheakIn.this, Home.class);
							startActivity(cheakin);
							finish();
						}
					});
			alertDialog.show();
		}
	 
	 
		public String makeServiceCall(String url, int method,
				List<NameValuePair> params) {
			try {
				// http client
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpEntity httpEntity = null;
				HttpResponse httpResponse = null;
				
				// Checking http request method type
				if (method == POST) {
					HttpPost httpPost = new HttpPost(url);
					// adding post params
					if (params != null) {
						httpPost.setEntity(new UrlEncodedFormEntity(params));
					}

					httpResponse = httpClient.execute(httpPost);

				} else if (method == GET) {
					// appending params to url
					if (params != null) {
						String paramString = URLEncodedUtils
								.format(params, "utf-8");
						url += "?" + paramString;
					}
					HttpGet httpGet = new HttpGet(url);
					httpResponse = httpClient.execute(httpGet);

				}
				httpEntity = httpResponse.getEntity();
				response = EntityUtils.toString(httpEntity);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return response;

		}
		public String makeServiceCall(String url, int method) {
			return this.makeServiceCall(url, method, null);
		}
		private class GetContacts extends AsyncTask<Void, Void, String> {

		

			private static final int GET = 1;
			
			@Override
			protected String doInBackground(Void... arg0) {
				
			//	 map = new HashMap<String, String>();
	          
				 url=Config.URL+"contacts?device_id="+android_id;
		
				String jsonStr = makeServiceCall(url,this.GET);

				Log.d("Response: ", "> " + jsonStr);

				return jsonStr;
			}
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (result != null) {
					try {
						 JSONObject contacts = new JSONObject(result.toString());
						 String status1 = contacts.getString("contacts");
						 if (status1 != null) { 
						 JSONArray jsonArray = new JSONArray(status1);
						 list1 = new ArrayList<String>();
						 for(int i = 0; i < jsonArray.length(); i++){
							 String s = jsonArray.getString(i);
								 JSONObject c1 = new JSONObject(s);
								 clientid1 = c1.getString(CLIENTID);
								 userid = c1.getString(USERID);
							     shop = c1.getString(SHOP);
							     contact.put(shop, clientid1); 
								 list1.add(shop);
						 }
							ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, list1); //selected item will look like a spinner set from XML
							spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_dropdown_item);
							//spinner1.setBackgroundColor(c);
							spinner1.setAdapter(spinnerArrayAdapter1);
							
							 spinner1.setOnItemSelectedListener(new OnItemSelectedListener() 
							    {
							        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
							        {
							            String selectedItem = parent.getItemAtPosition(position).toString();
							          //  int selectedItem=parent.getId();
							            System.out.println("ooooooooooooooooooooooooooooooo"+selectedItem);
							           
							            cheak_inid=contact.get(selectedItem);
							            System.out.println("dddddddddddddddddddddddddddddd"+cheak_inid);
							        } // to close the onItemSelected
							        public void onNothingSelected(AdapterView<?> parent) 
							        {

							        }           
							    });
						 }
					} catch (JSONException e) {
						e.printStackTrace();
					}	
				}
					else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}

			}
					  
		     
		} 
	  
}
