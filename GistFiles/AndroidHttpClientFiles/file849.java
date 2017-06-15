import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by blray on 2/23/2016.
 */
public class RestClient {
    
    AsyncHttpClient client = new AsyncHttpClient();
    
    String url;
    RequestParams requestParams;
    
    public RestClient() {

    url = "www.example.com";
        requestParams = new RequestParams();
        

    }
    
    void post(){
      
      //Set POST parameters
        
        requestParams.put("key", "value");
        requestParams.put("boolean", false);
        requestParams.put("int", 0);

//Make REST call using async http client

        client.post(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                
                //If response is JSONObject
                
            }
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                
                //If response is JSONArray
                
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                
                //If post failed

            }

        });
        
    }
    
    }
