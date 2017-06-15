
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import com.google.gson.Gson;

import android.net.http.AndroidHttpClient;
import android.util.Base64;
import android.util.Log;

abstract class ReusableHttpClient<T> {
    private static String TAG = ReusableHttpClient.class.getName();
	
	private static AndroidHttpClient client = AndroidHttpClient.newInstance("");
	private static Gson gson = new Gson();
	
	private final static String AUTH_TOKEN_PATTERN = "%s:%s";
	private final static String BASIC_AUTH_PATTERN = "Basic %s";
	private final static String BEARER_AUTH_PATTERN = "Bearer %s";
	
	private String scheme;
	private String host;
	private int port;	
	
	private String generateAuthToken(String username, String password){
		String result = null;
		try {
			result = Base64.encodeToString(String.format(AUTH_TOKEN_PATTERN, username, password).getBytes("UTF-8"), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String generateAuthHeader(String username, String password){
		String result = null;
		String token = generateAuthToken(username, password);
		if (token != null){
			result = String.format(BASIC_AUTH_PATTERN, token);
		}
		
		return result;
	}
	
	private String generateAuthHeader(String token){
		String result = String.format(BEARER_AUTH_PATTERN, token);		
		
		return result;
	}
	
	private Header[] generateHttpRequestHeaders(String username, String password){
		
		List<Header> result = new ArrayList<Header>();
		result.add(new BasicHeader("Content-type", "application/json"));
		
		if (username != null && password != null){
			result.add(new BasicHeader("Authorization", generateAuthHeader(username, password)));
		}
		return result.toArray(new Header[]{});
	}
	
private Header[] generateHttpRequestHeaders(String token){
		
		List<Header> result = new ArrayList<Header>();
		result.add(new BasicHeader("Content-type", "application/json"));
		
		if (token != null){
			result.add(new BasicHeader("Authorization", generateAuthHeader(token)));
		}
		return result.toArray(new Header[]{});
	}
	
	public ReusableHttpClient(String scheme, String host, int port) {
		super();
		this.scheme = scheme;
		this.host = host;
		this.port = port;
	}

	public abstract ResponseHandler<T> createResponseHandler();
	public abstract ResponseHandler<List<T>> createListResponseHandler();

    protected void handleErrors(HttpResponse resp) throws IOException {
        int respCode = resp.getStatusLine().getStatusCode();
        if (respCode == 404) {
            throw new NoResourceFoundException("Not found");
        } else if (!is20x(respCode)) {
            Log.e(TAG, convertStreamToString(resp.getEntity().getContent()));
            throw new ErrorAccessingResourceException("Error");
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
	
	public T get(String path, Map<String, String> parameters, String token) throws IOException {
		
		T response = null;
		
		try {
			String paramString = mapToQueryString(parameters);
			URI uri = URIUtils.createURI(scheme, host, port, path, paramString, null);
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeaders(generateHttpRequestHeaders(token));
			
			response = client.execute(httpGet, createResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	public List<T> getAll(String path, Map<String, String> parameters, String token) throws IOException {
		
		List<T> response = null;
		
		try {
			String paramString = mapToQueryString(parameters);
			URI uri = URIUtils.createURI(scheme, host, port, path, null, null);
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeaders(generateHttpRequestHeaders(token));

            Log.i("HttpClient", "GETing URI: " + uri);
			response = client.execute(httpGet, createListResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}

	public T post(String path, Map<String, String> parameters, Object obj, String token) throws IOException {				
		
		
		T response = null;		
		
		try {
			String paramString = mapToQueryString(parameters);
			URI uri = URIUtils.createURI(scheme, host, port, path, paramString, null);
			HttpPost httpPost = new HttpPost(uri);
			if (obj != null){
				String requestBody = gson.toJson(obj);
				httpPost.setEntity(new StringEntity(requestBody));
			}
			
			httpPost.setHeaders(generateHttpRequestHeaders(token));			
			
			response = client.execute(httpPost, createResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	public T put(String path, Map<String, String> parameters, T obj, String token) throws IOException {				
		
		
		T response = null;		
		
		try {
			String paramString = mapToQueryString(parameters);
			URI uri = URIUtils.createURI(scheme, host, port, path, paramString, null);
			String requestBody = gson.toJson(obj);
			HttpPut httpPut = new HttpPut(uri);
			httpPut.setEntity(new StringEntity(requestBody));
			httpPut.setHeaders(generateHttpRequestHeaders(token));
			
			response = client.execute(httpPut, createResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	private String mapToQueryString(Map<String, String> params) throws UnsupportedEncodingException{
		if (params == null || params.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		  for(HashMap.Entry<String, String> e : params.entrySet()){
		      if(sb.length() > 0){
		          sb.append('&');
		      }
		      sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
		  }
		  return sb.toString();
	}
	
	protected boolean is20x(int num){
		return ((200 <= num) && (num < 300));
	}
	protected boolean is30x(int num){
		return ((300 <= num) && (num < 400));
	}
	protected boolean is40x(int num){
		return ((400 <= num) && (num < 500));
	}
}
