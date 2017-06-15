public class HttpClientFactory {

    private HttpClient client;
    private HttpPost httpPost;
    private HttpGet httpGet;
    private String responseBody;

    public JSONObject sendGet(String url) {
        client = new DefaultHttpClient();
        httpGet = new HttpGet(Extras.BASE_URL + Extras.BASE_API + url);

        try {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = client.execute(httpGet, responseHandler);

            if (isJson(responseBody)) {
                return new JSONObject(responseBody);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean isJson(String str) {
        try {
            new JSONObject(str);
        } catch (JSONException e) {
            try {
                new JSONArray(str);
            } catch (JSONException e1) {
                return false;
            }
        }
        return true;
    }
}
