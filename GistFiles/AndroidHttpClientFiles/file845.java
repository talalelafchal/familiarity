package com.yume190.tw.test;

import android.os.Environment;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.maxwin.busstop.advance.YumeFile;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.HttpResponseGenerator;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.HttpEntityStub;
import org.robolectric.tester.org.apache.http.RequestMatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class Temp2Test  extends TestCase {

    HttpResponse successResponse;
    HttpResponse failResponse;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        ProtocolVersion httpProtocolVersion = new ProtocolVersion("HTTP", 1, 1);

        successResponse = new BasicHttpResponse(httpProtocolVersion, 200, "OK");
        successResponse.setEntity(new StringEntity("Yume S"));

        failResponse = new BasicHttpResponse(httpProtocolVersion, 404, "Not Found");
        failResponse.setEntity(new StringEntity("Yume F"));
    }

    @After
    public void tearDown() throws Exception {
        Robolectric.clearHttpResponseRules();
        Robolectric.clearPendingHttpResponses();
        super.tearDown();
    }

    @Test
    public void testHttpResponseRule1() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/test1.html";

        Robolectric.addHttpResponseRule("GET",url,successResponse);

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testHttpResponseRule2() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/test2.html";

        Robolectric.addHttpResponseRule(url,successResponse);

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testHttpResponseRule3() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/test3.html";

        Robolectric.addHttpResponseRule(url,"Yume S");

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testHttpResponseRule4() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/test4.html";

        RequestMatcher requestMatcher = new FakeHttpLayer.DefaultRequestMatcher("GET","http://www.test.com/test4.html");
        Robolectric.addHttpResponseRule(requestMatcher,successResponse);

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testHttpResponseRule5() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/test5.html";

        RequestMatcher requestMatcher = new FakeHttpLayer.RequestMatcherBuilder()
                                                .host("www.test.com")
                                                .path("test5.html")
                                                .method("GET");
        List<HttpResponse> list = new ArrayList<>();
        list.add(successResponse);
        list.add(failResponse);
        Robolectric.addHttpResponseRule(requestMatcher,list);

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        response = client.execute(request);
        assertEquals(404,response.getStatusLine().getStatusCode());
        assertEquals("Yume F", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testPendingHttpResponse1() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/testP1.html";

        Robolectric.addPendingHttpResponse(successResponse);
        Robolectric.addPendingHttpResponse(failResponse);
        Robolectric.addPendingHttpResponse(successResponse);

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        response = client.execute(request);
        assertEquals(404,response.getStatusLine().getStatusCode());
        assertEquals("Yume F", EntityUtils.toString(response.getEntity()));

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testPendingHttpResponse2() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/testP2.html";

        HttpResponseGenerator successHttpResponseGenerator = new HttpResponseGenerator() {
            @Override
            public HttpResponse getResponse(HttpRequest request) {
                return successResponse;
            }
        };
        HttpResponseGenerator failHttpResponseGenerator = new HttpResponseGenerator() {
            @Override
            public HttpResponse getResponse(HttpRequest request) {
                return failResponse;
            }
        };

        Robolectric.addPendingHttpResponse(successHttpResponseGenerator);
        Robolectric.addPendingHttpResponse(failHttpResponseGenerator);
        Robolectric.addPendingHttpResponse(successHttpResponseGenerator);

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        response = client.execute(request);
        assertEquals(404,response.getStatusLine().getStatusCode());
        assertEquals("Yume F", EntityUtils.toString(response.getEntity()));

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testPendingHttpResponse3() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/testP3.html";

        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Yume","Dream");

        Robolectric.addPendingHttpResponse(200,"Yume S",headers);
        Robolectric.addPendingHttpResponse(404,"Yume F",headers);
        Robolectric.addPendingHttpResponse(200,"Yume S",headers);

        assertFalse(Robolectric.httpRequestWasMade());
        assertFalse(Robolectric.httpRequestWasMade(url));

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        response = client.execute(request);
        assertEquals(404,response.getStatusLine().getStatusCode());
        assertEquals("Yume F", EntityUtils.toString(response.getEntity()));

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testHttpResponseRule6DefaultHttpClient() throws Exception {
        HttpClient client = new DefaultHttpClient();
        String url = "http://www.test.com/test6.html";

        Robolectric.getFakeHttpLayer().addHttpResponseRule(new MyResponseRule());

        URI uri = new URI( url);
        HttpGet request = new HttpGet(uri);
        HttpResponse response;

        response = client.execute(request);
        assertEquals(200,response.getStatusLine().getStatusCode());
        assertEquals("Yume S", EntityUtils.toString(response.getEntity()));

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testHttpResponseRule6SyncHttpClientSuccess() throws Exception {
        String url = "http://www.test.com/test6.html";

        Robolectric.getFakeHttpLayer().addHttpResponseRule(new MyResponseRule());

        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.get(url,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String decoded = new String(responseBody, "UTF-8");
                    assertEquals(200,statusCode);
                    assertEquals("Yume S", decoded);
                } catch (UnsupportedEncodingException e) {
                    fail("never happen");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                fail("never happen");
            }
        });

        assertTrue(Robolectric.httpRequestWasMade());
        assertTrue(Robolectric.httpRequestWasMade(url));
    }

    @Test
    public void testHttpResponseRule6SyncHttpClientFail() throws Exception {
        String url = "http://www.test.com/test7.html";

        Robolectric.getFakeHttpLayer().addHttpResponseRule(new MyResponseRule());

        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.get(url,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                fail("never happen");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.print("Request Fail Happen");
            }
        });
    }

    @Test
    public void testDownloadFile1() throws Exception {
        String url = "http://www.test.com/test.js";

        HttpResponse downloadResponse = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        InputStream is = Robolectric.getShadowApplication().getAssets().open("test/async_data_finder_factory.js");
        downloadResponse.setEntity(new InputStreamEntity(is,737));
        Robolectric.addHttpResponseRule(url,downloadResponse);

        String folderName = Robolectric.getShadowApplication().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        YumeFile folder = new YumeFile(folderName);
        folder.mkdirs();
        YumeFile file = new YumeFile(folderName + "/" + "b.js");

        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.get(url,new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                fail("never happen");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                YumeFile yumeFile = new YumeFile(file.getAbsolutePath());
                assertEquals(yumeFile.checksum(),"065710dfd1e9419deb9507d2e7fdaa98");
                assertEquals(737,file.length());
            }
        });

        File externalFileFolder = Robolectric.getShadowApplication().getExternalFilesDir("").getParentFile();
        externalFileFolder.delete();
    }

    @Test
    public void testDownloadFile2() throws Exception {
        String url = "http://www.test.com/test.ttf";

        HttpResponse downloadResponse = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        InputStream is = Robolectric.getShadowApplication().getAssets().open("test/RobotoCondensed-Bold.ttf");
        downloadResponse.setEntity(new InputStreamEntity(is,737));
        Robolectric.addHttpResponseRule(url,downloadResponse);

        String folderName = Robolectric.getShadowApplication().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        YumeFile folder = new YumeFile(folderName);
        folder.mkdirs();
        YumeFile file = new YumeFile(folderName + "/" + "c.ttf");

        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.get(url,new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                fail("never happen");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                YumeFile yumeFile = new YumeFile(file.getAbsolutePath());
                assertEquals("52f9b35f9f7cfa1be2644bcbac61a983",yumeFile.checksum());
                assertEquals(141796,file.length());
            }
        });

        File externalFileFolder = Robolectric.getShadowApplication().getExternalFilesDir("").getParentFile();
        externalFileFolder.delete();
    }


    class MyResponseRule implements HttpEntityStub.ResponseRule {
        @Override
        public HttpResponse getResponse() throws HttpException, IOException {
            HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");

            response.addHeader(new BasicHeader("Yume","Dream"));

            response.setEntity(new StringEntity("Yume S"));

            return response;
        }

        @Override
        public boolean matches(HttpRequest request) {
            String uri = request.getRequestLine().getUri();
            if (uri.equals("http://www.test.com/test6.html"))
                return true;
            return false;
        }
    }

}