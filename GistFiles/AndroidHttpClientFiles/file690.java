String urlToSendRequest = "http://example.com";
String targetDomain = "example.com";
String xmlContentToSend = "hello this is a test";

DefaultHttpClient httpClient = new DefaultHttpClient();

HttpHost targetHost = new HttpHost(targetDomain, 80, "http");
// Using POST here
HttpPost httpPost = new HttpPost(urlToSendRequest);
// Make sure the server knows what kind of a response we will accept
httpPost.addHeader("Accept", "text/xml");
// Also be sure to tell the server what kind of content we are sending
httpPost.addHeader("Content-Type", "application/xml");

try {
    StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
    entity.setContentType("application/xml");
    httpPost.setEntity(entity);

    // execute is a blocking call, it's best to call this code in a
    // thread separate from the ui's
    HttpResponse response = httpClient.execute(targetHost, httpPost);

    // Have your way with the response
}
catch (Exception ex){
    ex.printStackTrace();
}