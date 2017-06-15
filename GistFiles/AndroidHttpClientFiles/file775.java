public void validateApp()
  {
		try {
			HttpPost request = new HttpPost("http://binho.net/p/teste.json");
	        
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
	        nameValuePairs.add(new BasicNameValuePair("user_email", "positivo@positivo.com.br"));
	        nameValuePairs.add(new BasicNameValuePair("user_password", "123456"));
	        nameValuePairs.add(new BasicNameValuePair("application_id", APP_ID));
	        nameValuePairs.add(new BasicNameValuePair("device_id", obtemID()));
	        nameValuePairs.add(new BasicNameValuePair("device_type", "YPY"));
	        nameValuePairs.add(new BasicNameValuePair("device_os", "Android"));
	        System.out.println(nameValuePairs);
	        
	        try {
	        	request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setSoTimeout(params, 60000); // 1 minute
			request.setParams(params);

			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			
			String content = httpClient.execute(request, new BasicResponseHandler());
			System.out.println(content);
			
			JSONObject result;
			try {
				result = new JSONObject(content);
				
				Boolean is_valid = result.getBoolean("result");
				
				System.out.println(result);
				System.out.println(is_valid);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}