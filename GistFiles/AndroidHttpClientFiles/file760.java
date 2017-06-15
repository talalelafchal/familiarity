private class PushAsyncTask extends AsyncTask<Void, Void, Void>{
  
  protected Void doInBackground(Void... voids) {
  		HttpPost httpPost = new HttpPost("https://<DOMAIN>.azure-mobile.net/tables/<TABLE-NAME>");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("X-ZUMO-APPLICATION", "YOUR-AZURE-MOBILESERVICE-KEY-IS-HERE");

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("platform", "ANDROID");
				jsonObject.put("channel", mRegistationId); // channel strings from Android device.
				jsonObject.put("text", "Sample Text");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				httpPost.setEntity(new StringEntity(jsonObject.toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
			HttpContext httpContext = new BasicHttpContext();

			HttpResponse response = null;
			try {
				response = httpClient.execute(httpPost, httpContext);

        			// Handling Received Json data from ZUMO (Azure Mobile Service)
				InputStream is = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line; (line = reader.readLine()) != null; )
					builder.append(line);
				JSONObject jsonResult = new JSONObject(builder.toString());
				Log.d(TAG, jsonResult.toString()); // Verify Result from Azure Mobile.

			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
    
  // Note: other overriden methods such as onPreExecute are omitted.
}