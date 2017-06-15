public interface SignUpAPI {

    @Headers("Content-Type: application/json")
    @POST("/api/users")
    Call<RootObjectResponse> signUp(
            @Body RootObjectRequest request
    );

}