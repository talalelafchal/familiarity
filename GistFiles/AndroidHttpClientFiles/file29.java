package blah.blah.blah;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Base class for asynchronous tasks that perform an HTTP request and process the response as
 * text.
 * 
 * Pass a single HttpRequestBase-derived object, such as an HttpGet or HttpPost, to the
 * execute() method. The background thread will execute the request and read the response as
 * text. The abstract processResponse() method will be invoked on the response text.
 */
public abstract class HttpTextRequestTask extends AsyncTask<HttpRequestBase, Void, String>
{
	/**
	 * Abstract method that will be called in the UI thread when the request is complete.
	 * 
	 * @param responseBody
	 *            null if no response was received, or a String
	 */
	protected abstract void processResponse(String responseBody);

	/**
	 * Execute request and extract the response body text.
	 */
	@Override
	protected String doInBackground(HttpRequestBase... params)
	{
		try
		{
			HttpRequestBase request = params[0];

			HttpClient client = new DefaultHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = client.execute(request, responseHandler);

			return responseBody;
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	@Override
	protected void onPostExecute(String responseBody)
	{
		processResponse(responseBody);
	}
}
