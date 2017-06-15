public class cdlbomprincipio extends Application {

  @Override
  public void onCreate() {
      super.onCreate();
  
      String api_key = "xxxxxxxxxx";
      String secret_key = "xxxxxxxxxx";
  
      Kumulos.initWithAPIKeyAndSecretKey(api_key, secret_key, getApplicationContext());
  }
}


