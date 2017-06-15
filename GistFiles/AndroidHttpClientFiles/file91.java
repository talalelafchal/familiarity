package com.demo.okhttpissue;

import android.test.InstrumentationTestCase;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MockWebServerIssueTest extends InstrumentationTestCase {
    private MockWebServer mockWebServer;
    private OkHttpClient httpClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockWebServer = new MockWebServer();
        httpClient = new OkHttpClient();
        httpClient.setReadTimeout(10, TimeUnit.SECONDS);
    }

    @Override
    protected void tearDown() throws Exception {
        mockWebServer.shutdown();
        super.tearDown();
    }

    public void testInvalidContentLengthResultsInTimeout() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("ABC").clearHeaders().addHeader("Content-Length: 4"));
        mockWebServer.play();

        final StringBuilder testLog = new StringBuilder();
        final AtomicBoolean shouldFailTest = new AtomicBoolean(true);
        final CountDownLatch lock = new CountDownLatch(1);

        Request request = new Request.Builder().url(mockWebServer.getUrl("/fake")).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException ex) {
                // NEVER CALLED even though response.body().string() will throw an IOException
                
                testLog.append("- onFailure start\n");

                if (ex instanceof ProtocolException && "unexpected end of stream".equals(ex.getMessage())) {
                    shouldFailTest.set(false);
                }

                testLog.append("- onFailure end\n");
                lock.countDown();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                testLog.append("- onResponse start...\n");

                response.body().string();  // Should generate an unexpected end of stream condition
                // the thrown IOException is swallowed forever in Call.java:174 (https://github.com/square/okhttp/blob/parent-2.1.0/okhttp/src/main/java/com/squareup/okhttp/Call.java)

                testLog.append("- onResponse end...\n");
                lock.countDown();
            }
        });

        testLog.append("- waitForUnlock started...\n");
        lock.await(15, TimeUnit.SECONDS);
        testLog.append("- waitForUnlock ended...\n");

        if (shouldFailTest.get()) {
            fail("Log: \n" + testLog.toString());
        }
    }
}
