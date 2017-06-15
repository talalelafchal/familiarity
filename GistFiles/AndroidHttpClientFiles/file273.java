import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpUtil {
	
	
	/**
	 * Http通信で画像を取得する
	 * @param context
	 * @param targetUrl
	 * @return
	 * @throws SystemException
	 */
	public static Bitmap getBitmapHttpService(final Context context, String targetUrl) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		StringBuilder urlBuilder = new StringBuilder(targetUrl);
		HttpGet request = new HttpGet(urlBuilder.toString());
		
		try {
			Bitmap thumbnailBmp = httpClient.execute(request, new ResponseHandler<Bitmap>() {

				public Bitmap handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					switch(response.getStatusLine().getStatusCode()) {
					
					// 正常終了
					case HttpStatus.SC_OK:
						return BitmapFactory.decodeStream(response.getEntity().getContent());
					case HttpStatus.SC_NOT_FOUND:
						throw new IOException("Data Not Found");
					}
					return null;
				}
			});
			
			return thumbnailBmp;
		} catch (ClientProtocolException e) {
			throw new Exception();
		} catch (IOException e) {
			throw new Exception();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
}