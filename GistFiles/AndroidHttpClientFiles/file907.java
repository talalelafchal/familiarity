		protected String[] doInBackground(String... credentials) {
			String consumer_secret = mActivity.getString(R.string.oauth_consumer_secret);
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost("https://www.instapaper.com/api/1/oauth/access_token");
			CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(mActivity.getString(R.string.oauth_consumer_key),
					consumer_secret);
			List<BasicNameValuePair> params = Arrays.asList(
					new BasicNameValuePair("x_auth_username", credentials[0]),
					new BasicNameValuePair("x_auth_password", credentials[1]),
					new BasicNameValuePair("x_auth_mode", "client_auth"));
			UrlEncodedFormEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("wtf");
			}
			request.setEntity(entity);
			try {
				HttpRequest signedRequest = consumer.sign(request);
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse response;
			InputStream data = null;
			try {
				response = client.execute(request);
				data = response.getEntity().getContent();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String responseString = null;
		    try
		    {
		      final char[] buffer = new char[0x10000];
		      StringBuilder out = new StringBuilder();
		      Reader in = new InputStreamReader(data, HTTP.UTF_8);
		      int read;
		      do
		      {
		        read = in.read(buffer, 0, buffer.length);
		        if (read > 0)
		        {
		          out.append(buffer, 0, read);
		        }
		      } while (read >= 0);
		      in.close();
		      responseString = out.toString();
		    } catch (IOException ioe)
		    {
		      throw new IllegalStateException("Error while reading response body", ioe);
		    }
		    
		    return TextUtils.split(responseString, "&");
		}