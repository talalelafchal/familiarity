public class DemoServiceAPI {

    public static final String API_BASE_URL = "http://ecommercev1.herokuapp.com/";
    public static DemoServiceAPI _instance;

    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static DemoServiceAPI getInstance() {
        if (_instance == null) {
            _instance = new DemoServiceAPI();
        }
        return _instance;
    }

    private static Retrofit.Builder builderNormal =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public <S> S createService(Class<S> serviceClass) {
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builderNormal.client(client).build();
        return retrofit.create(serviceClass);
    }

}
