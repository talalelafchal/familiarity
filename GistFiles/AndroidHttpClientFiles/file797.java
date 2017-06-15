private HttpClient client;

public HttpModule() {
	client = new DefaultHttpClient();
}

public void executePost() {
	HttpPost post = new HttpPost(APIConfiguration.getURL(Protocol.HTTPS, "user"));
	Console.log(post.getURI());
	
    try {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("version", APIConfiguration.API_VERSION));
        data.add(new BasicNameValuePair("mail", "bidon@truc.com"));
        post.setEntity(new UrlEncodedFormEntity(data));

        HttpResponse response = client.execute(post);

        Console.log(new BasicResponseHandler().handleResponse(response));
    } catch (ClientProtocolException e) {
    	Console.err(e);
    } catch (IOException e) {
    	Console.err(e);
    }
}