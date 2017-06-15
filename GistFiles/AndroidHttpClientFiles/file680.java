Uri uri = null; // a Uri pointing to the file to be uploaded

HttpClient httpclient = new DefaultHttpClient();
HttpPost httppost = new HttpPost(SERVER_URL);

InputStream stream = null;
try {
	stream = getContext().getContentResolver().openInputStream(uri);

	InputStreamEntity reqEntity = new InputStreamEntity(stream, -1);

	httppost.setEntity(reqEntity);

	HttpResponse response = httpclient.execute(httppost);
	if (response.getStatusLine().getStatusCode() == 200) {
    // file uploaded successfully!
	} else {
		throw new RuntimeException("server couldn't handle request");
	}
} catch (Exception e) {
	e.printStackTrace();
  
  // handle error
} finally {
  stream.close();
}