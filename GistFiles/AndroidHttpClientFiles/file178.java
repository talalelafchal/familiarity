import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;



	protected HttpResponse doPost() throws java.io.IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(mURL);
		post.setEntity(new UrlEncodedFormEntity(mNameValuePairs, "UTF-8"));
		// to avoid 100-Continue from server side
		post.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);

		return client.execute(post);
	}

	protected HttpResponse doGet() throws java.io.IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(mURL);

		return client.execute(get);
	}

	public void run() {
		String entity = "";

		try {
			HttpResponse response =
				(mMethod == RemoteHandler.GET) ?
					doGet() :
					doPost();

			entity = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			android.util.Log.e("RemoteHandler", "An error occoured");
			e.printStackTrace();
		}
	}