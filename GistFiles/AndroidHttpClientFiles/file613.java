HttpClient client = sslHttpStack.getmHttpClient();
HttpEntityEnclosingRequestBase entityRequest;
entityRequest = new HttpPost("http://my-server.com.au/my-api/customer/data");

MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
entityBuilder.addTextBody("someparameter", params.toString()); //params in this case is a JSONObject

String uniqueListingId = "images_to_upload"; //used to name any images being uploaded
int count = 0;
for (Bitmap eachImage : getImagesArray()) {
    String imageName = uniqueListingId + count;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    eachImage.compress(CompressFormat.JPEG, 75, byteArrayOutputStream); 
    byte[] byteData = byteArrayOutputStream.toByteArray();
    ByteArrayBody byteArrayBody = new ByteArrayBody(byteData, imageName + ".jpg");
    entityBuilder.addPart(imageName, byteArrayBody);
    count++;
}

HttpEntity entity = entityBuilder.build();
entityRequest.setEntity(entity);
HttpResponse response = client.execute(entityRequest);
HttpEntity httpEntity = response.getEntity();
String result = EntityUtils.toString(httpEntity);
