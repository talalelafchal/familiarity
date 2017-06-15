protected String doInBackground(String... params){
	String attachmentName = "profilePic";
	String crlf = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	String testName="sandeep.jpg";
	try{
		HttpURLConnection httpUrlConnection = null;
		URL url = new URL("http://your_url_here.com/rest/update/profilePicture");
		httpUrlConnection = (HttpURLConnection) url.openConnection();
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
		httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
		httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

		Bitmap bitmap = get your bit map;
		byte[] byteArray = stream.toByteArray();

		DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
		request.writeBytes(twoHyphens + boundary + crlf);
		request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\"" + crlf);
		request.writeBytes("Content-Type: image/jpeg" + crlf);
		request.writeBytes(crlf);
		request.write(byteArray);
		request.writeBytes(crlf);
		request.writeBytes(twoHyphens + boundary + crlf);
		request.writeBytes("Content-Disposition: form-data; name=\"testingName\"" + crlf);
		request.writeBytes(crlf);
		request.writeBytes(testName);
		request.writeBytes(crlf);
		request.writeBytes(twoHyphens + boundary + twoHyphens);
		request.flush();
		request.close();
		InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
		BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = responseStreamReader.readLine()) != null) {
			stringBuilder.append(line).append("\n");
		}
		responseStreamReader.close();
		String response = stringBuilder.toString();
		return response;
	}
	catch (Exception exception){
		return exception.toString();
	}
}
