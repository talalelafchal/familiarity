package com.android.handsfree.data;


import com.android.handsfree.BuildConfig;
import com.android.handsfree.presentation.DefaultSubsriber;
import com.android.handsfree.data.error_handling.RxErrorHandlingCallAdapterFactory;
import com.android.handsfree.data.models.Token;
import com.android.handsfree.domain.interactors.GetTokenUseCase;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public final class RestClientRetrofit implements RestClient {

    private Retrofit retrofit;
    private GetTokenUseCase tokenUseCase;

    @Inject
    public RestClientRetrofit(GetTokenUseCase tokenUseCase) {
        this.tokenUseCase = tokenUseCase;
        init();
    }

    private void init() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.LOG_ENABLED)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new TokenInterceptor(tokenUseCase))
                .build();

        this.retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(RestAPI.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
    }

    @Override
    public <T> T create(final Class<T> clazz) {
        return retrofit.create(clazz);
    }

    private static class TokenInterceptor implements Interceptor {
        private GetTokenUseCase tokenUseCase;
        private String token;

        TokenInterceptor(GetTokenUseCase tokenUseCase) {
            this.tokenUseCase = tokenUseCase;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            initToken();

            Request request = chain.request();
            request = request.newBuilder()
                    .addHeader("Authorization","Bearer "+token)
                    .addHeader("Content-Type","application/json")
                    .build();
            return chain.proceed(request);
        }

        private void initToken(){
            tokenUseCase.executeSync(new DefaultSubsriber<Token>(){
                @Override
                public void onNext(Token token1) {
                  token = token1.getToken();
                }
            });
        }
    }
}
