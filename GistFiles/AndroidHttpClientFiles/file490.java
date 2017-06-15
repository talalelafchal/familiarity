package br.com.say2me.app.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import br.com.say2me.app.MainActivity;
import br.com.say2me.app.config.ConfigManager;

public class POST extends AuthLayer{

	private String url;
	private List<BasicNameValuePair> data;
	
	
	public POST(MainActivity activity, String url, List<BasicNameValuePair> data) {
		super(activity);
		this.url = url;
		this.data = data;
		
		// especifico dos casos de uso deste projeto
		if(data == null){
			this.data = new ArrayList<BasicNameValuePair>();
		}
		this.data.add(new BasicNameValuePair("token_t", "t"));
		this.data.add(new BasicNameValuePair("token", this.getActivity().getFacade().getAuth().getToken()));
		this.data.add(new BasicNameValuePair("imei", this.getActivity().getImei()));
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		String result = "";
        try {
	
		// 1. create HttpClient
	        HttpClient httpclient = new DefaultHttpClient();
	        
	        // 2. make POST request to the given URL
	        HttpPost httpPost = new HttpPost(this.url);

	        httpPost.addHeader("Authorization", "Token token="+ConfigManager.AUTH_TOKEN);
	        // Request parameters and other properties.
		httpPost.setEntity(new UrlEncodedFormEntity(this.data, "UTF-8"));
	        	        
	        // 3. Execute POST request to the given URL
	        HttpResponse httpResponse = httpclient.execute(httpPost);
	
	        // 4. receive response as inputStream
	        InputStream inputStream = httpResponse.getEntity().getContent();
	
	        // 5. convert inputstream to string
	        if(inputStream != null)
	            result = convertInputStreamToString(inputStream);
	        else
	            result = "Did not work!";
	        
	        int status = httpResponse.getStatusLine().getStatusCode();
	        
	        // 6. sets the status, so the AuthLayer can work normally;
	        this.setProtocolStatus(status);
        
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
        return result;
	}
	
	protected String getURL(){
		return this.url;
	}
	
	protected String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	        String line = "";
	        String result = "";
	        while((line = bufferedReader.readLine()) != null)
	            result += line;
	 
	        inputStream.close();
	        return result;
	}

}
