//JSON EXAMPLE
{
    "contacts": [
        {
                "id": "c200",
                "name": "Ravi Tamada",
                "email": "ravi@gmail.com",
                "address": "xx-xx-xxxx,x - street, x - country",
                "gender" : "male",
                "phone": {
                    "mobile": "+91 0000000000",
                    "home": "00 000000",
                    "office": "00 000000"
                }
        },
        {
                "id": "c201",
                "name": "Johnny Depp",
                "email": "johnny_depp@gmail.com",
                "address": "xx-xx-xxxx,x - street, x - country",
                "gender" : "male",
                "phone": {
                    "mobile": "+91 0000000000",
                    "home": "00 000000",
                    "office": "00 000000"
                }
        }
  ]
}


/********************************************************************/
/********************************************************************/
//PARSER CLASS
/*
This parser class has a method which will make http request to get JSON data and returns a JSONObject
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.util.Log;


public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
 
    public JSONObject getJSONFromUrl(String url) {
 
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jObj;
 
    }
}

/********************************************************************/
/********************************************************************/
//USE

// url to make request
private static String url = "http://api.androidhive.info/contacts/";
 
// JSON Node names
private static final String TAG_CONTACTS = "contacts";
private static final String TAG_ID = "id";
private static final String TAG_NAME = "name";
private static final String TAG_EMAIL = "email";
private static final String TAG_ADDRESS = "address";
private static final String TAG_GENDER = "gender";
private static final String TAG_PHONE = "phone";
private static final String TAG_PHONE_MOBILE = "mobile";
private static final String TAG_PHONE_HOME = "home";
private static final String TAG_PHONE_OFFICE = "office";
 
// contacts JSONArray
JSONArray contacts = null;



// Creating JSON Parser instance
JSONParser jParser = new JSONParser();
 
// getting JSON string from URL
JSONObject json = jParser.getJSONFromUrl(url);
 
try {
    // Getting Array of Contacts
    contacts = json.getJSONArray(TAG_CONTACTS);
     
    // looping through All Contacts
    for(int i = 0; i < contacts.length(); i++){
        JSONObject c = contacts.getJSONObject(i);
         
        // Storing each json item in variable
        String id = c.getString(TAG_ID);
        String name = c.getString(TAG_NAME);
        String email = c.getString(TAG_EMAIL);
        String address = c.getString(TAG_ADDRESS);
        String gender = c.getString(TAG_GENDER);
         
        // Phone number is agin JSON Object
        JSONObject phone = c.getJSONObject(TAG_PHONE);
        String mobile = phone.getString(TAG_PHONE_MOBILE);
        String home = phone.getString(TAG_PHONE_HOME);
        String office = phone.getString(TAG_PHONE_OFFICE);
         
    }
} catch (JSONException e) {
    e.printStackTrace();
}