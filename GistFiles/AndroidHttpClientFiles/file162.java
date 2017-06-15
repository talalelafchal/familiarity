
/*  --  USAGE:  --  */

//to start a new download
new GetHTTPsAsync("https://hi.com") {  
	
	//provide a callback:
	protected void onPostExecute(String res) {
		Log.v(res); //yay, new string!
	}

}.execute(); //run it.




/* -- actual implementation -- */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.os.AsyncTask;
import android.util.Log;

public class GetHTTPsAsync extends AsyncTask<Void, Integer, String> {
	String urls;
	GetHTTPsAsync(String s) { urls = s; }

	@Override
	protected String doInBackground(Void... arg0) {
		try {
			//setup SSL
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			DefaultHttpClient client = new DefaultHttpClient();
			SchemeRegistry registry = new SchemeRegistry();
			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			registry.register(new Scheme("https", socketFactory, 443));
			SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
			DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

			// Set verifier      
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			// Example send http request
			HttpPost httpPost = new HttpPost(urls);
			HttpResponse response = httpClient.execute(httpPost);
			
			InputStream is = response.getEntity().getContent(); //outputs an inputstream
		    
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();
		    String line = null;

		    while ((line = reader.readLine()) != null) {
		        sb.append(line);
		    }

		    is.close();

		    return sb.toString();
			
		} catch (IOException e) { Log.w("IOException with GetHTTPsAsync. ", e); }
		return null;
	}
}