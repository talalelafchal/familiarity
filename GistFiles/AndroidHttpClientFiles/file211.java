/** Static helper methods related to accessing Internet. */
public class HttpUtils {

    /** No need to instantiate this class. */
    private HttpUtils() {}

    /** Downloads and returns the information from a given URI in the same thread
     * this is called in. */
    public static String get(String uri) throws IOException, URISyntaxException {
        return get(new URI(uri));
    }

    /** Downloads and returns the information from a given URI in the same
     * thread this is called in. The returned String may contain XML, JSON,
     * or another format. You should check for network connectivity before
     * calling this.
     * Note: This is just a simple implementation, it should be made more
     * robust, like by using a nice library. */
    public static String get(URI uri) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(uri);
        HttpResponse httpResponse = httpClient.execute(httpGet);

        InputStream inputStream;
        BufferedReader reader = null;
        String value = null;
        try {
            inputStream = httpResponse.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder(inputStream.available());
            while ((value = reader.readLine()) != null) {
                sb.append(value);
            }
            value = sb.toString();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Close quietly.
                }
            }
        }
        return value;
    }

}