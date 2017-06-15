public class ServiceHandler {

    public ServiceHandler() {
    }

    public static String POST(String url, List<String> params){
        InputStream inputStream = null;
        String result = "";p

        try {

            DefaultHttpClient httpclient = new DefaultHttpClient();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            HttpPost httpPostLogin = new HttpPost(url);

            String jsonLogin = "";
            JSONObject jsonObjectLogin = new JSONObject();
            jsonObjectLogin.put("Username", "xxx");
            jsonObjectLogin.put("Password", "yyy*");
            jsonLogin = jsonObjectLogin.toString();

            StringEntity seLogin = new StringEntity(jsonLogin);
            httpPostLogin.setEntity(seLogin);
            httpPostLogin.setHeader("Accept", "application/json");
            httpPostLogin.setHeader("Content-type", "application/json");

            HttpResponse httpResponseLogin = httpclient.execute(httpPostLogin, localContext);

            inputStream = httpResponseLogin.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Can't connect to service!";

            HttpPost httpPost = new HttpPost(params.get(1));

            String json = "";
            JSONObject jsonObject = new JSONObject();

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost, localContext);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Can't connect to service!";


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}