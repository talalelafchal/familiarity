package com.example.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import android.net.http.AndroidHttpClient;

public class SentryLog implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler defaultUEH;
	private String localPath;
	private String url;

	public SentryLog(String localPath, String url) {
		this.localPath = localPath;
		this.url = url;
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		String timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		String filename = timestamp + ".stacktrace";

		File file = null;
		if (localPath != null) {
			file = writeToFile(stacktrace, filename);
		}
		if (url != null) {
			sendToServer(stacktrace, file);
		}

		defaultUEH.uncaughtException(t, e);
	}

	private File writeToFile(String stacktrace, String filename) {
		File dir = new File(filename);
		if ( ! dir.exists()) dir.mkdirs();
		String savePath = localPath + "/" + filename;
		File file = new File(savePath);
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter bos = new BufferedWriter(fw);
			bos.write(stacktrace);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	private void sendToServer(String stacktrace, File filename) {
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart(filename.getName(), new FileBody(filename));
		try {
			httpClient.execute(httpPost);
			entity.consumeContent();
		} catch (IOException e) {
			httpPost.abort();
			e.printStackTrace();
		}
		httpClient.getConnectionManager().shutdown();
	}
}
