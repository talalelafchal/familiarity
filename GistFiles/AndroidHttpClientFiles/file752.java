public class Interceptors {
  
  private static final String ENDPOINT = "http://www.yourfancyurl.com/can/have/more_url/";
  
  /*If you are gonn use this a lot, then the class should be a Singleton, creating the interceptor is a heavy process reuse it*/
  private static Interceptors ourInstance = new Interceptors();

  public static Interceptors getInstance() {
      return ourInstance;
  }

  private Inteceptors() {
  }
  
  public Requests theMostBasicInterceptor() {
    Retrofit interceptor = new Retrofit.Builder()
            .baseUrl(ENDPOINT)
            /*Never forget about adding the converter, otherwise you can not parse the data*/
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    Requests someRequest = interceptor.create(Requests.class);
    return someRequest;
  }
  
  public Request basicVariation() {
    //Same basic interceptor than above, but this time we add a longer wait before time out, in case our server is slow
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS);

    OkHttpClient client = httpClient.build();

    Retrofit interceptor = new Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

    Requests request = interceptor.create(Requests.class);
    return request;
  }
  
  public Requests aCommonGetInterceptor() {
    /*This is very common in gets cause increase the response time wait and add headers and does retrys*/
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS);

    httpClient.addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            Request request = originalRequest.newBuilder()
                    /*Common headers*/
                    .header("authtoken", "YOUR_AUTHTOKEN_REPLACE_THIS")
                    .header("Accept", "application/json")
                    /*Custom header*/
                    .header("Flavor", "mint")
                    .build();

            Response response = chain.proceed(request);
            
            /*If the request fail then you get 3 retrys*/
            int retryCount = 0;
            while (!response.isSuccessful() && retryCount < 3) {
                retryCount++;
                response = chain.proceed(request);
            }

            return response;
        }
    });

    OkHttpClient client = httpClient.build();

    Retrofit interceptor = new Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

    Request request = interceptor.create(Requests.class);

    return request;
  }
  
  public Requests commonPostInterceptor() {
    /*Mostly the same of what is done with post, but this time the waiting time for response after post is increase
    and, very important, there are no retry. Here there is a 1 min waiting period, if for any reason the server did
    got processed the request but took 1 min and 1 sec to response, you dont want to retry cause it would create
    another object duplicated. One min waiting time for a server is a lot, it should work with this basis. If it doesnt
    then dont make it worse by doing retry*/
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS);

    httpClient.addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("authtoken", "YOUR_AUTHTOKEN_REPLACE_THIS")
                    .header("Music", "loud")
                    .build();

            Response response = chain.proceed(request);

            return response;
        }
    });

    OkHttpClient client = httpClient.build();

    Retrofit interceptor = new Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

    Requests service = interceptor.create(Requests.class);
    return service;
  }
  
}