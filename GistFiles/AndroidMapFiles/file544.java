import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class WebService
{
	private static Map<String, String> DefaultHeaders = new HashMap<>();
	
	public static void getStuffArray(IWebServiceCallback<Stuff[]> callback)
	{
		Map<String, String> headers = new HashMap<>();
		headers.put("key", "value");
		WebService.sendRequest("http://example.com/getStuffArray", Stuff[].class, callback, headers);
	}

	public static <T> void sendRequest(String url, final Class<T> clas, final IWebServiceCallback<T> callback, final Map<String, String> headers)
	{
		VolleySingleton.getInstance().getRequestQueue().add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
		{
			@Override
			public void onResponse(String s)
			{
				Gson gson = new GsonBuilder().create();
				try
				{
					T ret = gson.fromJson(s, clas);
					callback.onResponse(ret);
				} catch (Exception ex)
				{
					Log.e("TAG", ex.getMessage());
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError volleyError)
			{
				String message = volleyError == null ? "error null" : volleyError.getMessage();
				message = message == null ? volleyError.toString() : message;
				Log.d("TAG", message );
				callback.onErrorResponse();
			}
		})
		{
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				return headers == null ? DefaultHeaders : headers;
			}
		});
	}
}