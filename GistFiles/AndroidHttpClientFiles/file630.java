// Normal GET approach with AuthLayer. Remember to implement the method postExecuteAction as needed
package br.com.say2me.app.async;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import br.com.say2me.app.MainActivity;
import br.com.say2me.app.config.ConfigManager;
import br.com.say2me.app.model.Auth;

public class GET extends AuthLayer{

	private String url;
	private List<BasicNameValuePair> data;
	

	public GET(MainActivity activity, String url) {
		super(activity);
		this.url = url;
		
		// I have a class to handle my Authorization parameters
		Auth auth = this.getActivity().getFacade().getAuth();
		
		// This is a specific point of the project, as no GET request uses other parameter beyond the token
		this.data = null;
		this.checkData();
		this.data.add(new BasicNameValuePair("token",auth.getToken()));
		
	}

	// if there is no data, initializes an empty array.
	private void checkData() {
		if(this.data == null)
			this.data = new ArrayList<BasicNameValuePair>();
	}
	// be aware to implement the method postExecuteAction after the execution of the GET, in case you want to add other functionalities
	@Override
	protected Object doInBackground(Object... params) {
		try {
			return downloadUrl();
		} catch (IOException e) {
			return "error";
		}
		
	}
	
	private String downloadUrl() throws IOException {
		InputStream is = null;
		String contentAsString = "";
		try {
			
			URL url = this.getParsedUrl();
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(50000);
			conn.setConnectTimeout(55000);

			
			String basicAuth = "Token token=" + ConfigManager.AUTH_TOKEN;
			conn.setRequestProperty ("Authorization", basicAuth);
			
			conn.setRequestMethod("GET");
			conn.setDoInput(true); 
			conn.connect();
			
			int status = conn.getResponseCode();
	        

	        this.setProtocolStatus(status);
	        if(status == 200){
				is = conn.getInputStream();
				contentAsString = readIt(is);
	        }
			
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			} 
		}
		return contentAsString;
	}
	
	private URL getParsedUrl() throws MalformedURLException {
		URL url = null;
		
		String urlTemp = this.url;
		if(this.data.size()>0)
			urlTemp = urlTemp+"?";
		for (BasicNameValuePair bs : this.data) {
			urlTemp = urlTemp+"&"+bs.getName()+"="+bs.getValue();
		}
		url = new URL(urlTemp);

		return url;
	}

	public String readIt(InputStream stream) throws IOException {
	        Reader reader = null;
	        reader = new InputStreamReader(stream);        
	        String buffer = "";
	        int content = 0;
	        while(content != -1){
	        	content = reader.read();
	        	buffer += (char) content;
	        }
	        reader.close();
	        return buffer;
	}
}
