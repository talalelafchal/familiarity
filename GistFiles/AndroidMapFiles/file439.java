import org.codehaus.jackson.map.ObjectMapper;

public class HttpUtils {

        private static final int SERVER_PORT = 80;
        private static final String SERVER_IP = "myapp.appspot.com"; // use 10.0.2.2 for emulator

        private static HttpUtils instance = new HttpUtils();
        private DefaultHttpClient client;
        private ResponseHandler<String> responseHandler;
        private ObjectMapper mapper;

        private HttpUtils() {
                super();
                client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 30000);
                responseHandler = new BasicResponseHandler();
                mapper = new ObjectMapper(); // can reuse, share globally
        }

        public static HttpUtils getInstance() {
                return instance;
        }
        
        public String doGet(String path) throws IOException {
                return doGet(path, null);
        }

        public String doGet(String path, String query) throws IOException {
                try {
                        URI uri;
                        uri = createURI(path, query);
                        HttpGet get = new HttpGet(uri);
                        HttpResponse response = client.execute(get);
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == HttpStatus.SC_OK) {
                                return responseHandler.handleResponse(response);
                        } else {
                                throw new IOException("wrong http status: " + statusCode);
                        }
                } catch (URISyntaxException e) {
                        throw new IOException("uri syntax error");
                } catch (ClientProtocolException e) {
                        throw new IOException("protocol error");
                } 
                
        }

        private URI createURI(String path, String query) throws URISyntaxException {
                return URIUtils.createURI("http", SERVER_IP, SERVER_PORT, "rest/" + path, query, null);
        }

        public boolean doPut(String path, Object object) throws IOException {
                try {
                        String json = mapper.writeValueAsString(object);
                        URI uri = createURI(path, null);
                        HttpPut put = new HttpPut(uri);
                        put.addHeader("Accept", "application/json");
                        put.addHeader("Content-Type", "application/json");
                        StringEntity entity = new StringEntity(json, "UTF-8");
                        entity.setContentType("application/json");
                        put.setEntity(entity);
                        HttpResponse response = client.execute(put);
                        int statusCode = response.getStatusLine().getStatusCode();
                        return statusCode == HttpStatus.SC_OK;
                } catch (URISyntaxException e) {
                        throw new IOException("uri syntax error");
                } catch (ClientProtocolException e) {
                        throw new IOException("protocol error");
                }
        }

        public String doPutFile(final String path, final File file) throws URISyntaxException, HttpException,
                        IOException {
                URI uri = createURI(path, null);
                HttpPut put = new HttpPut(uri);
                String mimeType = "binary/octet-stream";
                if(file.getName().matches(".*\\.(jpeg|jpg)"))
                        mimeType = "image/jpeg";
                FileEntity reqEntity = new FileEntity(file, mimeType);
                put.setEntity(reqEntity);
                HttpResponse response = client.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                        return responseHandler.handleResponse(response);
                } else {
                        throw new IOException("wrong http status: " + statusCode);
                }
        }

        public String doPost(final String path, final String POSTText) throws URISyntaxException, HttpException,
                        IOException {
                URI uri = createURI(path, null);
                HttpPost httpPost = new HttpPost(uri);
                StringEntity entity = new StringEntity(POSTText, "UTF-8");
                BasicHeader basicHeader = new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
                httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
                entity.setContentType(basicHeader);
                httpPost.setEntity(entity);
                HttpResponse response = client.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                        return responseHandler.handleResponse(response);
                } else {
                        throw new IOException("wrong http status: " + statusCode);
                }
        }

        public boolean doDelete(final String path) throws HttpException, IOException, URISyntaxException {
                URI uri = createURI(path, null);
                HttpDelete httpDelete = new HttpDelete(uri);
                httpDelete.addHeader("Accept", "text/html, image/jpeg, *; q=.2, */*; q=.2");
                HttpResponse response = client.execute(httpDelete);
                int statusCode = response.getStatusLine().getStatusCode();
                return statusCode == HttpStatus.SC_OK ? true : false;
        }

}