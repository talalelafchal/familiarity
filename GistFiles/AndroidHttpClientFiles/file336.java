package org.apache.cordova;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.*;

import org.acra.ErrorReporter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.util.Log;

public class YouTubeUploader {
	String DeveloperKey = "";
	String YouTubeUsername = "";
	String GoogleUsername = "";
	String Password = "";
	String Source = "";

	private String AuthToken = "";

	void Login() {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost request = new HttpPost(
				"https://www.google.com/accounts/ClientLogin");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Email", GoogleUsername));
		params.add(new BasicNameValuePair("Passwd", Password));
		params.add(new BasicNameValuePair("source", Source));
		params.add(new BasicNameValuePair("service", "youtube"));

		try {
			request.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			ErrorReporter.getInstance().handleException(e);
		}

		HttpResponse response = null;
		try {
			response = httpclient.execute(request);
		} catch (ClientProtocolException e) {
			ErrorReporter.getInstance().handleException(e);
		} catch (IOException e) {
			ErrorReporter.getInstance().handleException(e);
		}

		HttpEntity resEntityGet = response.getEntity();

		try {
			String responseText = EntityUtils.toString(resEntityGet);

			String[] tokens = responseText.split("\n");
			if (tokens.length >= 3) {
				String[] authTokens = tokens[2].split("=");
				this.AuthToken = authTokens[1];
			}

		} catch (ParseException e) {
			ErrorReporter.getInstance().handleException(e);
		} catch (IOException e) {
			ErrorReporter.getInstance().handleException(e);
		}
	}

	void OtherLogin() {
		// do this wherever you are wanting to POST
		URL url;
		HttpsURLConnection conn;

		try {
			// if you are using https, make sure to import
			// java.net.HttpsURLConnection
			url = new URL("https://www.google.com/accounts/ClientLogin");

			// you need to encode ONLY the values of the parameters
			String param = "Email="
					+ URLEncoder.encode(GoogleUsername, "UTF-8") + "&Passwd="
					+ URLEncoder.encode(Password, "UTF-8") + "&source="
					+ URLEncoder.encode(Source, "UTF-8") + "&service="
					+ URLEncoder.encode("youtube", "UTF-8");

			conn = (HttpsURLConnection) url.openConnection();
			// set the output to true, indicating you are outputting(uploading)
			// POST data
			conn.setDoOutput(true);
			// once you set the output to true, you don't really need to set the
			// request method to post, but I'm doing it anyway
			conn.setRequestMethod("POST");

			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
			} };

			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts,
					new java.security.SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext
					.getSocketFactory();
			conn.setSSLSocketFactory(sslSocketFactory);

			// Android documentation suggested that you set the length of the
			// data you are sending to the server, BUT
			// do NOT specify this length in the header by using
			// conn.setRequestProperty("Content-Length", length);
			// use this instead.
			conn.setFixedLengthStreamingMode(param.getBytes().length);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// send the POST out
			PrintWriter out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.close();

			// build the string to store the response text from the server
			String response = read(conn.getInputStream());

			String[] tokens = response.split("\n");
			if (tokens.length >= 3) {
				String[] authTokens = tokens[2].split("=");
				this.AuthToken = authTokens[1];
			}
		}
		// catch some error
		catch (MalformedURLException ex) {
			ErrorReporter.getInstance().handleException(ex);
		}
		// and some more
		catch (IOException ex) {
			ErrorReporter.getInstance().handleException(ex);
		}
		// and more
		catch (Exception ex) {
			ErrorReporter.getInstance().handleException(ex);
		}
	}

	String UploadVideo(String videoPath) {
		File file = new File(videoPath);
		String boundary = "qwerty";
		String endLine = "\r\n";

		StringBuilder sb = new StringBuilder();
		sb.append("--" + boundary + endLine);
		sb.append("Content-Type: application/atom+xml; charset=UTF-8" + endLine
				+ endLine);

		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\"?>");
		xml.append("<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:yt=\"http://gdata.youtube.com/schemas/2007\">");
		xml.append("<media:group>");
		xml.append("<media:title type=\"plain\">" + file.getName()
				+ "</media:title>");
		xml.append("<media:description type=\"plain\">Video Description goes here...</media:description>");
		xml.append("<media:category scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\">People</media:category>");
		xml.append("<media:keywords>video</media:keywords>");
		xml.append("</media:group>");
		xml.append("</entry>");

		sb.append(xml.toString() + endLine);
		sb.append("--" + boundary + endLine);
		sb.append("Content-Type: application/octet-stream" + endLine);
		sb.append("Content-Transfer-Encoding: binary" + endLine + endLine);

		String bodyStart = sb.toString();

		sb = new StringBuilder();
		sb.append(endLine + "--" + boundary + "--");

		String bodyEnd = sb.toString();

		HttpURLConnection conn;
		try {
			FileInputStream fIn = new FileInputStream(file);
			byte fileBytes[] = new byte[(int) file.length()];
			fIn.read(fileBytes);

			conn = (HttpURLConnection) new URL(
					"http://uploads.gdata.youtube.com/feeds/api/users/"
							+ YouTubeUsername + "/uploads").openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"multipart/related; boundary=\"" + boundary + "\"");
			conn.setRequestProperty("Authorization", "GoogleLogin auth="
					+ this.AuthToken);
			conn.setRequestProperty("GData-Version", "2");
			conn.setRequestProperty("X-GData-Key", "key=" + DeveloperKey);
			conn.setRequestProperty("Slug", file.getName());
			conn.setRequestProperty(
					"Content-Length",
					""
							+ (bodyStart.getBytes().length + fileBytes.length + bodyEnd
									.getBytes().length));
			conn.setRequestProperty("Connection", "close");

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			try {
				conn.connect();

				Log.d("ID", "" + file.length());

				try {
					OutputStream os = new BufferedOutputStream(
							conn.getOutputStream());

					os.write(bodyStart.getBytes());
					os.write(fileBytes);
					os.write(bodyEnd.getBytes());
					os.flush();

					String response = "";
					try {
						response = read(conn.getInputStream());

						Document doc = Jsoup.parse(response);

						Element id = doc.select("entry id").first();

						String video_id = id.text().split("video:")[1];

						String url = "http://www.youtube.com/watch?v="
								+ video_id;

						return url;
					} catch (FileNotFoundException ex) {
						ErrorReporter.getInstance().handleException(ex);
					} catch (Exception ex) {
						ErrorReporter.getInstance().handleException(ex);
					}

					Log.d("ID", response);

				} catch (FileNotFoundException e1) {
					ErrorReporter.getInstance().handleException(e1);
				} catch (IOException ex) {
					ErrorReporter.getInstance().handleException(ex);
				}
			} catch (IOException e2) {
				ErrorReporter.getInstance().handleException(e2);
			}
		} catch (MalformedURLException e3) {
			ErrorReporter.getInstance().handleException(e3);
		} catch (IOException e3) {
			ErrorReporter.getInstance().handleException(e3);
		}
		return null;
	}

	String read(InputStream is) {
		String response = "";
		Scanner inStream = new Scanner(is);
		while (inStream.hasNextLine())
			response += (inStream.nextLine()) + "\n";
		return response;
	}
}