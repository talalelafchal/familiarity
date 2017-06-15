package com.darmasoft.parental_informant;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class PIWebService {

	private static final String TAG = "PIWebService";
	
	private static PIWebService _instance;
	private ArrayList<Location> _locations = null;
	private ArrayList<Location> _tmp_locations = null;

	private HttpParams _http_params = null;
	private ClientConnectionManager _conn_manager;
	private HttpContext _http_context;
	private HttpClient _client = null;
	
	private final String _server_proto = "http";
	private final String _server_host = "pi.darmasoft.net";
	//private final String _server_host = "192.168.248.181:3000";
	//private final String _server_host = "10.14.111.10:3000";
	//private final String _server_host = "motherrussia.darmasoft.com:3000";
	
	private final int _api_version = 1;
	
	private PIWebService() {
		// TODO Auto-generated constructor stub
		Log.d(TAG, "PIWebService()");
		_locations = new ArrayList<Location>();
		
		SchemeRegistry scheme_registry = new SchemeRegistry();
		
		scheme_registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		scheme_registry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		
		_http_params = new BasicHttpParams();
		_http_params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 3);
		_http_params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));
		_http_params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		_http_params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpConnectionParams.setConnectionTimeout(_http_params, 3000);
		HttpConnectionParams.setSoTimeout(_http_params, 5000);
		HttpProtocolParams.setVersion(_http_params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(_http_params, "utf8");
		
		_conn_manager = new ThreadSafeClientConnManager(_http_params, scheme_registry);
		_http_context = new BasicHttpContext();
		
		_client = new DefaultHttpClient(_conn_manager, _http_params);
		
	}

	public static PIWebService instance() {
		if (_instance == null) {
			_instance = new PIWebService();
		}
		return(_instance);
	}

	public void sayHello()
	{
		Log.d(TAG, "sayHello()");
		Log.d(TAG, String.format("url: %s", helloUrl()));
		URI uri = null;
		
		try {
			uri = new URI(helloUrl());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(TAG, String.format("host: %s", uri.getHost()));
		Log.d(TAG, String.format("port: %d", uri.getPort()));
		
		HttpPost post = new HttpPost(uri);
		HttpResponse res = null;

		try {
			res = _client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!(res == null)) {
			String body = HttpHelper.readBody(res);
			Log.d(TAG, String.format("body: %s", body));
		}
	}
	
	public void addLocation(Location loc)
	{
		Log.d(TAG, String.format("addLocation(%s)", loc.toString()));
		_locations.add(loc);
		Log.d(TAG, String.format("%d locations", _locations.size()));
	}
	
	public void syncLocations()
	{
		Log.d(TAG, "syncLocations()");
		_tmp_locations =  new ArrayList<Location>(_locations);
		_locations.clear();
		if (_tmp_locations.size() == 0) {
			return;
		}
		
		Log.d(TAG, String.format("tmp_locations: %d", _tmp_locations.size()));
		JSONArray locs = new JSONArray();
		for (int i = 0; i < _tmp_locations.size(); i++) {
			Location l = _tmp_locations.get(i);
			JSONObject loc = new JSONObject();
			try {
				loc.put("latitude", l.getLatitude());
				loc.put("longitude", l.getLongitude());
				loc.put("accuracy", l.getAccuracy());
				loc.put("time", l.getTime());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			locs.put(loc);
		}
		Log.d(TAG, String.format("json: %s", locs.toString()));
		
		URI uri = null;
		
		try {
			uri = new URI(locationsUrl());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpPost post = new HttpPost(uri);
		HttpResponse res = null;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("locs", locs.toString()));

		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			res = _client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!(res == null)) {
			String body = HttpHelper.readBody(res);
			Log.d(TAG, String.format("body: %s", body));
		}
		
	}
	
	private String helloUrl()
	{
		return(String.format("%s://%s/api/v%d/device/%s/%d/hello", _server_proto, _server_host, _api_version, Installation.id(), Installation.account_code()));
	}
	
	private String locationsUrl()
	{
		return(String.format("%s://%s/api/v%d/device/%s/locations", _server_proto, _server_host, _api_version, Installation.id()));
	}
	
}
