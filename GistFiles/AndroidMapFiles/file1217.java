package your.app.package;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class PlacesRequest extends JsonObjectRequest
{
    static String yelpAccessToken;

    PlacesRequest(int method, String url, JSONObject jsonRequest,
                  Response.Listener<JSONObject> listener,
                  Response.ErrorListener errorListener)
    {
        super(method, url, jsonRequest, listener, errorListener);
    }

    PlacesRequest(String url, JSONObject jsonRequest,
                  Response.Listener<JSONObject> listener,
                  Response.ErrorListener errorListener)
    {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + yelpAccessToken);

        return headers;
    }
}