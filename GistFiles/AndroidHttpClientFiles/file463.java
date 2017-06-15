
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.util.Log;



public class HttpUtil {
	public static String doGET(String url) throws MalformedURLException {
		Log.d("debug", "request:" + url);
		URL urlRequest = new URL(url);
		try {
			InputStream stream = urlRequest.openConnection().getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuffer sb = new StringBuffer();

			String line = reader.readLine();
			while (line != null) {
				sb.append(line + "\n");
				line = reader.readLine();
			}
			reader.close();
			return sb.toString();
		} catch (IOException e) {
			Log.e("error", e.getMessage(), e);
		}
		return null;

	}

	public static String doMultiPartPost(String url, Map<String, Object> parameters) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			// setup multipart entity
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			if (parameters != null) {
				for (Map.Entry<String, Object> entry : parameters.entrySet()) {
					if(entry.getValue() instanceof File){
						File f = (File) entry.getValue();
						FileBody fileBody = new FileBody(f);
						entity.addPart(entry.getKey(), fileBody);						
						// identify param type by Key
					} else {
						entity.addPart(entry.getKey(),
								new StringBody(String.valueOf(entry.getValue()), Charset.forName("UTF-8")));
					}
				}
			}

			post.setEntity(entity);

			// create response handler
			ResponseHandler<String> handler = new BasicResponseHandler();
			// execute and get response
			return new String(client.execute(post, handler).getBytes(), HTTP.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String doPost(String surl, Map<String, Object> parameters) {
		StringBuffer param = new StringBuffer();
		if (parameters != null) {
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				if (ArrayUtil.isArray(entry.getValue())) {
					// Note: required to be String[]
					String[] values = (String[]) entry.getValue();
					for (String str : values) {
						param.append("&");
						param.append(entry.getKey());
						param.append("[]=");
						param.append(str);
					}
				} else if (entry instanceof List) {
					for (Object value : (List<?>) entry) {
						param.append("&");
						param.append(entry.getKey());
						param.append("[]=");
						param.append(String.valueOf(entry.getValue()));
					}
				} else {
					param.append("&");
					param.append(entry.getKey());
					param.append("=");
					try {
						param.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
					}

				}
			}
		}
		String paramStr = param.toString();
		byte[] bytes = paramStr.getBytes();
		try {
	
			URL url = new URL(surl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			((HttpURLConnection) urlConn).setRequestMethod("POST");
			urlConn.setUseCaches(false);
			urlConn.setAllowUserInteraction(true);
			HttpURLConnection.setFollowRedirects(true);
			urlConn.setInstanceFollowRedirects(true);

			urlConn.setRequestProperty("User-agent", "myproject");
			urlConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlConn.setRequestProperty("Accept-Language", "zh-tw,en-us;q=0.7,en;q=0.3");
			urlConn.setRequestProperty("Accept-Charse", "utf-8;q=0.7,*;q=0.7");

			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Content-Length", String.valueOf(bytes.length));

			java.io.DataOutputStream dos = new java.io.DataOutputStream(urlConn.getOutputStream());
			dos.write(bytes);

			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(
					urlConn.getInputStream()));

			StringBuffer response = new StringBuffer();
			String line = reader.readLine();
			while (line != null) {
				response.append(line + "\n");
				line = reader.readLine();
			}
			reader.close();
			return response.toString();
		} catch (java.io.IOException e) {
			Log.e("error", e.getMessage(), e);
		}
		return null;
	}
}
