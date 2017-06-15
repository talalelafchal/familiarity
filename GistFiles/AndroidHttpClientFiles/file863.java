
import android.net.http.HttpResponseCache;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import br.com.clairtonluz.interceptor.RewriteResponseInterceptor;
import br.com.clairtonluz.interceptor.RewriteResponseOffilineInterceptor;
import br.com.clairtonluz.util.App;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestFactory {
    public static final String API_BASE_URL = "http://blog.clairtonluz.com.br/api/";

    private static Cache cache;

  // Esse metodo deve ser chamado no onCreate da sua MainActivity
    public static void carregarCache() {
        try {
            File httpCacheDir = new File(App.getContext().getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            cache = new Cache(httpCacheDir, httpCacheSize);
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("App", "HTTP response cache installation failed:", e);
        }
    }

// Esse metodo deve ser chamado no onStop da sua MainActivity
    public static void armazenarCache() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private static Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));


    public static Cache getCache() {
        return cache;
    }

    public static Retrofit builder() {
        return builder.build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }


        OkHttpClient client = httpClient.cache(getCache())
                .addNetworkInterceptor(new RewriteResponseInterceptor())
                .addInterceptor(new RewriteResponseOffilineInterceptor())
                .build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}