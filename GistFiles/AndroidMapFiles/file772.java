public class Service {
    public final class Method {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
    }

    public interface OnBeforeRequestListener {
        public void onBeforeRequest();
    }

    public interface OnRequestFailedListener {
        public void onRequestFailed(String response, int statusCode);
    }

    public interface OnAfterRequestListener {
        public void onAfterRequest(String response, int statusCode);
    }

    protected URL url;
    protected String method;

    protected OnBeforeRequestListener onBeforeRequestListener = null;
    protected OnAfterRequestListener onAfterRequestListener = null;
    protected OnRequestFailedListener onRequestFailedListener = null;

    public final HashMap<String, Object> params = new HashMap<>();

    public Service() {

    }

    public URL getUrl() {
        return url;
    }

    public Service setUrl(String url) throws MalformedURLException {
        this.url = new URL(url);

        return this;
    }

    public String getMethod() {
        return method;
    }

    public Service setMethod(String method) {
        this.method = method;

        return this;
    }

    public Service addParameter(String name, String value) {
        this.params.put(name, value);

        return this;
    }

    public Service addParameter(String name, ArrayList<String> value) {
        this.params.put(name, value);

        return this;
    }

    public Service addParameter(String name, int value) {
        return addParameter(name, Integer.toString(value));
    }

    public Service addParameter(String name, boolean value) {
        return addParameter(name, (value ? "1" : "0"));
    }

    public Service addParameter(String name, CharSequence value) {
        return addParameter(name, value.toString());
    }

    public Service setOnBeforeRequestListener(OnBeforeRequestListener listener) {
        this.onBeforeRequestListener = listener;

        return this;
    }

    public Service setOnAfterRequestListener(OnAfterRequestListener listener) {
        this.onAfterRequestListener = listener;

        return this;
    }

    public Service setOnRequestFailedListener(OnRequestFailedListener listener) {
        this.onRequestFailedListener = listener;

        return this;
    }

    public void execute() {
        new AsyncTask<Void, Void, Void>() {
            String response = "";
            int statusCode = -1;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (onBeforeRequestListener != null) {
                    onBeforeRequestListener.onBeforeRequest();
                }
            }

            @Override
            protected Void doInBackground(Void... taskParams) {

                try {

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(getMethod());
                    connection.setUseCaches(false);
                    connection.setDefaultUseCaches(false);
                    connection.connect();

                    if (params.size() > 0) {
                        String data = new Gson().toJson(params);

                        OutputStream outputStream = connection.getOutputStream();
                        outputStream.write(data.getBytes("UTF-8"));
                    }

                    InputStream stream = new BufferedInputStream(connection.getInputStream());

                    StringBuilder sb = new StringBuilder();
                    for (int c; (c = stream.read()) >= 0; )
                        sb.append((char) c);

                    response = sb.toString();
                    statusCode = connection.getResponseCode();


                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (onAfterRequestListener != null && statusCode == HttpURLConnection.HTTP_OK) {
                    onAfterRequestListener.onAfterRequest(response, statusCode);
                } else if (onRequestFailedListener != null) {
                    onRequestFailedListener.onRequestFailed(response, statusCode);
                }
            }
        }.execute();
    }

    public static Service get(String url) throws MalformedURLException {
        return new Service().setMethod(Method.GET).setUrl(url);
    }

    public static Service post(String url) throws MalformedURLException {
        return new Service().setMethod(Method.POST).setUrl(url);
    }

    public static Service delete(String url) throws MalformedURLException {
        return new Service().setMethod(Method.DELETE).setUrl(url);
    }

    public static Service put(String url) throws MalformedURLException {
        return new Service().setMethod(Method.PUT).setUrl(url);
    }
}