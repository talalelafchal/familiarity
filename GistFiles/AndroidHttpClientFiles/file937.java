		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = null;
		//Choose whether mind https certificate
		if (isAuthentificationEnabled == false) {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			socketFactory = new MySSLSocketFactory(trustStore);//See the other class
			socketFactory.setHostnameVerifier(new AllowAllHostnameVerifier());
		} else {
			socketFactory = SSLSocketFactory.getSocketFactory();
		}
		registry.register(new Scheme("https", socketFactory, 443));
		HttpParams hp = new BasicHttpParams();
		
		HttpConnectionParams.setConnectionTimeout(hp, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(hp, SOCKET_TIMEOUT);
		
		SingleClientConnManager mgr = new SingleClientConnManager(hp, registry);
		httpClient = new DefaultHttpClient(mgr, hp);
		//--Until here, the httpClient is generated and will be used for the whole life of application--//
                //Begin the communication
		HttpPost httpPost = new HttpPost(url); 
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("USERNAME", cursor
				.getString(cursor.getColumnIndex("username"))));
		nameValuePairs.add(new BasicNameValuePair("PASSWORD", cursor
				.getString(cursor.getColumnIndex("password"))));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpClient.execute(httpPost);

		//Analyse the response
		BufferedReader br = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String msg = "";
		while ((msg = br.readLine()) != null) {
			System.out.println(msg);
		}
		
		br.close();