//http://stackoverflow.com/questions/2423498/how-to-get-the-html-source-of-a-page-from-a-html-link-in-android
HttpClient client = new DefaultHttpClient();
HttpGet request = new HttpGet(url);
HttpResponse response = client.execute(request);

String html = "";
InputStream in = response.getEntity().getContent();
BufferedReader reader = new BufferedReader(new InputStreamReader(in));
StringBuilder str = new StringBuilder();
String line = null;
while((line = reader.readLine()) != null)
{
    str.append(line);
}
in.close();
html = str.toString();