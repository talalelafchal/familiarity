/**
 * This code is originally from here:
 * http://stackoverflow.com/questions/7203047/code-for-download-video-from-youtube-on-java-android
 * 
 * And some code about HTTP header is from here:
 * http://xissy.github.com/dev/2012/09/28/get-a-youtube-mp4-file-link-which-can-be-played-iphone/
 */
package com.example.zzz;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;

public class MainActivity extends Activity {

  /**
	 * Inner class to play YouTube video with MediaPlayer
	 */
	private class YouTubePlayTask extends AsyncTask<String, Void, String> {

		private ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
		
		private String mVideoId;
		private MediaPlayer mPlayer;
		
		/**
		 * Constructor
		 */
		public YouTubePlayTask(String videoId, MediaPlayer mp) {
			this.mVideoId = videoId;
			this.mPlayer = mp;
		}
		
		@Override
		protected void onPreExecute() {
			mDialog.setMessage("Downloading...");
			mDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			int begin, end;
			String tmpstr = null;
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://www.youtube.com/watch?v=" + this.mVideoId);
				request.setHeader("User-Agent", "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 Mobile/9B176 Safari/7534.48.3");
				HttpResponse response = client.execute(request);
				
				InputStream stream = response.getEntity().getContent();
				InputStreamReader reader=new InputStreamReader(stream);
				StringBuffer buffer=new StringBuffer();
				char[] buf=new char[262144];
				int chars_read;
				while ((chars_read = reader.read(buf, 0, 262144)) != -1) {
					buffer.append(buf, 0, chars_read);
				}
				tmpstr=buffer.toString();

				begin  = tmpstr.indexOf("url_encoded_fmt_stream_map=");
				end = tmpstr.indexOf("&", begin + 27);
				if (end == -1) {
					end = tmpstr.indexOf("\"", begin + 27);
				}
				tmpstr = URLDecoder.decode(tmpstr.substring(begin + 27, end), "utf-8");

			} catch (MalformedURLException e) {
				throw new RuntimeException();
			} catch (IOException e) {
				throw new RuntimeException();
			}

			Vector<String> url_encoded_fmt_stream_map = new Vector<String>();
			begin = 0;
			end   = tmpstr.indexOf(",");

			while (end != -1) {
				url_encoded_fmt_stream_map.add(tmpstr.substring(begin, end));
				begin = end + 1;
				end   = tmpstr.indexOf(",", begin);
			}

			url_encoded_fmt_stream_map.add(tmpstr.substring(begin, tmpstr.length()));
			String result = "";
			Enumeration<String> url_encoded_fmt_stream_map_enum = url_encoded_fmt_stream_map.elements();
			while (url_encoded_fmt_stream_map_enum.hasMoreElements()) {
				tmpstr = (String)url_encoded_fmt_stream_map_enum.nextElement();
				begin = tmpstr.indexOf("itag=");
				if (begin != -1) {
					end = tmpstr.indexOf("&", begin + 5);

					if (end == -1) {
						end = tmpstr.length();
					}

					int fmt = Integer.parseInt(tmpstr.substring(begin + 5, end));
					Log.v("ZZZ", "fmt = " + fmt);

					if (fmt == 18 /*35*/) {
						begin = tmpstr.indexOf("url=");
						if (begin != -1) {
							end = tmpstr.indexOf("&", begin + 4);
							if (end == -1) {
								end = tmpstr.length();
							}
							try {
								result = URLDecoder.decode(tmpstr.substring(begin + 4, end), "utf-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							break;
						}
					}
				}
			}
			
			// PLAY VIDEO
			Log.v("ZZZ", result);
			if (mPlayer != null) {
				try {
					// Set SurfaceView to the MediaPlayer
					SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView1);
					mPlayer.setDisplay(sv.getHolder());
					
					// Start to Play
					mPlayer.setDataSource(result);
					mPlayer.prepare();
					mPlayer.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			mDialog.dismiss();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MediaPlayer player = new MediaPlayer();
		new YouTubePlayTask("CHCwXc4DBaA", player).execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
