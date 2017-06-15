	public static void fetchImage(ArrayList imgUrls, ArrayList bmpArr) {
		for (int i = 0; i < imgUrls.size(); i++) {
			String urlstr = (String) imgUrls.get(i);
			InputStream is = null;
			Bitmap bm = null;
			try {
				System.out.println("URL: " + urlstr);
				HttpGet httpRequest = new HttpGet(urlstr);
				System.out.println("Request: " + httpRequest);
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = (HttpResponse) httpclient
						.execute(httpRequest);

				HttpEntity entity = response.getEntity();
				BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(
						entity);
				is = bufHttpEntity.getContent();
				bm = BitmapFactory.decodeStream(is);
			} catch (MalformedURLException e) {
				Log.d("RemoteImageHandler", "fetchImage passed invalid URL: "
						+ urlstr);
			} catch (IOException e) {
				Log.d("RemoteImageHandler", "fetchImage IO exception: " + e);
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
					}
			}
			bmpArr.add(bm);
		}
	}