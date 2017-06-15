package be.kuleuven.toledo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class TestHttpClientActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        testDoesItSmoke();
    }
    
       
    public void testDoesItSmoke(){
    	
    	//not allowed to execute network IO on the main thread --> run as async task
       	new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				
				Looper.prepare();  
				
				DefaultHttpClient httpclient = new DefaultHttpClient();
		    	httpclient.setRedirectHandler( new DefaultRedirectHandler(){
		    		public boolean isRedirectRequested(HttpResponse response, HttpContext context){
		    			boolean isRedirect = super.isRedirectRequested(response, context);
		    			int responseCode = response.getStatusLine().getStatusCode();
		        		return isRedirect || responseCode == 301 || responseCode == 302;
		    		}
		    	});

		    	HttpGet httpGet = new HttpGet("https://wayf.associatie.kuleuven.be/shibboleth-wayf/WAYF?shire=https%3A%2F%2Flyra.cc.kuleuven.be%2FShibboleth.sso%2FSAML%2FArtifact&target=https%3A%2F%2Flyra.cc.kuleuven.be%2Fwebapps%2Flogin%2F&providerId=https%3A%2F%2Flyra.cc.kuleuven.be&action=selection&cache=session&origin=urn%3Amace%3Akuleuven.be%3Akulassoc%3Akuleuven.be");
		    	
		    	String response = "failed";
				try {
					Log.e("SSL", "here we go");
					response = httpclient.execute( httpGet, new BasicResponseHandler() );
					Toast.makeText(TestHttpClientActivity.this, response, Toast.LENGTH_LONG ).show();
				} catch (Exception e) {
					Log.e("SSL", "whooops",e);
					Toast.makeText(TestHttpClientActivity.this, e.getMessage(), Toast.LENGTH_LONG ).show();
					e.printStackTrace();
				}
		    	
				httpclient.getConnectionManager().shutdown();
				
				Log.e("SSL", response);
				
				Looper.loop();  
				
				return null;
			}
    		
    	}.execute((Void)null);
    	
    	
    	
    }
    
    
}