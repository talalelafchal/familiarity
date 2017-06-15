public class DemoActivity extends AppCompatActivity {
    private final String TAG = "DemoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        //create user to sign up
        UserRequest userRequest = new UserRequest(
                "Tuan Anh",
                "anhdt@gmail.com",
                "asdfgh",
                "asdfgh",
                "09912123434"
        );

        RootObjectRequest rootObjectRequest = new RootObjectRequest(userRequest);

        //create service sign up and use callback to handle some error cases
        DemoServiceAPI demoServiceAPI = DemoServiceAPI.getInstance();
        demoServiceAPI.createService(SignUpAPI.class)
                .signUp(rootObjectRequest)
                .enqueue(new Callback<RootObjectResponse>() {
                    @Override
                    public void onResponse(Call<RootObjectResponse> call, Response<RootObjectResponse> response) {
                        if (response.isSuccessful()) {
                            //Successful
                            Log.v(TAG, "success");
                            Log.v(TAG, "information of new user: " + response.body().getUser().toString());
                        }
                        else {
                            //Error in request
                            int statusCode = response.code();
                            ResponseBody errorBody = response.errorBody();
                            try {
                                Log.v(TAG, "error :" + errorBody.string() + " " + statusCode );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<RootObjectResponse> call, Throwable t) {
                        //Error because network
                        Log.v(TAG, "error by " + t.getMessage() );
                    }
                });
    }
}
