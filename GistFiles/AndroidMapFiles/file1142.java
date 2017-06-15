package com.webile.OrientationDemo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * This program demonstrates the orientation and state of the views of an activity.
 * This also demonstrates the getpreferences of a particular activity
 * This also uses onRetainNonConfigurationInstance() method which is called whenever activity changes orientation.
 * this also demonstrates about using different ids for the views so that on orientation the state of the views wont change
 * @author Harsha
 *
 */
public class OrientationDemo extends Activity {

	protected static final String URL_TO_DOWNLOAD = "http://www.google.co.in/images/srpr/logo3w.png";
	Bitmap bitMap;
	ImageView myImageView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String num1 =getPreferences(MODE_PRIVATE).getString("num1", null);
		String num2 = getPreferences(MODE_PRIVATE).getString("num2", null);
		
		setContentView(R.layout.main);
		myImageView = (ImageView) findViewById(R.id.image);
		((EditText)findViewById(R.id.editText1)).setText(num1);
		((EditText)findViewById(R.id.editText2)).setText(num2);
		
		Object data = getLastNonConfigurationInstance();
		if (data == null) {
			new Thread() {
				public void run() {
					try {
						URL aURL = new URL(URL_TO_DOWNLOAD);
						URLConnection conn = aURL.openConnection();
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						conn.connect();
						InputStream is = conn.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is,
								20);
						bitMap = BitmapFactory.decodeStream(bis, null, null);
						bis.close();
						is.close();
						OrientationDemo.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								myImageView.setImageBitmap(bitMap);
							}

						});
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			myImageView.setImageBitmap((Bitmap) data);
		}
		
	}

	@Override
	/**
	 * this is called when orientation of an activty changes.
	 */
	public Object onRetainNonConfigurationInstance() {

		BitmapDrawable d = (BitmapDrawable) myImageView.getDrawable();
		return d.getBitmap();
	}
	
	@Override
	
	protected void onStop() {
		super.onStop();
//		String num = ((EditText)findViewById(R.id.editText1)).getText().toString();
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putString("num1", ((EditText)findViewById(R.id.editText1)).getText().toString());
		editor.putString("num2", ((EditText)findViewById(R.id.editText2)).getText().toString());
		editor.commit();
	}
}