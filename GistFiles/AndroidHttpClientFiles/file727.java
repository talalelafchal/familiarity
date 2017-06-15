package com.github.welingtonveiga.mensageiro.util;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Interceptor;
import android.util.Log;


public class HTTPClient {

    private final OkHttpClient client;

    public HTTPClient() {
        client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    @NonNull
    public String get(String uri) {
        Response response = null;
        String body;
        try {
            Request request = new Request.Builder()
                    .url(uri)
                    .header("Content-Type", "application/json")
                    .build();
            response = client.newCall(request).execute();
            body = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
        return body;
    }

    public String post(String uri, String data) {
        Response response = null;
        String body;
        try {
            Request request = new Request.Builder()
                    .url(uri)
                    .post(RequestBody.create(MediaType.parse("application/json"), data))
                    .build();
            response = client.newCall(request).execute();
            body = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
        return body;
    }
  
  private static class LoggingInterceptor implements Interceptor {

    private static final String TAG = LoggingInterceptor.class.getName();

    @Override 
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.i(TAG, String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.i(TAG, String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}
}