String cookieUrl = "SOME_URL_THAT_WILL_PROVIDE_COOKIE";
String authenticateUrl = "URL_TO_POST_FORM_DATA";
String dataUrl = "AUTHENTICATED_URL_YOU_WANT_DATA_FROM";

final String userNameKey = "FORM_KEY_FOR_USERNAME";
final String userPassKey = "FORM_KEY_FOR_PASSWORD";
final String userName = "USER_NAME";
final String userPass = "USER_PASSWORD";

HttpClient client = new DefaultHttpClient();
CookieStore cookieStore = new BasicCookieStore();
HttpContext context = new BasicHttpContext();
context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

String getUrl = cookieUrl;
HttpGet get = new HttpGet( getUrl );
HttpResponse getResponse = client.execute(get, context);
Log.d( "ConnectionTest", "GET @ " + getUrl );
Log.d( "ConnectionTest", getResponse.getStatusLine().toString() );

List<NameValuePair> authDataList = new ArrayList<NameValuePair>();
authDataList.add( new NameValuePair() {
    @Override
    public String getName() {
        return userNameKey;
    }

    @Override
    public String getValue() {
        return userName;
    }
} );
authDataList.add( new NameValuePair() {
    @Override
    public String getName() {
        return userPassKey;
    }

    @Override
    public String getValue() {
        return userPass;
    }
} );
HttpEntity authEntity = new UrlEncodedFormEntity( authDataList );

String authPostUrl = authenticateUrl;
HttpPost authPost = new HttpPost( authPostUrl );
authPost.setEntity( authEntity );
HttpResponse authPostResponse = client.execute(authPost, context);
Log.d( "ConnectionTest", "POST @ " + authPostUrl );
Log.d( "ConnectionTest", authPostResponse.getStatusLine().toString() );

String getUsersUrl = dataUrl;
HttpGet usersGet = new HttpGet( getUsersUrl );
HttpResponse usersGetResponse = client.execute(usersGet, context);
Log.d( "ConnectionTest", "GET @ " + getUsersUrl );
Log.d( "ConnectionTest", usersGetResponse.getStatusLine().toString() );
Log.d( "ConnectionTest", EntityUtils.toString( usersGetResponse.getEntity() ) );