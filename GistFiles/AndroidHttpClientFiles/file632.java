package jp.gr.jin.OCAO;

// based on http://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/httpclient/src/examples/org/apache/http/examples/client/ClientCustomContext.java
// and see Unlock Android (Japanese version) section 6.4.2 p.219

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;

// import jp.gr.jin.OCAO.HTTPRequestHelper;
import jp.gr.jin.OCAO.Constants;

public class CookieLogin extends Activity {

	private ProgressDialog progressDialog;
	private TextView output;
	private Button button;
	private final String CLASSTAG = jp.gr.jin.OCAO.CookieLogin.class.getSimpleName();
	// Create a local instance of cookie store
	private CookieStore cookieStore = new BasicCookieStore();
	
	// use a handler to update the UI (send the handler messages from other
	// threads)
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			progressDialog.dismiss();
			String bundleResult = msg.getData().getString("RESPONSE");
			output.setText(bundleResult);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.output = (TextView) findViewById(R.id.Response);
		this.button = (Button) findViewById(R.id.Button);
		
		this.button.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				output.setText(".....");
				performRequest("http://192.168.1.85/cacti/index.php");
			}
		});
	}

	private void performRequest(final String url) {
		final ResponseHandler<String> responseHandler = new ResponseHandler<String> () {
			public String handleResponse(HttpResponse response) {
				HttpEntity entity = response.getEntity();
				String result =null;
				List<Cookie> cookies = cookieStore.getCookies();
				try {
					for (int i = 0; i < cookies.size(); i++) {
						result = result + cookies.get(i).toString() + "\n";
					}

					result = result + StringUtils.inputStreamToString(entity.getContent());
					Message message = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("RESPONSE", result);
					message.setData(bundle);
					handler.sendMessage(message);
				} catch (Exception e) {
					Log.e(Constants.LOGTAG, CLASSTAG + "error while performing request: " + e.getMessage());
				}
				return result;
			}
		};

		this.progressDialog = ProgressDialog.show(this, "working . . .",
				"performing HTTP request");

		// do the HTTP dance in a separate thread (the responseHandler will fire
		// when complete)
		new Thread() {
			@Override
			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
				try {

					// Create local HTTP context
					HttpContext localContext = new BasicHttpContext();
					// Bind custom cookie store to the local context
					localContext.setAttribute(ClientContext.COOKIE_STORE,
							cookieStore);

					final HttpPost httppost = new HttpPost(url);

					Log.i(Constants.LOGTAG, CLASSTAG + "executing request "
							+ httppost.getURI());

					// Pass local context as a parameter
					String response = httpclient.execute(httppost, responseHandler,
							localContext);
					Log.i(Constants.LOGTAG, response);
					
				} catch (Exception e) {
					Log.e(Constants.LOGTAG,
							CLASSTAG + "error " + e.getMessage());
				}
			}
		}.start();

	}
}