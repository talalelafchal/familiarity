import org.json.JSONObject;
import java.util.Map;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Example of how to set up retrofit to call a RESTful API. 
 * This example shows how to set send a message to a user VIA the
 * Hipchat API.
 */
public class api {
    private static String API_URL = "https://api.hipchat.com";
    private static HipChatInterface hipChatInterface;

    public static HipChatInterface getServiceClient(){
        if(hipChatInterface == null){
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
            hipChatInterface = restAdapter.create(HipChatInterface.class);
        }
        return hipChatInterface;
    }

    public interface HipChatInterface{
        
        // Send a message to a user in Hipchat.
        //Since this is a void method, it will execute asynchronously will execute the callback once finished. 
        @Headers({
            "Authorization: Bearer ACCESS_TOKEN",
            "Content-type: application/json"
        })
        @POST("/v2/user/{email}/message")
        void sendMessage(@Path("email") String email, @Body Map json, retrofit.Callback<JSONObject> cb);
    }
}



/********************************************************************************************************************************/

/*
* This is the code on how to call the API using the sendMessage method
*/

Callback callback = new Callback(){
                @Override
                public void success(Object o, Response response) {
                    // success code
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                   // Error code
                   Log.e("Retrofit Error",retrofitError.getMessage());
                }
            };
//Create a map that will be turned into a JSON object
Map message = new HashMap();
message.put("message","Hello World!");
message.put("color","green");
api.getServiceClient().sendMessage("sample@example.com",message,callback);