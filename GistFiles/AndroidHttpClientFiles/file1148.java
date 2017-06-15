public clas AsyncImplementation extends AsyncTask<Void, Void, Void> {
  
  /*Please read the rest of the explanation in the DefaultImplementation.java file. The difference between a default http request
  and one using an AsyncTask is the enqeue() or the .execute() method*/
  
  /*If you are passing params to the url, like it would be the case of the method post(long theDynamicParameter) you can
  do it using replacing the void in the AsyncTask, in this case we are passing a Map so is passed in the constructor. Some times,
  you would want the AsyncTask solve all the logic, then implements methods here to do it. Create the request http in a loop.
  Use getter and setter to extends this to another class, etc.
  Now in activity you can new AsyncImplementation(map).execute();*/
  
  private Map<String, String> map;
  
  public AsyncImplementation(Map<String, String> map) {
    this.map = map;
  }
  
  @Override
    protected Integer doInBackground(Void... voids) {
      Requests request = Interceptors.getInterceptor().aCommonGetInterceptor();
        Call<ArrayOfModel[]> call = request.get(map);
        try {
            Response<ArrayOfModel[]> response = call.execute();
            /*You have your response do what ever you want. Tip:*/
            ArrayOfModel[] arrayOfModel = response.body();
        } catch (IOException e) {
            /*Something went wrong*/
        }
        return null;
    }
  
  
}