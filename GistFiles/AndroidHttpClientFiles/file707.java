// see http://developer.android.com/intl/ru/training/articles/security-ssl.html#UnknownCa
// copy ssl certificate: echo -n | openssl s_client -connect server:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > ./res/raw/cert.crt
public class ApiBuilder {

    public static IServerApi getServerApi() {
        
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        
        try {
            Context context = App.getContext();
            
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.cert);
            Certificate ca = cf.generateCertificate(caInput);

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            builder.sslSocketFactory(sslContext.getSocketFactory());
            builder.hostnameVerifier((hostname, session) -> true);
        } finally {
            caInput.close();
        }

        OkHttpClient httpClient = builder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())       // or something else
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // or something else
                .baseUrl(BuildConfig.BASE_URL)
                .client(httpClient);

        return retrofitBuilder.build().create(IServerApi.class);        
    }
}
