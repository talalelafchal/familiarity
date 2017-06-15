import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class RestClient {
	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int SOCKET_TIMEOUT = 10000;

    private String baseUrl;
    private AbstractHttpClient httpClient;
    
    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        setupHttpClient();
    }
    
    private void setupHttpClient() {
    	HttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
    	
    	httpClient = new DefaultHttpClient(httpParams);
    	httpClient.addRequestInterceptor(new GzipHttpRequestInterceptor());
        httpClient.addResponseInterceptor(new GzipHttpResponseInterceptor());
    }
    
    public Response get(Request req) throws Exception {
        return request(req, new HttpGet());
    }
    
    public Response head(Request req) throws Exception {
        return request(req, new HttpHead());
    }
    
    public Response delete(Request req) throws Exception {
        return request(req, new HttpDelete());
    }
    
    public Response post(Request req) throws Exception {
    	return entityEnclosingRequest(req, new HttpPost());
    }
    
    public Response put(Request req) throws Exception {
        return entityEnclosingRequest(req, new HttpPut());
    }
    
    private Response request(Request r, HttpRequestBase request) throws Exception {
        String url = baseUrl + r.getResource() + serializeUrlParams(r.getParams());
        request.setURI(new URI(url));

        for(NameValuePair h : r.getHeaders())
        	request.addHeader(h.getName(), h.getValue());

        return executeRequest(request, url);
    }
    
    private Response entityEnclosingRequest(Request r, HttpEntityEnclosingRequestBase request) throws Exception {
    	String url = baseUrl + r.getResource();
    	request.setURI(new URI(url));

        for(NameValuePair h : r.getHeaders())
            request.addHeader(h.getName(), h.getValue());

        if(!r.getParams().isEmpty())
            request.setEntity(new UrlEncodedFormEntity(r.getParams(), HTTP.UTF_8));

        return executeRequest(request, url);
    }

    private Response executeRequest(HttpUriRequest request, String url) {
        HttpResponse httpResponse;
        Response response = new Response();

        try {
            httpResponse = httpClient.execute(request);

            response.setUrl(url);
            response.setMessage(httpResponse.getStatusLine().getReasonPhrase());
            response.setResponseCode(httpResponse.getStatusLine().getStatusCode());

            HttpEntity entity = httpResponse.getEntity();
            
            if (entity != null) {
                InputStream instream = entity.getContent();
                response.setResponse(convertStreamToString(instream));
                
                // Closing the input stream will trigger connection release
                instream.close();
            }
        } catch (ClientProtocolException e)  {
        	httpClient.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
        	httpClient.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        
        return response;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    private String serializeUrlParams(ArrayList<NameValuePair> params) throws UnsupportedEncodingException {
    	String sParams = "";
        for (int i=0; i < params.size(); i++) {
        	sParams += (i == 0) ? "?" : "&";
        	sParams += params.get(i).getName() + "=" + URLEncoder.encode(params.get(i).getValue(), "UTF-8");
        }
        return sParams;
    }
    
    private class GzipHttpRequestInterceptor implements HttpRequestInterceptor {
        public void process(final HttpRequest request, final HttpContext context) {
            if (!request.containsHeader("Accept-Encoding")) {
                request.addHeader("Accept-Encoding", "gzip");
            }
        }
    }

    private class GzipHttpResponseInterceptor implements HttpResponseInterceptor {
        public void process(final HttpResponse response, final HttpContext context) {
            final HttpEntity entity = response.getEntity();
            final Header encoding = entity.getContentEncoding();
            if (encoding != null) {
                inflateGzip(response, encoding);
            }
        }

        private void inflateGzip(final HttpResponse response, final Header encoding) {
            for (HeaderElement element : encoding.getElements()) {
                if (element.getName().equalsIgnoreCase("gzip")) {
                    response.setEntity(new GzipInflatingEntity(response.getEntity()));
                    break;
                }
            }
        }
    }

    private class GzipInflatingEntity extends HttpEntityWrapper {
        public GzipInflatingEntity(final HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}