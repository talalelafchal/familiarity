//Global
private DefaultHttpClient client = new DefaultHttpClient();
//Library: https://code.google.com/p/google-gson/
gson = new Gson();

HttpParams httpParameters = new BasicHttpParams();
HttpConnectionParams.setConnectionTimeout(httpParameters,Utils.NETWORK_CONNECTION_TIMEOUT);
HttpConnectionParams.setSoTimeout(httpParameters,Utils.NETWORK_SOCKET_TIMEOUT);

client.setParams(httpParameters);



//Model sample of a  User class
public class User 
{
    String username;
    String password; //TODO: make byte[]?
    boolean preserver;

    public boolean isPreserver() {
        return preserver;
    }

    public void setPreserves(boolean preserver) {
        this.preserver = preserver;
    }
          
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }        
}




//Custom JsonResponse class. Variables are the keys of the json request service response
public class JsonResponse {

	private boolean status;
	private String message;
	private String result;

	public JsonResponse(boolean status, String message, String result) {
		super();
		this.status = status;
		this.message = message;
		this.result = result;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}


//Initialize post and put requests
public static HttpPost getUtf8HttpPost(String url, String args)
		throws UnsupportedEncodingException {

	HttpPost postMethod = new HttpPost(url);

	postMethod.setHeader("Accept", "application/json");

	StringEntity postData = new StringEntity(args, "UTF-8");
	postData.setContentType("application/json; charset=UTF-8");
	postMethod.setEntity(postData);

	return postMethod;
}
	
public static HttpPut getUtf8HttpPut(String url, String args)
		throws UnsupportedEncodingException {

	HttpPut putMethod = new HttpPut(url);

	putMethod.setHeader("Accept", "application/json");

	StringEntity putData = new StringEntity(args, "UTF-8");
	putData.setContentType("application/json; charset=UTF-8");
	putMethod.setEntity(putData);

	return putMethod;


}
//Actual JSON class
//The AsyncTask class encapsulates the creation of a background process and the synchronization with the main thread.
//To use it you have ti subclass it
//The parameters are the following AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue>
public class AsyncJSON extends AsyncTask<String, Void, JsonResponse> {

		String httpUrl;
		String httpArgs;
		int httpMethod;

		public AsyncJSON(String url, int method, String args) {
			httpUrl = url;
			httpMethod = method;
			httpArgs = args;

		}

		protected JsonResponse doInBackground(String... url) {
			// Making HTTP request
			String responseBody = "";
			JsonResponse response = null;
			try {

				switch (httpMethod) {

				case HTTP_POST:
					HttpPost postMethod = getUtf8HttpPost(httpUrl, httpArgs);
					Log.d(tag, "Http POST to " + httpUrl + " with body: "+ httpArgs);
					responseBody = client.execute(postMethod, resposneHandler);
					Log.d(tag, " Response: " + responseBody);		//Tag is a string ex."Title"
					response = gson.fromJson(responseBody, JsonResponse.class);
					return response;
				case HTTP_GET:
					HttpGet httpGet = new HttpGet(httpUrl);
					Log.d(tag, "Http GET from " + httpUrl);
					responseBody = client.execute(httpGet, resposneHandler);
					Log.d(tag, " Response: " + responseBody);
					response = gson.fromJson(responseBody, JsonResponse.class);
					return response;
				case HTTP_PUT:
					HttpPut httpPut = new HttpPut(httpUrl);
					Log.d(tag, "Http PUT to " + httpUrl + " with body: "+ httpArgs);
					responseBody = client.execute(httpPut, resposneHandler);
					Log.d(tag, " Response: " + responseBody);
					response = gson.fromJson(responseBody, JsonResponse.class);
					return response;
				default:
					Log.d(tag, "Network call error in AsyncJSON.");
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return response;
		}

		protected void onPostExecute() {
			// TODO: check this.exception		

		}
	}

}

/********************************************************************/
/********************************************************************/
//USE

public JsonResponse login(User user) {

	String url = "http://...";
	String args = gson.toJson(user);
	AsyncJSON Task = new AsyncJSON(url, HTTP_POST, args);
	try {
		return Task.execute().get();
	} 
	catch (Exception e) {
		if (LOG) Log.e("NETWORK ERROR", e.toString());
		return null;
	}
}

public boolean register(User user) {
	try {
		String url = urlProvider.getServerUrl();
		String args = gson.toJson(user);

		HttpPost postMethod = new HttpPost(url); //(There is also HttpPut HttpGet etc)

		postMethod.setHeader("Accept", "application/json");

		StringEntity postData = new StringEntity(args, "UTF-8");
		postData.setContentType("application/json; charset=UTF-8");
		postMethod.setEntity(postData);

		String responseBody = client.execute(postMethod, resposneHandler);

	}
	catch (Exception e) {
		Utils.logNetworkError(e);
	}
	
	return false;
}


//MAKE THE CALL
EditText username = (EditText) findViewById(R.id.user_text);
EditText password = (EditText) findViewById(R.id.pass_text);
				
User curUser = new User(username.getText().toString(), password.getText().toString());

JsonResponse loginresult = someInstanceOfTheClass.login(curUser);

//GET THE RESULT

//User logged successfully or is already online
//"ok" and "User is already signed in" is the result value of the json returned (custom)
if (loginresult.getResult().equals("ok") || loginresult.getMessage().equals( "User is already signed in"))
{
	//Do stuff, go to another activity
}
else
{
	//Log in failed
}
	
