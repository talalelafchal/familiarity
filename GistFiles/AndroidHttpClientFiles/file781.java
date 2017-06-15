package th.co.fingertip.eventproFP.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import th.co.fingertip.eventproFP.EventproFPEnum;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * @author raisonde
 *
 */
public class RestTask extends AsyncTask<HttpUriRequest, Void, String>{

    public static final String HTTP_DEFAULT_RESPONSE = "httpResponse";
	public static final String HTTP_SEARCH_EVENT_RESPONSE = "httpEventResponse";
	public static final String HTTP_SEARCH_CATEGORY_RESPONSE = "httpCategoryResponse";

	private Context context;
	private String action;
	private HttpClient http_client;

	ResponseHandler<String> response_handler = new ResponseHandler<String>() {
	    public String handleResponse(final HttpResponse response)
	        throws HttpResponseException, IOException {
	        StatusLine statusLine = response.getStatusLine();
	        if (statusLine.getStatusCode() >= 300) {
	            throw new HttpResponseException(statusLine.getStatusCode(),
	                    statusLine.getReasonPhrase());
	        }

	        HttpEntity entity = response.getEntity();
	        return entity == null ? null : EntityUtils.toString(entity, EventproFPEnum.Encoding.UTF);
	    }
	};

	public RestTask(Context context, String action) {
		super();
		this.context = context;
		this.action = action;
		this.http_client = new DefaultHttpClient();
	}

	public RestTask(Context context, String action, HttpClient http_client) {
		super();
		this.context = context;
		this.action = action;
		this.http_client = http_client;
	}

	@Override
	protected String doInBackground(HttpUriRequest... params) {
		try{
			//create request
			HttpUriRequest http_request = params[0];
			//execute request
			HttpResponse http_response = http_client.execute(http_request);

			String response_string = response_handler.handleResponse(http_response);

			return response_string;
		}
		catch(Exception e){
			return null;
		}
	}

	//callback that broadcasts the message when the  htttp response is returned.
	@Override
	protected void onPostExecute(String result) {
		Intent intent = new Intent(action);
		if (action.equals(EventproFPEnum.Action.VIEW_EVENT)) {
			intent.putExtra(HTTP_SEARCH_EVENT_RESPONSE, result);

		}

		/*
		 * Fire the message to the receivers that are registered in context(activity) instance
		 * with an intent as a message.
		 */
		context.sendBroadcast(intent);
	}


}