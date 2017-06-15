public static void executeMultipartPost(String url, String imgPath, String field1, String field2){
  try {
    HttpClient client = new DefaultHttpClient();
    HttpPost poster = new HttpPost(url);

    File image = new File(imgPath);  //get the actual file from the device
    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);          
    entity.addPart("field1", new StringBody(field1));
    entity.addPart("field2", new StringBody(field2));
    entity.addPart("image", new FileBody(image));
    poster.setEntity(entity );
    
    client.execute(poster, new ResponseHandler<Object>() {
      public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        HttpEntity respEntity = response.getEntity();
	String responseString = EntityUtils.toString(respEntity);
        // do something with the response string
	return null;
      }
    });
  } catch (Exception e){
    //do something with the error
  }
}