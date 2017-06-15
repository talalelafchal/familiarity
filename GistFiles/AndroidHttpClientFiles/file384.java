HttpClient client = new DefaultHttpClient();
String post_url = "https://myposturl.com";
HttpPost post = new HttpPost( post_url );

try
{
  List<NameValuePair> parameters = new ArrayList<>( 1 );
  parameters.add( new BasicNameValuePair( "param1", "param1" ) );
  post.setEntity( new UrlEncodedFormEntity( parameters ) );
  HttpResponse response = client.execute( post );

  BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );
  StringBuilder sb = new StringBuilder();
  String line = "";
  while ( ( line = rd.readLine() ) != null )
  {
    sb.append( line );
  }
  String data = sb.toString();
  JSONObject json = new JSONObject( data );
  
  }
} catch ( Exception e )
{
  // Catch errors
}
