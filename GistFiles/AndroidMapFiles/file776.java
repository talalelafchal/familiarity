public interface Requests {

    @FormUrlEncoded
    @POST("some_relative_url")
    Call<SomeModel> postRequest(@Field("first_field") String firstField, @Field("secondField") String secondField);
    
    @GET("another_relative_url")
    Call<ArrayOfModel[]> get(@QueryMap Map<String, String> queryMap);
    
    @POST("relative/{THIS_IS_A_DYNAMIC_PARAMETER}/relative_again")
    Call<TheModel> post(@Path("THIS_IS_A_DYNAMIC_PARAMETER") long theDynamicParameter);
    
    @FormUrlEncoded
    @PUT("relative/url")
    Call<SomeModel>  put(@Field("name_of_the_field") String nameOfTheField);

}