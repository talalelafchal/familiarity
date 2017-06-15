

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class WebServiceHandler {
    
	private String responseString = "";
	
	//get json data
    public String getWebServiceData(String webServiceURL, ArrayList<NameValuePair> parametersList) {
        
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        
        HttpPost httppost = new HttpPost(webServiceURL);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(parametersList));
 
            // Execute HTTP Post Request and get response
            HttpResponse response = httpclient.execute(httppost);
            responseString = inputStreamToString(response.getEntity().getContent()).toString();
            for(int i=0;i<parametersList.size();i++) {
                Log.i("PARAM:("+(i+1)+")",parametersList.get(i).getName()+": "+ parametersList.get(i).getValue());
            }
            Log.i("JSONResponse: ", responseString);

            
        } catch (ClientProtocolException e) {
         e.printStackTrace();
        } catch (IOException e) {
         e.printStackTrace();
        }
        return responseString;
    } 

    public String getWebServiceDataByGET(String webServiceURL) {

        BufferedReader in = null;
        String data = null;
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet();
        try {

            URI website = new URI(webServiceURL);
            httpGet.setURI(website);

            // Execute HTTP Get and get response
            HttpResponse response = httpclient.execute(httpGet);
            responseString = inputStreamToString(response.getEntity().getContent()).toString();
            Log.i("JSONResponse: ", responseString);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseString;
    }

    // convert stream to string
    private StringBuilder inputStreamToString(InputStream inputStream)
    {
        
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
        
        // Read response until the end
        try 
        {
             while ((line = rd.readLine()) != null)
             { 
               total.append(line); 
             }
             
        } catch (IOException e) {
         e.printStackTrace();
        }

        return total;
       }
}
