package com.example.sampleusamao;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public final class HttpClient {
    private static final String TAG = HttpClient.class.getSimpleName();

    public static OkHttpClient getApiHttpClient() {
        return Holder.apiHttpClient;
    }

    public static OkHttpClient getImageHttpClient() {
        return Holder.imageHttpClient;
    }

    private static class Holder {
        private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();

        private static final OkHttpClient apiHttpClient;

        static {
            File httpCacheDirectory = new File(App.getContext().getCacheDir(), "responses");
            Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024); // 10MB
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "max-age=3600") // 1hour
                            .removeHeader("pragma")
                            .build();
                }
            };
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            Log.d(TAG, message);
                        }
                    });
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC :
                    HttpLoggingInterceptor.Level.NONE);
            apiHttpClient = httpClient.newBuilder()
                    .cache(cache)
                    .addNetworkInterceptor(interceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();
        }

        private static final OkHttpClient imageHttpClient;

        static {
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            Log.d(TAG, message);
                        }
                    });
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC :
                    HttpLoggingInterceptor.Level.NONE);
            imageHttpClient = httpClient.newBuilder()
                    .addInterceptor(loggingInterceptor)
                    .build();
        }
    }

    private HttpClient() {
    }
}
