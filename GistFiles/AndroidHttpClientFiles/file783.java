
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HTTP {

    private static final int CONN_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 30000;
    private static final Logger logger = Logger.getLogger(HTTP.class.getName());
    private HashMap<String, String> headers = new HashMap<>();

    // START REQUIRE IMPROVEMENTS
    private CookieManager cookieManager;

    public HTTP(){
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    private long lastResponseTime = Long.MIN_VALUE;
    public long getLastResponseTime() {
        return lastResponseTime;
    }
    public String get(URI uri) throws IOException { return get(uri.toURL()); }
    protected String get(URI uri, Map<String, String> data) throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append(uri.toString()).append('?');
        for (Map.Entry<String, String> pair : data.entrySet()) {
            if (sb.length() > 0)
                sb.append('&');

            sb.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return get( new URL( sb.toString() ) );
    }

    public void setForgiveCookies(boolean forgive) {
        cookieManager.setCookiePolicy( forgive ? CookiePolicy.ACCEPT_NONE : CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }
    private boolean autoRedirectEnabled = true;
    protected void setAutoRedirectEnabled(boolean autoRedirectEnabled) {
        this.autoRedirectEnabled = autoRedirectEnabled;
    }
    public boolean isAutoRedirectEnabled() {
        return autoRedirectEnabled;
    }
    // END REQUIRE IMPROVEMENTS




    static {
        if (Boolean.getBoolean("sun.net.http.errorstream.enableBuffering") == false) {
            logger.config("System property \"sun.net.http.errorstream.enableBuffering\" is not set to true, this will cause issues");
        }
    }

    private boolean throwExceptions;

    /**
     * Throws exceptions if server responds with error code >= 400
     *
     * @param throwExceptions
     */
    public void setThrowExceptions(boolean throwExceptions) {
        this.throwExceptions = throwExceptions;
    }

    public boolean isThrowingExceptions() {
        return throwExceptions;
    }

    public void setKeepAlive(boolean keepAlive) {
        setHeader("Connection", keepAlive ? "keep-alive" : null);
    }

    public void setHeader(String name, String value) {
        if (value == null) headers.remove(name);
        else headers.put(name, value);
    }

    protected URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public String delete(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setRequestMethod("DELETE");
        return executeAndGetResponse(urlConnection);
    }



    public String get(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        applyHeaders(urlConnection);
        return executeAndGetResponse(urlConnection);
    }

    public void download(URL url, File targetFile) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        applyHeaders(urlConnection);
        FileOutputStream baos = new FileOutputStream(targetFile);
        execute(urlConnection, baos);
        baos.flush();
        baos.close();
    }

    public String upload(URL url, String contentType, InputStream stream) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        applyHeaders(urlConnection);
        urlConnection.setRequestMethod("POST");
        if (contentType != null) {
            urlConnection.setRequestProperty("Content-Type", contentType);
        }
        writeContent(urlConnection, stream);

        return executeAndGetResponse(urlConnection);
    }

    public String post(URL url, List<AbstractMap.SimpleEntry> params) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        urlConnection.setRequestProperty("Accept", "application/json");
        applyHeaders(urlConnection);

        StringBuilder sb = new StringBuilder();
        for (AbstractMap.SimpleEntry<String, String> pair : params) {
            if (sb.length() > 0)
                sb.append('&');

            sb.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        writeContent(urlConnection, sb.toString());
        return executeAndGetResponse(urlConnection);
    }

    public String postJson(URL url, String jsonData) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        urlConnection.setRequestProperty("Accept", "application/json");
        applyHeaders(urlConnection);
        writeContent(urlConnection, jsonData);

        return executeAndGetResponse(urlConnection);
    }

    protected String putJson(URL url, String jsonData) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        applyHeaders(urlConnection);
        urlConnection.setRequestMethod("PUT");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        urlConnection.setRequestProperty("Accept", "application/json");
        writeContent(urlConnection, jsonData);

        return executeAndGetResponse(urlConnection);
    }

    private void writeContent(HttpURLConnection urlConnection, String data) throws IOException {
        urlConnection.setInstanceFollowRedirects( autoRedirectEnabled );
        logger.finest(data);
        byte[] bytes = data.getBytes("UTF-8");
        urlConnection.setDoOutput(true);
        urlConnection.setFixedLengthStreamingMode(bytes.length);
        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
        out.write(bytes);
        out.flush();
        out.close();
    }

    private void writeContent(HttpURLConnection urlConnection, InputStream data) throws IOException {
        urlConnection.setInstanceFollowRedirects( autoRedirectEnabled );
        urlConnection.setDoOutput(true);
        urlConnection.setFixedLengthStreamingMode(data.available());
        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
        byte[] bytes = new byte[512];
        int readBytes;
        while ((readBytes = data.read(bytes)) > 0) {
            out.write(bytes, 0, readBytes);
        }
        out.flush();
        out.close();
    }

    protected String executeAndGetResponse(HttpURLConnection request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        execute(request, baos);
        baos.flush();
        baos.close();
        String ret = baos.toString("UTF-8");
        logger.finest(ret);
        return ret;
        // return new String(baos.toByteArray(), "UTF-8");
    }

    protected void execute(HttpURLConnection connection, OutputStream stream) throws IOException {
        logger.finest(connection.getRequestMethod() + " " + connection.getURL() + " " + connection.getResponseCode()
                + " " + connection.getResponseMessage());
        connection.setInstanceFollowRedirects( autoRedirectEnabled );
        int status = connection.getResponseCode();

        if (throwExceptions && status >= 400) {
            throw new IOException(
                    "Server has responded with " + status + " " + connection.getResponseMessage());
        }

        InputStream input;
//        if (status >= 400)
//            input = connection.getErrorStream();
//        else
//            input = connection.getInputStream();
        if (status >= 200 && status <= 299) {
            input = connection.getInputStream();
        } else {
            input = connection.getErrorStream();
        }
        if (input == null) return;

        if (stream != null) {
            byte[] buffer = new byte[512];
            int byteLetti;
            while ((byteLetti = input.read(buffer)) > 0) {
                stream.write(buffer, 0, byteLetti);
            }
        }
        input.close();
        lastResponseTime = System.currentTimeMillis();
    }

    protected void applyHeaders(HttpURLConnection request) {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }
}