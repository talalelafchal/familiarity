/*
 
JSONParser, guya00@gmail.com
 
Usecase:

## JSON from File in assets directory
AssetManager assetManager = getResources().getAssets();
String jsonFileName       = "test.json";
JSONObject jsonObj        = JSONParser.getJSONObject(jsonFileName,
                                                     JSONParser.FL_FILE,
                                                     assetManager);
## JSON from URL
String jsonURL            = "http://www.example.com/json";
JSONObject jsonObj        = JSONParser.getJSONObject(jsonFileName,
                                                     JSONParser.FL_URL,
                                                     null);

License: BSD license
 
Copyright (c) 2013, ygpark
 
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;


public class JSONParser
{
  public static final String TAG      = "JSONParser";
  public static final boolean DEBUG   = true;
    
  public static final String FL_FILE  = "file";
  public static final String FL_URL   = "url";

    
  public static JSONObject getJSONObject 
      (final String src, final String option, final AssetManager assetManager) 
              throws IOException 
  {
      
      JSONObject jObj         = null;
      String     jsonString   = "";

      if (option == FL_FILE) 
      {
          jsonString = getJSONStringFromFile(src, assetManager);
      } 
      else if (option == FL_URL) 
      {
          jsonString = getJSONStringFromUrl(src);            
      }

      try 
      {
          jObj = new JSONObject(jsonString);
      } 
      catch (JSONException e) 
      {
          if (DEBUG) Log.e(TAG, "Error parsing data " + e.toString());
      }
      
      return jObj;
  }// getJSONObject


  protected static String getJSONStringFromFile(final String filename, final AssetManager assetManager) 
  {
      final int BUFFER_SIZE = 1024 * 1024;
      
      AssetInputStream ais = null;
      try {
          ais = (AssetInputStream)assetManager.open(filename);
      } catch (IOException e) {
          e.printStackTrace();
      }
      
      BufferedReader  br   = new BufferedReader(new InputStreamReader(ais));
      StringBuilder   sb   = new StringBuilder();
      char readBuf[]       = new char[BUFFER_SIZE];

      int resultSize       = 0;
      
      try 
      {
          while((resultSize = br.read(readBuf)) != -1) 
          {
              if (resultSize == BUFFER_SIZE)
              {
                  sb.append(readBuf);
              }
              else
              {
                  for (int i=0; i<resultSize; i++)
                  {
                      sb.append(readBuf[i]);
                  }
              }
          }
      } 
      catch (IOException e) 
      {
          e.printStackTrace();
      }

      return sb.toString();
  }// getJSONStringFromFile


  protected static String getJSONStringFromUrl(final String url) 
  {
      InputStream is          = null;
      String      jsonString  = null;
      
      final String encoding   = "iso-8859-1";
      

      try 
      {
          DefaultHttpClient httpClient = new DefaultHttpClient();
          HttpPost          httpPost   = new HttpPost(url);

          HttpResponse httpResponse    = httpClient.execute(httpPost);
          HttpEntity   httpEntity      = httpResponse.getEntity();
          
          is  = httpEntity.getContent();
      } 
      catch (UnsupportedEncodingException e) 
      {
          e.printStackTrace();
      } 
      catch (ClientProtocolException e) 
      {
          e.printStackTrace();
      } 
      catch (IOException e) 
      {
          e.printStackTrace();
      }

      try 
      {
          BufferedReader  reader = new BufferedReader(new InputStreamReader(is, encoding), 8);
          StringBuilder   sb     = new StringBuilder();
          String          line   = null;
          
          while ((line = reader.readLine()) != null) 
          {
              sb.append(line + "\n");
          }
          
          is.close();

          jsonString = sb.toString();
      } 
      catch (Exception e) 
      {
          if (DEBUG) Log.e(TAG, "Error converting result " + e.toString());
      }
     
      return jsonString;
  }// getJSONStringFromUrl
}
