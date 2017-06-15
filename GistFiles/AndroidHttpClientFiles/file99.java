package net.vvakame.hogehoge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

public class FriendFeedActivity extends Activity {

	private final String uri = "https://friendfeed.com/account/oauth/ia_access_token";

	private final String oauth_consumer_key = "hogehoge";
	private final String oauth_consumer_secret = "fugafuga";
	private final String oauth_signature_method = "HMAC-SHA1";
	private final String oauth_timestamp = String
			.valueOf(new Date().getTime() / 1000);
	private final String oauth_nonce = randomAscii(8);
	private final String oauth_version = "1.0";

	private final String ff_username = "fizz";
	private final String ff_password = "buzz";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		StringBuilder stb = new StringBuilder();

		// リクエスト内容 Key名でソートしてないといけないとかなんとか？
		stb.append("ff_password=").append(ff_password).append("&");
		stb.append("ff_username=").append(ff_username).append("&");
		stb.append("oauth_consumer_key=").append(oauth_consumer_key)
				.append("&");
		stb.append("oauth_nonce=").append(oauth_nonce).append("&");
		stb.append("oauth_signature_method=").append(oauth_signature_method)
				.append("&");
		stb.append("oauth_timestamp=").append(oauth_timestamp).append("&");
		stb.append("oauth_version=").append(oauth_version);
		String requestParameters = stb.toString();

		// signatureの作成
		String keyString = oauth_consumer_secret + "&";
		String signatureBaseString = "GET&" + Uri.encode(uri) + "&"
				+ Uri.encode(requestParameters);
		String signeture = null;
		try {
			signeture = getSignature(keyString, signatureBaseString);
		} catch (InvalidKeyException e) {
			// TODO なんか処理しないとだめよ
		} catch (NoSuchAlgorithmException e) {
			// TODO なんか処理しないとだめよ
		}

		// 実際にほしがるURI
		String request = uri + "?" + requestParameters + "&oauth_signature="
				+ signeture;

		HttpGet httpGet = new HttpGet(request);
		httpGet.addHeader("Authorization", "OAuth");

		HttpClient httpClient = new DefaultHttpClient();
		BufferedReader br = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();

				br = new BufferedReader(new InputStreamReader(is));
				String line = br.readLine();

				System.out.println(response.getStatusLine());
				System.out.println(line);
			}
		} catch (ClientProtocolException e) {
			throw new IllegalStateException(e);
		} catch (SSLException e) {
			// Desireだとここに落ちる
			// N1だと大丈夫
			// 日頃の行いが悪いか、もしくはルート認証局の違いとかだと思う
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private String randomAscii(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("length was too short!");
		}
		StringBuilder stb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			stb.append(randomAscii());
		}
		return stb.toString();
	}

	private char randomAscii() {
		final String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final int index = (int) (Math.random() * chars.length());
		final char c = chars.charAt(index);
		return c;
	}

	private String getSignature(String keyString, String signatureBaseString)
			throws NoSuchAlgorithmException, InvalidKeyException {

		final String algorithm = "HmacSHA1";
		Mac mac = Mac.getInstance(algorithm);
		Key key = new SecretKeySpec(keyString.getBytes(), algorithm);
		mac.init(key);
		byte[] digest = mac.doFinal(signatureBaseString.getBytes());
		String signature = Uri.encode(Base64.byteArrayToBase64(digest));

		return signature;
	}

	// ogawaさんにもらった
	// API Level8 以降なら標準でandroid.util.Base64とか使えるはず
	private static class Base64 {

		static final char intToBase64[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
				'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e',
				'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
				'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
				'3', '4', '5', '6', '7', '8', '9', '+', '/' };

		static String byteArrayToBase64(byte[] bytes) {
			int aLen = bytes.length;
			int numFullGroups = aLen / 3;
			int numBytesInPartialGroup = aLen - 3 * numFullGroups;
			int resultLen = 4 * ((aLen + 2) / 3);
			StringBuilder b = new StringBuilder(resultLen);

			int inCursor = 0;
			for (int i = 0; i < numFullGroups; i++) {
				int byte0 = bytes[inCursor++] & 0xff;
				int byte1 = bytes[inCursor++] & 0xff;
				int byte2 = bytes[inCursor++] & 0xff;
				b.append(intToBase64[byte0 >> 2]);
				b.append(intToBase64[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
				b.append(intToBase64[(byte1 << 2) & 0x3f | (byte2 >> 6)]);
				b.append(intToBase64[byte2 & 0x3f]);
			}

			if (numBytesInPartialGroup != 0) {
				int byte0 = bytes[inCursor++] & 0xff;
				b.append(intToBase64[byte0 >> 2]);
				if (numBytesInPartialGroup == 1) {
					b.append(intToBase64[(byte0 << 4) & 0x3f]);
					b.append("==");
				} else {
					int byte1 = bytes[inCursor++] & 0xff;
					b.append(intToBase64[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
					b.append(intToBase64[(byte1 << 2) & 0x3f]);
					b.append('=');
				}
			}
			return b.toString();
		}
	}
}