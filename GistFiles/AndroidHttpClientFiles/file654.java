/**
 * OAUTHnesia for Java Android Class
 * 
 * @package	OAUTHnesia
 * @subpackage	Java Android
 * @category	OAUTH Client
 * @author	Batista R. Harahap <tista@urbanesia.com>
 * @link	http://www.bango29.com
 * @license	MIT License - http://www.opensource.org/licenses/mit-license.php
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.urbanesia.android.premier.Utils;

import android.net.ParseException;
import android.util.Log;

public class OAUTHnesia {
	protected static final String BASE_URL = "http://api1.urbanesia.com/";
	public static final int OAUTH_SAFE_ENCODE = 1;
	public static final int OAUTH_NO_SAFE_ENCODE = 0;

	private static String CONSUMER_KEY;
	private static String CONSUMER_SECRET;
	private static String USER_KEY;
	private static String USER_SECRET;

	private static String API_URI;

	private static String SAFE_ENCODE = "0";

	public OAUTHnesia(String cons_key, String cons_secret, int safe_encode) {
		setConsumerKey(cons_key);
		setConsumerSecret(cons_secret);
		if (safe_encode == 1) {
			setSafeEncode("1");
		}
	}

	public String oAuth(String oUri, String post, String get)
			throws InvalidKeyException, NoSuchAlgorithmException,
			ClientProtocolException, IOException {
		setApiUri(oUri);

		// Check POST values, add oauth requirements
		String oPost = "oauth_consumer_key=" + getConsumerKey()
				+ "&oauth_nonce=" + getNonce() + "&oauth_signature_method="
				+ "HMAC-SHA1" + "&oauth_timestamp=" + getTime()
				+ "&oauth_token=" + getUserKey() + "&oauth_version=" + "1.0";
		if (post.compareTo("") == 0) {
			post = oPost;
		} else {
			post += "&" + oPost;
		}
		// Trigger safe encoding
		if (getSafeEncode().compareTo("1") == 0) {
			post += "&safe_encode=" + Integer.toString(OAUTH_SAFE_ENCODE);
		}

		// Check GET values
		if (get.compareTo("") != 0) {
			String[] g = get.split("&");
			int max = g.length;
			int j = 0;
			String getify = "";
			for (int i = 0; i < max; i++) {
				if (j == 1)
					getify += "&";
				String[] temp = g[i].split("=");
				getify += temp[0] + "="
						+ temp[1];
				j = 1;
			}
			get = "&" + get;
		}
		
		post = encodeForOAuth(post);
		get = encodeForOAuth(get);
		
		String requestify = encodeForOAuth(post + get);
		
		// Generate Base Signature
		String base_sig = generateBaseSignature(requestify);

		// Sign to Generate Signature for oAuth
		String signature = sha1(base_sig, getConsumerSecret() + "&"
				+ getUserSecret());

		// Send to Urbanesia
		String oauth_sig = "?oauth_signature=";
		oauth_sig += URLEncoder.encode(signature);
		String url = BASE_URL + getApiUri() + oauth_sig + get;
		String response = this.sendRequest(url, post);

		return response;
	}

	public String oAuthNoUserKey(String oUri, String post, String get)
			throws InvalidKeyException, NoSuchAlgorithmException,
			ClientProtocolException, IOException {
		setApiUri(oUri);
		// Check POST values, add oauth requirements
		String oPost = "oauth_consumer_key=" + getConsumerKey()
				+ "&oauth_nonce=" + getNonce() + "&oauth_signature_method="
				+ "HMAC-SHA1" + "&oauth_timestamp=" + getTime()
				+ "&oauth_version=" + "1.0";

		if (post.compareTo("") == 0) {
			post = oPost;
		} else {
			post += "&" + oPost;
		}

		// Trigger safe encoding
		if (getSafeEncode().compareTo("1") == 0) {
			post += "&safe_encode=" + Integer.toString(OAUTH_SAFE_ENCODE);
		}

		// Check GET values
		if (get.compareTo("") != 0) {
			String[] g = get.split("&");
			int max = g.length;
			int j = 0;
			String getify = "";
			for (int i = 0; i < max; i++) {
				if (j == 1)
					getify += "&";
				String[] temp = g[i].split("=", 2);
				getify += temp[0] + "="
						+ temp[1];
				j = 1;
			}
			get = "&" + get;
		}

		String request = post + get;

		// Encode Request
		String requestify = encodeForOAuth(request);
		
		// Temp hack
		post = encodeForOAuth(post);
		get = encodeForOAuth(get);

		// Generate Base Signature
		String base_sig = generateBaseSignature(requestify);

		// Sign to Generate Signature for oAuth
		String signature = sha1(base_sig, getConsumerSecret() + "&");

		// Send to Urbanesia
		String oauth_sig = "?oauth_signature=";
		oauth_sig += URLEncoder.encode(signature);
		String url = BASE_URL + getApiUri() + oauth_sig + get;
		String response = this.sendRequest(url, post);

		return response;
	}

	public List<String> xAuthList(String username, String password)
			throws InvalidKeyException, NoSuchAlgorithmException,
			ClientProtocolException, IOException, JSONException {
		setApiUri("oauth/access_token");

		// XAUTH Post requests
		String xPost = "&x_auth_username=" + username + "&x_auth_password="
				+ password + "&x_auth_mode=client_auth";

		// OAUTH Post requests
		String post = "oauth_consumer_key=" + getConsumerKey()
				+ "&oauth_nonce=" + getNonce()
				+ "&oauth_signature_method=HMAC-SHA1&oauth_timestamp="
				+ getTime() + "&oauth_version=1.0" + xPost;
		if (getSafeEncode().compareTo("1") == 0) {
			post += "&safe_encode=" + Integer.toString(OAUTH_SAFE_ENCODE);
		}

		// Encode POST
		String postify = encodeForOAuth(post);

		// Generate Base Signature
		String base_sig = generateBaseSignature(postify);

		// Generate Signature for xAuth
		String signature = sha1(base_sig, getConsumerSecret() + "&");

		// Send to Urbanesia
		String oauth_sig = "?oauth_signature=";
		oauth_sig += URLEncoder.encode(signature);
		String url = BASE_URL + getApiUri() + oauth_sig;
		String response = sendRequest(url, post);

		// Parse JSON to extract Results
		JSONObject json = new JSONObject(response);
		JSONObject result = json.getJSONObject("result");
		setUserKey(result.getString("oauth_token_key"));
		setUserSecret(result.getString("oauth_token_secret"));
		String first_name = result.getString("first_name");
		String last_name = result.getString("last_name");
		String account_id = result.getString("account_id");

		List<String> xAuthValList = new ArrayList<String>();
		xAuthValList.add(getUserKey());
		xAuthValList.add(getUserSecret());
		xAuthValList.add(first_name);
		xAuthValList.add(last_name);
		xAuthValList.add(account_id);
		return xAuthValList;
	}
	
	public String oAuthMultipart(String oUri, String post, String get, String filefield, String filepath)
		throws InvalidKeyException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		setApiUri(oUri);
		
		// Check POST values, add oauth requirements
		String oPost = "oauth_consumer_key=" + getConsumerKey()
				+ "&oauth_nonce=" + getNonce() + "&oauth_signature_method="
				+ "HMAC-SHA1" + "&oauth_timestamp=" + getTime()
				+ "&oauth_token=" + getUserKey() + "&oauth_version=" + "1.0";
		
		if (post.compareTo("") == 0) {
			post = oPost;
		} else {
			post += "&" + oPost;
		}
		
		// Trigger safe encoding
		if (getSafeEncode().compareTo("1") == 0) {
			post += "&safe_encode=" + Integer.toString(OAUTH_SAFE_ENCODE);
		}
		
		// Check GET values
		if (get.compareTo("") != 0) {
			String[] g = get.split("&");
			int max = g.length;
			int j = 0;
			String getify = "";
			for (int i = 0; i < max; i++) {
				if (j == 1)
					getify += "&";
				String[] temp = g[i].split("=");
				getify += URLEncoder.encode(temp[0]) + "="
						+ URLEncoder.encode(temp[1]);
				j = 1;
			}
			get = "&" + getify;
		}
		
		String request = post + get;
		
		// Encode Request
		String requestify = encodeForOAuth(request);
		
		// Generate Base Signature
		String base_sig = generateBaseSignature(requestify);
		// Sign to Generate Signature for oAuth
		String signature = sha1(base_sig, getConsumerSecret() + "&"
				+ getUserSecret());
		
		// Send to Urbanesia
		String oauth_sig = "?oauth_signature=";
		oauth_sig += URLEncoder.encode(signature);
		String url = BASE_URL + getApiUri() + oauth_sig + get;
		String response = this.multipartRequest(url, post, filepath, filefield);
		
		return response;
	}

	public void setApiUri(String s) {
		OAUTHnesia.API_URI = s;
	}

	public String getApiUri() {
		return OAUTHnesia.API_URI;
	}

	public void setSafeEncode(String s) {
		OAUTHnesia.SAFE_ENCODE = s;
	}

	public String getSafeEncode() {
		return OAUTHnesia.SAFE_ENCODE;
	}

	public void setConsumerKey(String s) {
		OAUTHnesia.CONSUMER_KEY = s;
	}

	public String getConsumerKey() {
		return OAUTHnesia.CONSUMER_KEY;
	}

	public void setConsumerSecret(String s) {
		OAUTHnesia.CONSUMER_SECRET = s;
	}

	public String getConsumerSecret() {
		return OAUTHnesia.CONSUMER_SECRET;
	}

	public void setUserKey(String s) {
		OAUTHnesia.USER_KEY = s;
	}

	public String getUserKey() {
		return OAUTHnesia.USER_KEY;
	}

	public void setUserSecret(String s) {
		OAUTHnesia.USER_SECRET = s;
	}

	public String getUserSecret() {
		return OAUTHnesia.USER_SECRET;
	}

	String sendRequest(String url, String postString)
			throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();

		if (postString.compareTo("") != 0) {
			// GET & POST Request
			HttpPost post = new HttpPost(url);

			// Create POST List
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			String[] exp = postString.split("&");
			int max = exp.length;
			for (int i = 0; i < max; i++) {
				String[] kv = exp[i].split("=");
				pairs.add(new BasicNameValuePair(kv[0], kv[1]));
			}
			post.setEntity(new UrlEncodedFormEntity(pairs));

			// Get HTTP Response & Parse it
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				// Log.i("HTTPError", "Result of conversion: [" + result + "]");

				instream.close();
				return result;
			}
		} else {
			// GET Request
			HttpGet get = new HttpGet(url);
			
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);

				instream.close();
				return result;
			}
		}

		return "";
	}
	
	public String multipartRequest(String urlTo, String post, String filepath, String filefield) throws ParseException, IOException {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		InputStream inputStream = null;
		
		String twoHyphens = "--";
		String boundary =  "*****"+Long.toString(System.currentTimeMillis())+"*****";
		String lineEnd = "\r\n";
		
		String result = "";
		
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;
		
		String[] q = filepath.split("/");
		int idx = q.length - 1;
		
		try {
			File file = new File(filepath);
			FileInputStream fileInputStream = new FileInputStream(file);
			
			URL url = new URL(urlTo);
			connection = (HttpURLConnection) url.openConnection();
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent", "OAUTHnesia Client 1.0");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
			
			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] +"\"" + lineEnd);
			outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
			outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
			outputStream.writeBytes(lineEnd);
			
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while(bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			
			outputStream.writeBytes(lineEnd);
			
			// Upload POST Data
			String[] posts = post.split("&");
			int max = posts.length;
			for(int i=0; i<max;i++) {
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				String[] kv = posts[i].split("=");
				outputStream.writeBytes("Content-Disposition: form-data; name=\"" + kv[0] + "\"" + lineEnd);
				outputStream.writeBytes("Content-Type: text/plain"+lineEnd);
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(kv[1]);
				outputStream.writeBytes(lineEnd);
			}
			
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			inputStream = connection.getInputStream();
			result = this.convertStreamToString(inputStream);
			
			fileInputStream.close();
			inputStream.close();
			outputStream.flush();
			outputStream.close();
			
			return result;
		} catch(Exception e) {
			Log.e("URBANESIA","Multipart Form Upload Error");
			e.printStackTrace();
			return "error";
		}
	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private String generateBaseSignature(String s) {
		return "POST&" + URLEncoder.encode(BASE_URL + getApiUri()) + "&"
				+ URLEncoder.encode(s);
	}

	private String encodeForOAuth(String s) {
		// Sort the requests
		String[] par = s.split("&");
		Arrays.sort(par);

		// URL Encode Key and Values
		int max = par.length;
		int j = 0;
		String postify = "";
		for (int i = 0; i < max; i++) {
			if (j == 1)
				postify += "&";
			String[] temp = par[i].split("=", 2);
			try {
				postify += URLEncoder.encode(temp[0], "UTF-8") + "="
						+ URLEncoder.encode(Utils.spaceToPercentEncoding(temp[1]), "UTF-8");
			} catch(Exception unused) {}
			j = 1;
		}

		return postify;
	}

	private String getNonce() {
		return md5(Long.toString(System.currentTimeMillis()));
	}

	private String getTime() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	private String sha1(String s, String keyString)
			throws UnsupportedEncodingException, NoSuchAlgorithmException,
			InvalidKeyException {

		SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"),
				"HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);

		byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

		return new String(Base64.encodeBase64(bytes));
	}

	private String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}