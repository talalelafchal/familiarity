package com.matto1990.policeuk.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateUtils;
import android.util.Log;

import com.github.droidfu.cachefu.AbstractCache;
import com.github.droidfu.http.BetterHttp;
import com.github.droidfu.http.BetterHttpRequest;
import com.github.droidfu.http.BetterHttpResponse;

public class Data {
  private static final String USERNAME = "";
  private static final String PASSWORD = "";
  
  private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;
  private static final String HOST = "policeapi2.rkh.co.uk";
  private static final String URL_BASE = "http://policeapi2.rkh.co.uk/api/";
  private static final String TAG = "policeuk";
  private Context context;
  
  public Data(Context context) {
    this.context = context;
  }
  
  public ArrayList<ForceList> getForces() {
    try {
      String json = makeRequest(URL_BASE + "forces");
      
      JSONArray forcesArray = (JSONArray) new JSONTokener(json).nextValue();
      
      JSONObject force;
      ArrayList<ForceList> forceList = new ArrayList<ForceList>();
      
      for (int i = 0; i < forcesArray.length(); i++) {
        force = (JSONObject) forcesArray.get(i);
        forceList.add(new ForceList(force.getString("id"), force.getString("name")));
      }
      
      Log.i(TAG, "" + forceList);
      
      return forceList;
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  public Force getForce(String forceId) {
    try {
      String json = makeRequest(URL_BASE + "forces/" + forceId);
      
      JSONObject forceData = (JSONObject) new JSONTokener(json).nextValue();
      Force force = new Force();
      
      force.forceId = forceData.getString("id");
      force.name = forceData.getString("name");
      force.description = forceData.getString("description");
      force.url = forceData.getString("url");
      force.telephone = forceData.getString("telephone");
      
      return force;
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  public ArrayList<NeighbourhoodList> getNeighbourhoods(String forceId) {
    try {
      String json = makeRequest(URL_BASE + forceId + "/neighbourhoods");
      
      JSONArray neighbourhoodArray = (JSONArray) new JSONTokener(json).nextValue();
      
      JSONObject neighbourhood;
      ArrayList<NeighbourhoodList> neighbourhoodList = new ArrayList<NeighbourhoodList>();
      
      for (int i = 0; i < neighbourhoodArray.length(); i++) {
        neighbourhood = (JSONObject) neighbourhoodArray.get(i);
        neighbourhoodList.add(new NeighbourhoodList(neighbourhood.getString("id"), neighbourhood.getString("name")));
      }
      
      return neighbourhoodList;
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  public Neighbourhood getNeighbourhood(String forceId, String neighbourhoodId) {
    try {
      String json = makeRequest(URL_BASE + forceId + "/" + neighbourhoodId);
      
      JSONObject neighbourhoodData = (JSONObject) new JSONTokener(json).nextValue();
      Neighbourhood neighbourhood = new Neighbourhood();
      
      neighbourhood.neighbourhoodId = neighbourhoodData.optString("id");
      neighbourhood.name = neighbourhoodData.optString("name", "Untitled");
      neighbourhood.description = neighbourhoodData.optString("description", "null");
      neighbourhood.url = neighbourhoodData.optString("url_force", "null");
      neighbourhood.population = neighbourhoodData.optString("population", "null");
      
      JSONObject contactDetails = neighbourhoodData.getJSONObject("contact_details");
      neighbourhood.email = contactDetails.optString("email", "null");
      neighbourhood.telephone = contactDetails.optString("telephone", "null");
      
      JSONObject locationDetails = neighbourhoodData.getJSONObject("centre");
      neighbourhood.lat = locationDetails.optString("latitude", "null");
      neighbourhood.lon = locationDetails.optString("longitude", "null");
      
      neighbourhood.force = getForce(forceId);
      
      return neighbourhood;
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  public ArrayList<Crimes> getCrime(String forceId, String neighbourhoodId) {
    try {
      String json = makeRequest(URL_BASE + forceId + "/" + neighbourhoodId + "/crime");
      
      JSONObject crimeData = ((JSONObject) new JSONTokener(json).nextValue()).getJSONObject("crimes");
      Iterator<String> crimesIterator = crimeData.keys();
      String crimeMonth;
      JSONObject crimesData, crime;
      Crimes crimes;
      ArrayList<Crimes> crimeList = new ArrayList<Crimes>();
      
      while (crimesIterator.hasNext()) {
        crimeMonth = crimesIterator.next();
        Log.i(TAG, crimeMonth);
        crimes = new Crimes(crimeMonth);
        
        crimesData = crimeData.getJSONObject(crimeMonth);
        
        crime = crimesData.getJSONObject(Crimes.ALL);
        crimes.addCategory(Crimes.ALL, crime.getDouble("crime_rate"), crime.getString("crime_level"), crime.getInt("total_crimes"));
        
        crime = crimesData.getJSONObject(Crimes.ANTI_SOCIAL);
        crimes.addCategory(Crimes.ANTI_SOCIAL, crime.getDouble("crime_rate"), crime.getString("crime_level"), crime.getInt("total_crimes"));
        
        crime = crimesData.getJSONObject(Crimes.BURGLARY);
        crimes.addCategory(Crimes.BURGLARY, crime.getDouble("crime_rate"), crime.getString("crime_level"), crime.getInt("total_crimes"));
        
        crime = crimesData.getJSONObject(Crimes.OTHER);
        crimes.addCategory(Crimes.OTHER, crime.getDouble("crime_rate"), crime.getString("crime_level"), crime.getInt("total_crimes"));
        
        crime = crimesData.getJSONObject(Crimes.ROBBERY);
        crimes.addCategory(Crimes.ROBBERY, crime.getDouble("crime_rate"), crime.getString("crime_level"), crime.getInt("total_crimes"));
        
        crime = crimesData.getJSONObject(Crimes.VEHICLE);
        crimes.addCategory(Crimes.VEHICLE, crime.getDouble("crime_rate"), crime.getString("crime_level"), crime.getInt("total_crimes"));
        
        crime = crimesData.getJSONObject(Crimes.VIOLENT);
        crimes.addCategory(Crimes.VIOLENT, crime.getDouble("crime_rate"), crime.getString("crime_level"), crime.getInt("total_crimes"));
        
        crimeList.add(crimes);
      }
      
      return crimeList;
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  public Neighbourhood getLocation(double lat, double lon) {
    try {
      String json = makeRequest(URL_BASE + "locate-neighbourhood?q=" + lat + "," + lon);
      
      JSONObject locationData = (JSONObject) new JSONTokener(json).nextValue();
      
      return getNeighbourhood(locationData.getString("force"), locationData.getString("neighbourhood"));
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }
  
  private String makeRequest(String url) throws Exception {
    // Setup the http client
    setHttpClient();
    
    // Get the data
    final BetterHttpRequest request = BetterHttp.get(url, true);
    BetterHttpResponse response = request.send();
    
    // Check the http status code is correct
    final int status = response.getStatusCode();
    if (status != HttpStatus.SC_OK) {
      throw new Exception("Unexpected server response " + response.getStatusCode() + " for " + request.getRequestUrl());
    }
    
    // Get the response as an BufferedReader
    final InputStream input = response.getResponseBody();
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
    
    String line;
    String json = "";
    
    // Loop though each line
    while ((line = bufferedReader.readLine()) != null) {
      json += line;
    }
    
    return json;
  }
  
  public void setHttpClient() {
    BetterHttp.setHttpClient((AbstractHttpClient) getHttpClient(context));
    BetterHttp.enableResponseCache(context, 32, 60, 2, AbstractCache.DISK_CACHE_INTERNAL);
  }
  
  /**
   * Generate and return a {@link HttpClient} configured for general use,
   * including setting an application-specific user-agent string.
   */
  public static HttpClient getHttpClient(Context context) {
    final HttpParams params = new BasicHttpParams();
    
    // Use generous timeouts for slow mobile networks
    HttpConnectionParams.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
    HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);
    
    HttpConnectionParams.setSocketBufferSize(params, 8192);
    HttpProtocolParams.setUserAgent(params, buildUserAgent(context));
    
    final DefaultHttpClient client = new DefaultHttpClient(params);
    
    Credentials defaultcreds = new UsernamePasswordCredentials(USERNAME, PASSWORD);
    CredentialsProvider credProvider = new BasicCredentialsProvider();
    credProvider.setCredentials(new AuthScope(HOST, 80, AuthScope.ANY_REALM), defaultcreds);
    client.setCredentialsProvider(credProvider);
    
    return client;
  }
  
  /**
   * Build and return a user-agent string that can identify this application to
   * remote servers. Contains the package name and version code.
   */
  private static String buildUserAgent(Context context) {
    try {
      final PackageManager manager = context.getPackageManager();
      final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
      return info.packageName + "/" + info.versionName + " (" + info.versionCode + ")";
    }
    catch (NameNotFoundException e) {
      return null;
    }
  }
}