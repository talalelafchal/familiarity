import com.google.gson.Gson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Override callback to get a simple error
 * Override callback to get a simple error
 * @param <T>
 */
public abstract class RestCallback<T> implements Callback<T>
{
    // -- We need to create those functions each time we create a RestCallback
    public abstract void success(T t);
    public abstract void failure(RestError restError);

    @Override
    public void success(T t, Response response) {
        success(t);
    }

    @Override
    public void failure(RetrofitError error)
    {
        // Try to unserialize the body
        RestError restError = null;

        try {
            if (error.getResponse() != null) {
                restError = (RestError) error.getBodyAs(RestError.class);

                if (restError == null || restError.getCode() == null) {
                    TypedByteArray jsonBody =  (TypedByteArray)error.getResponse().getBody();

                    if (jsonBody != null) {
                        restError = new Gson().fromJson(new String(jsonBody.getBytes()), RestError.class);
                    }
                }
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

        // it's not a valid Api error with a correct json.
        if (restError == null) {
            restError = new RestError();
        }

        // We save the RetrofitError
        restError.setError(error);

        failure(restError);
    }
}