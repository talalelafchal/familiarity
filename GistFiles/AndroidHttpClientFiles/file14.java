public OkHttpClient provideOkHTTPClient() {
    final OkHttpClient okHttpClient = new OkHttpClient();
    if (BuildConfig.DEBUG) {
        okHttpClient.interceptors()
                .add(new LoggingInterceptor(Clock.systemDefaultZone(), LoggingInterceptor.LogLevel.BASIC));
    }
    return okHttpClient;
}