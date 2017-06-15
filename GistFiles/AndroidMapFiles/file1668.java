public class DefaultImplementation {
  /*There are 2 implementations, the first is using the Retrofit default callback and the second one is done by
  controlling the thread using an AsyncTask. Most of the times, a login can be done with a simple default callback,
  but more heavy gets should be done in the background. If for any reason you want to write objects in the Android database,
  you have to use the AsyncTask, otherwise UI will freeze*/

  public void postUsingDefaultCallback(String firstField, String secondField) {
        Requests request = Interceptors.getInterceptor().theMostBasicInterceptor();
        Call<SomeModel> call = request.postRequest(firstField, secondField);
        call.enqueue(new Callback<SomeModel>() {
            @Override
            public void onResponse(Call<SomeModel> call, Response<SomeModel> response) {
                int code = response.code();
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
              /*If you are doing this inside an activity then onFailure and onResponse can access the UI.
              If you are doing this in another class that is noat an activity neither a fragment, then pass
              an interface in the constructor of the class so you can deliver result back*/
            }
        });
    }
}