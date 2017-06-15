package epeli.ircshare;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.codec_1_4.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;

public class PostToIRC extends Activity
{
	
	TextView status;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		status = (TextView) findViewById(R.id.result);
		
		

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		
		status.setText("Start");

		// if this is from the share menu
		if (Intent.ACTION_SEND.equals(action)){
			if (extras.containsKey(Intent.EXTRA_STREAM)){
				Log.i(this.getClass().getName(), "SEND2");
				try {
					
					// Get resource path from intent callee
					Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
					

					// Query gallery for camera picture via
					// Android ContentResolver interface
					ContentResolver cr = getContentResolver();
					InputStream is = cr.openInputStream(uri);
					// Get binary bytes for encode
					byte[] data = getBytesFromFile(is);

					// base 64 encode for text transmission (HTTP)
					byte[] encoded_data = Base64.encodeBase64(data);
					String data_string = new String(encoded_data); // convert to
																	// string

					status.setText("Sending" + uri.getPath());
					new UploadImageTask().execute(data_string);

					return;
				} catch (Exception e)
				{
					Log.e(this.getClass().getName(), e.toString());
				}

			} else if (extras.containsKey(Intent.EXTRA_TEXT))
			{
				return;
			}
		}
		else {
			status.setText("Not called by SEND intent");
		}

	}
	
	
	private class UploadImageTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String data = params[0];
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://kamino.kortex.jyu.fi:1337/photos");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("picname", "test"));
			nameValuePairs.add(new BasicNameValuePair("pic", data));
			
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		}
		public void onPostExecute(Void dfs) {
			status.setText("ok!");
		}
		
	}
	

	public static byte[] getBytesFromFile(InputStream is)
	{
		try
		{
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1)
			{
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			return buffer.toByteArray();
		} catch (IOException e)
		{
			Log.e("com.eggie5.post_to_eggie5", e.toString());
			return null;
		}
	}

}