import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.CookieStore;
import ch.boye.httpclientandroidlib.client.CredentialsProvider;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.config.RequestConfig;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpEntityEnclosingRequestBase;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.protocol.HttpClientContext;
import ch.boye.httpclientandroidlib.config.Registry;
import ch.boye.httpclientandroidlib.config.RegistryBuilder;
import ch.boye.httpclientandroidlib.conn.HttpClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.socket.ConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.socket.PlainConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.SSLConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.SSLContextBuilder;
import ch.boye.httpclientandroidlib.entity.ByteArrayEntity;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import ch.boye.httpclientandroidlib.impl.client.BasicCredentialsProvider;
import ch.boye.httpclientandroidlib.impl.client.DefaultRedirectStrategy;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;
import ch.boye.httpclientandroidlib.impl.conn.PoolingHttpClientConnectionManager;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

public class SslHttpStack implements HttpStack {

  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  public static final int TIMEOUT = 30000;

  private final String TAG = getClass().getSimpleName();

  private HttpClient mHttpClient;
  private HttpContext mHttpContext;
  private CookieStore mCookieStore;
  private boolean mIsAllowSelfSigned = false;

  public HttpClient getmHttpClient() {
	return mHttpClient;
}
  
  public SslHttpStack(boolean allowSelfSigned) {
    mCookieStore = new BasicCookieStore();
    mIsAllowSelfSigned = allowSelfSigned;

    RequestConfig requestConfig =
        RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
            .setSocketTimeout(TIMEOUT).setCircularRedirectsAllowed(true).build();

    HttpClientBuilder httpClientBuilder =
            HttpClientBuilder.create().setDefaultCookieStore(mCookieStore)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(requestConfig)
            .setRedirectStrategy(new DefaultRedirectStrategy() {
              public boolean isRedirected(HttpRequest request, ch.boye.httpclientandroidlib.HttpResponse response, HttpContext context)  {
                boolean isRedirect=false;
                try {
                  isRedirect = super.isRedirected(request, response, context);
                } catch (ProtocolException e) {
                  e.printStackTrace();
                }
                if (!isRedirect) {
                  int responseCode = response.getStatusLine().getStatusCode();
                  if (responseCode == 301 || responseCode == 302) {
                    return true;
                  }
                }
                return isRedirect;
              }
            });

    try {
      SSLContext sslContext;

      if (mIsAllowSelfSigned) {
        sslContext = SSLContext.getInstance("SSL");
        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
          @Override
          public X509Certificate[] getAcceptedIssuers() {
            Log.d(TAG,"getAcceptedIssuers =============");
            return null;
          }

          @Override
          public void checkClientTrusted(X509Certificate[] certs, String authType) {
            Log.d(TAG,"checkClientTrusted =============");
          }

          @Override
          public void checkServerTrusted(X509Certificate[] certs, String authType) {
            Log.d(TAG,"checkServerTrusted =============");
          }
        } }, new SecureRandom());
      } else {
        SSLContextBuilder builder = new SSLContextBuilder();
        sslContext = builder.useSSL().build();
      }

      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

      Registry<ConnectionSocketFactory> socketFactoryRegistry =
          RegistryBuilder.<ConnectionSocketFactory>create()
          .register("https", sslsf)
          .register("http", new PlainConnectionSocketFactory()).build();

      HttpClientConnectionManager httpConnMan =
          new PoolingHttpClientConnectionManager(socketFactoryRegistry);

      //sslContext commented out because it shouldn't be needed since we pass it
      //in the socketFactoryRegistry as part of the SSLConnectionSocketFactory
      httpClientBuilder.setConnectionManager(httpConnMan);

		// This will only supply the username and password is the server
		// prompts for it. You'll have to create a singleton for your Application.
		// You can leave out this section.
      if (MyApplication.getAppContext().getResources().getInteger(R.integer.auth_required) == 1) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("Username", "Password"));
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
      }

    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
    }

    mHttpClient = httpClientBuilder.build();
    mHttpContext = new BasicHttpContext();

    mHttpContext.setAttribute(HttpClientContext.COOKIE_STORE, mCookieStore);
  }

  @SuppressWarnings("unused")
  private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
    for (String key : headers.keySet()) {
      httpRequest.setHeader(key, headers.get(key));
    }
  }

  @SuppressWarnings("unused")
  private static List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
    List<NameValuePair> result = new ArrayList<NameValuePair>(postParams.size());
    for (String key : postParams.keySet()) {
      result.add(new BasicNameValuePair(key, postParams.get(key)));
    }
    return result;
  }

  @Override
  public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
      throws IOException, AuthFailureError {

    HttpUriRequest httpRequest = createHttpRequest(request, additionalHeaders);

    ch.boye.httpclientandroidlib.HttpResponse resp = mHttpClient.execute(httpRequest);
    return convertResponseNewToOld(resp);
  }

  private org.apache.http.HttpResponse convertResponseNewToOld(
      ch.boye.httpclientandroidlib.HttpResponse resp) throws IllegalStateException, IOException {

    ProtocolVersion protocolVersion =
        new ProtocolVersion(resp.getProtocolVersion().getProtocol(), resp.getProtocolVersion()
            .getMajor(), resp.getProtocolVersion().getMinor());

    StatusLine responseStatus =
        new BasicStatusLine(protocolVersion, resp.getStatusLine().getStatusCode(), resp
            .getStatusLine().getReasonPhrase());

    BasicHttpResponse response = new BasicHttpResponse(responseStatus);
    org.apache.http.HttpEntity ent = convertEntityNewToOld(resp.getEntity());
    response.setEntity(ent);

    for (Header h : resp.getAllHeaders()) {
      org.apache.http.Header header = convertHeaderNewToOld(h);
      response.addHeader(header);
    }

    return response;
  }

  private org.apache.http.HttpEntity convertEntityNewToOld(HttpEntity ent)
      throws IllegalStateException, IOException {

    if (ent != null) {
      BasicHttpEntity ret = new BasicHttpEntity();
      ret.setContent(ent.getContent());
      ret.setContentLength(ent.getContentLength());
      Header h;
      h = ent.getContentEncoding();
      if (h != null) {
        ret.setContentEncoding(convertHeaderNewToOld(h));
      }
      h = ent.getContentType();
      if (h != null) {
        ret.setContentType(convertHeaderNewToOld(h));
      }
      return ret;
    }

    return null;
  }

  private org.apache.http.Header convertHeaderNewToOld(Header header) {
    org.apache.http.Header ret = new BasicHeader(header.getName(), header.getValue());
    return ret;
  }

  /**
   * Creates the appropriate subclass of HttpUriRequest for passed in request.
   */
  @SuppressWarnings("deprecation")
  /* protected */static HttpUriRequest createHttpRequest(Request<?> request,
      Map<String, String> additionalHeaders) throws AuthFailureError {
    switch (request.getMethod()) {
      case Method.DEPRECATED_GET_OR_POST: {
        // This is the deprecated way that needs to be handled for backwards
        // compatibility.
        // If the request's post body is null, then the assumption is that
        // the request is
        // GET. Otherwise, it is assumed that the request is a POST.
        byte[] postBody = request.getPostBody();
        if (postBody != null) {
          HttpPost postRequest = new HttpPost(request.getUrl());
          postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
          HttpEntity entity;
          entity = new ByteArrayEntity(postBody);
          postRequest.setEntity(entity);
          return postRequest;
        } else {
          return new HttpGet(request.getUrl());
        }
      }
      case Method.GET:
        HttpGet getRequest = new HttpGet(request.getUrl());
        setHeaderFromRequest(getRequest, request);
        return getRequest;
      case Method.DELETE:
        HttpDelete delRequest = new HttpDelete(request.getUrl());
        setHeaderFromRequest(delRequest, request);
        return delRequest;
      case Method.POST: {
        HttpPost postRequest = new HttpPost(request.getUrl());
        postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
        setEntityIfNonEmptyBody(postRequest, request);
        setHeaderFromRequest(postRequest, request);
        return postRequest;
      }
      case Method.PUT: {
        HttpPut putRequest = new HttpPut(request.getUrl());
        putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
        setEntityIfNonEmptyBody(putRequest, request);
        setHeaderFromRequest(putRequest, request);
        return putRequest;
      }
      default:
        throw new IllegalStateException("Unknown request method.");
    }
  }

  private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
      Request<?> request) throws AuthFailureError {
    byte[] body = request.getBody();
    if (body != null) {
      HttpEntity entity = new ByteArrayEntity(body);
      httpRequest.setEntity(entity);
    }
  }

  private static void setHeaderFromRequest(HttpRequestBase base, Request<?> request) throws AuthFailureError {
    Map<String, String> getHeaders = request.getHeaders();
    if (!getHeaders.isEmpty()) {
      for (Entry<String, String> entry : getHeaders.entrySet()) {
        base.addHeader(entry.getKey(), entry.getValue());
      }
    }
  }

}
