/**
This code snippet shows how an Android activity can trigger a web based auth using webview and then use
the cookies from there to make subsequent calls to an API (since the api is checking the cookies to detect
sign in)

MyApplication:  The root "controller" and the first activity in my app
OAuthLogin:     A class that handles the web based login.
                It montors the url change and if the user had been redirected to 
                a "success" page it sends out the cookie string to the root controller
                Once the controller has the cookie, it sets those on the service that
                does all the API calls.
MyAPIService:   The class that makes all calls to the api

*/
public class MyApplication{
    
    private MyAPIService service;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //authenticate
        CookieSyncManager.createInstance(this);
        service = new MyAPIService();
        
        Intent i = new Intent(MyApplication.this, OAuthLogin.class);
       	startActivity(i);
    }
    
    public void setCookie(String c){
        service.setCookie(c);
    }
    
    // When the cookie has been set on the service object proceed to call the api 
    // that needs the cookies for authenticating
    public void getProfile(){
        service.getProfile();
    }
    
}

public class OAuthLogin extends Activity{

    private static String MY_DOMAIN = "www.arpitonline.com"
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	WebView w = new WebView(this);
    	setContentView(w);
    	w.getSettings().setJavaScriptEnabled(true);
    	w.loadUrl(AUTH_URL);		
    	w.setWebViewClient(new WebViewClient(){
		
    		public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	        view.loadUrl(url);
    	        return true;
    	    }
		
    		public void onPageFinished(WebView view, String url){
    		    /**
    		    When the oAuth process sends the user to the final "success page",
    		    take the cookie and send it to my main auth class
    		    */
    			if(url.indexOf("authComplete=1") != -1){
    				String c = CookieManager.getInstance().getCookie(MY_DOMAIN);
    				// set the cookie on the "root" class thats managing the 
    				// entire application
    				MyApplication.getInstance().setCookie(c);
    				CookieSyncManager.getInstance().sync();
    				setResult(RESULT_OK);
    				finish();
    			}
    		}
    	}
    }
}

///// APi service class /////
public class MyAPIService{
    
    private HttpURLConnection conn;
	private DefaultHttpClient httpclient;
	private  BasicHttpContext localContext;
	private CookieStore cookieJar;
	
	
	private String _cookie = "";
	
	public void setCookie(String c){
		_cookie = c;
	}
	
	// this is only called once the setCookie has been called already
	public boolean loadUserProfile(){
		
		httpclient = new DefaultHttpClient();
		cookieJar = new BasicCookieStore();
		localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieJar);
		 
		 try{
			 
			 HttpGet httpget = new HttpGet(API_END_POINT);
			 
			 if(!_cookie.equals("")){
				 String[] cookies = _cookie.split(";");
				 for(int i=0; i< cookies.length; i++){
					 String[] nvp = cookies[i].split("=");
					 BasicClientCookie c = new BasicClientCookie(nvp[0], nvp[1]);
					 //c.setVersion(1);
					 c.setDomain(MY_DOMAIN);
					 cookieJar.addCookie(c);
				 }
		 	}
			 
			 HttpResponse response = httpclient.execute(httpget,localContext);
			 int code = response.getStatusLine().getStatusCode();
			 
			 if(code >= 400 && code < 500){
	            
	        	return false;
			 }
			 else{
				/*BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        	String line;
	        	while ((line = rd.readLine()) != null) {
	        	}*/
	        		
	        	return true;
			 }
			 
		 }catch(Exception e){
			// Log the error
			return false; 
		 }
	}
    
}

