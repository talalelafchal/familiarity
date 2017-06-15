HttpClient httpClient = new DefaultHttpClient();
HttpHead req = new HttpHead("http://xxxxx/_now");
HttpResponse res = httpClient.execute(req);
Header header = res.getFirstHeader("x-now");
String value = header.getValue();
Long time = Long.parseLong(value) * 1000l;
Date now = new Date(time);