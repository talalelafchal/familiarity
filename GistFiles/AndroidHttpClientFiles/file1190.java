private String updateProfile(Map map) {
	File image = (File) map.get(KEY_IMAGE);
	File background = (File) map.get(KEY_BACKGROUND);
	FCUser user = (FCUser) map.get(KEY_USER);

	String userJsonString = user.toJSONUpdate().toString();

	// TODO : Change HTTPClient to HttpUrlConnection or Android Async HTTP and Upload Image

	HttpClient httpClient = new DefaultHttpClient();
	HttpPost httpPost = new HttpPost(FlipCardClient.API_PROFILE);
	HttpResponse httpResponse;
	HttpEntity httpEntity;
	String response = "";

	try {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		// Image
		if (image != null) {
			FileBody fileBodyImage = new FileBody(image);
			builder.addPart(KEY_IMAGE, fileBodyImage);
		}

		// Background
		if (background != null) {
			FileBody fileBodyBackground = new FileBody(background);
			builder.addPart(KEY_BACKGROUND, fileBodyBackground);
		}

		// User ID
		String userId = String.valueOf(user.getId());
		builder.addTextBody(KEY_USER_ID, userId, ContentType.TEXT_PLAIN);

		// User Profile JSON String
		builder.addTextBody(KEY_PROFILE, userJsonString, ContentType.TEXT_PLAIN);

		HttpEntity entity = builder.build();
		httpPost.setEntity(entity);
		httpResponse = httpClient.execute(httpPost);
		httpEntity = httpResponse.getEntity();
		response = EntityUtils.toString(httpEntity);

	} catch (ClientProtocolException e) {
		e.printStackTrace();
	} catch (IOException e) {
		Log.v(TAG, "Something wrong with I/O operation");
		e.printStackTrace();
	}

	return response;
}