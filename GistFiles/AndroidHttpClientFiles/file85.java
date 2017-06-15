public final class UrlUtils {

    private UrlUtils() {
    }

    public static URL getUrl(String urlAsString) {
        URL url;
        try {
            url = new URL(urlAsString);
        } catch (MalformedURLException e) {
            Log.w(UrlUtils.class.getName(), "Invalid format for url : " + urlAsString, e);
            return null;
        }

        return url;
    }

    public static String downloadData(String url) {
        Log.d(UrlUtils.class.getName(), "Connection opened to : " + url);
        long time = System.currentTimeMillis();

        HttpClient client = new DefaultHttpClient();

        try {
            HttpResponse response = client.execute(new HttpGet(url));
            InputStream in = response.getEntity().getContent();

            try {
                return IOUtils.toString(in);
            } finally {
                in.close();
            }
        } catch (Exception e) {
            Log.e(UrlUtils.class.getName(), "Error during downloading : " + e.getMessage());
            return null;
        } finally {
            Log.d(UrlUtils.class.getName(), "Finish in " + (System.currentTimeMillis() - time) + " ms");
        }
    }
}