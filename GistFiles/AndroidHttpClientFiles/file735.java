// apiSecret and accessToken have already been acquired

HttpClient httpClient = AndroidHttpClient.newInstance("");
HttpGet httpGet = new HttpGet(Urls.BASE + "/user");

long utcTimestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
String rawNonce = NONCE_SALT + RANDOM.nextInt();
String nonce = rawNonce.substring(0, Math.min(rawNonce.length(), 35));
String normalisedString = accessToken + "\n"
	+ utcTimestamp + "\n" 
	+ nonce + "\n"
	+ "GET\n"
        + mlkshk.com\n"
	+ "80\n"
	+ Urls.USER + "\n";

// This is from the standard Java crypto librairies

SecretKeySpec signingKey = new SecretKeySpec(apiSecret.getBytes(), HMAC_SHA1);
Mac mac = Mac.getInstance(HMAC_SHA1);
mac.init(signingKey);
byte[] rawHmac = mac.doFinal(normalisedString.getBytes());

// Base64 is from the Android SDK

String signature = Base64.encodeToString(String.format("%x", new BigInteger(rawHmac)).getBytes(), Base64.NO_PADDING | Base64.NO_WRAP);

httpGet.addHeader("Authorization", "MAC token=\"" + accessToken + "\", timestamp=\"" + utcTimestamp + "\", nonce=\"" + nonce + "\", signature=\"" + signature + "\"");
HttpResponse httpResponse = httpClient.execute(httpGet);
HttpEntity entity = httpResponse.getEntity();

System.out.println(httpResponse.getStatusLine());
System.out.println(httpResponse.getFirstHeader("WWW-Authenticate"));
BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
String line;
StringBuilder jsonBuilder = new StringBuilder();
while ((line = reader.readLine()) != null) {
	jsonBuilder.append(line);
}
System.out.println(jsonBuilder);
