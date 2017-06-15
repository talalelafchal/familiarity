import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public final class HttpClient {

	private HttpClient() {
		
	}
	
	public static Bitmap getBitmap(String urlStr) {
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return getBitmap(url);
	}
	
	public static Bitmap getBitmap(URL url) {
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			in = con.getInputStream();
			return BitmapFactory.decodeStream(in);
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.disconnect();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;	
	}
}
