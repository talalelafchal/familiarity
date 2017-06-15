package com.example.android;

import android.content.Context;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;

import java.net.URL;
import java.io.File;
import java.net.URLEncoder;


/**
 * Declare in the layout this class like
 *
 * &lt;com.example.android.ImageViewExtended/&gt;
 *
 * In order to use the "href" attribute you have to declare its namespace like this
 * 	xmlns:ms="http://schemas.android.com/apk/res/com.example.android"
 *
 */
public class ImageViewExtended extends ImageView implements RemoteResourcesDownloader.RemoteResourcesObserver {
	private static final String TAG = "ImageViewExtended";
	private Context mContext;
	private String mHref;
	private Animation mAnimation;

	public ImageViewExtended(Context context) {
		this(context, null, 0);
	}

	public ImageViewExtended(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		init(context, attrs, 0);
	}

	public ImageViewExtended(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;

		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageViewExtended, defStyle, 0);

		// this is useless for now since we have only one attribute
		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			//android.util.Log.i(TAG, "N=" + N);
			int attr = a.getIndex(i);
			switch (attr) {
				case R.styleable.ImageViewExtended_href:
					//android.util.Log.i(TAG, "ImageViewExtended_href");
					setImageHref(a.getString(attr));
					break;
				default:
					android.util.Log.i(TAG, "default");
			}
		}

		a.recycle();
	}

	private void startSpinner() {
		mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.clockwise_rotation);
		setImageResource(R.drawable.spinner_black_48);
		startAnimation(mAnimation);
	}

	private void stopSpinner() {
		mAnimation.cancel();
		mAnimation.reset();
	}

	private String getLocalFilePath(String filename, boolean inSDCard) {
		// http://stackoverflow.com/questions/3082325/my-cache-folder-in-android-is-null-do-i-have-to-create-it
		String cacheDirName;
		// get (if exists) the SD Card mount path and create (if doesn't exists) the directory
		if (inSDCard && android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDirName = android.os.Environment.getExternalStorageDirectory() + "/myApp/";
			File cacheDir = new File(cacheDirName);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		} else {
			cacheDirName = mContext.getCacheDir().toString();
		}

		// "/data/com.example.android.myApp/cache/" + "meow.png"
		String localFileName = cacheDirName + filename;

		return localFileName;
	}

	private void setImageBroken() {
		setImageResource(R.drawable.broken_image);
	}

	/**
	 * This sets the href attribute and downloads the image if necessary.
	 *
	 * This implementations get the URL, find the last path component and look
	 * in the local filesystem if a file exists with the same name; if not call
	 * the RemoteResourcesDownloader and set a default image.
         * TODO: if mHref was already set with different value notify the downloader
         * to remove this instance from the observer for the old resource.
	 */
	public void setImageHref(String href) {
		mHref = href;

		// imageURL = "http://www.dominio.com/miao/bau/meow.png"
		final URL imageURL;
		try {
			imageURL = new URL(href);
		} catch(java.net.MalformedURLException e) {
			/*
			 * If the href is malformed what to do? we simply set a broken image
			 * and return.
			 */
			e.printStackTrace();
			setImageBroken();
			return;
		}
		// ["miao", "bau", "meow.png"]
		String[] components = imageURL.getFile().split("/");
		// "meow.png"
		String filename = "";
		try {
			filename = URLEncoder.encode(components[components.length - 1], "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			filename = components[components.length - 1];
		}

		String localFileName = getLocalFilePath(filename, false);

		final File localFile = new File(localFileName);

		if (!localFile.exists()) {
			startSpinner();

			RemoteResourcesDownloader.getInstance().askForResource(this, imageURL, localFile);
		} else {
			setBitmapFromFilename(localFileName);
		}
	}

	protected void setBitmapFromFilename(String localFileName) {
		Bitmap b = BitmapFactory.decodeFile(localFileName);
		super.setImageBitmap(b);
	}

	/**
	 * When the downloader has finished this instance will be advised and
	 * recall setImageHref() so to find the file locally and load the Bitmap.
	 */
	public void notifyEndDownload() {

		/*
		 * This will be called from a thread different from the UI thread and can cause
		 * crash of application. Run instead in the UIThread using post().
		 */
		post(new Runnable() {
			public void run() {
				stopSpinner();
				setImageHref(mHref);
			}
		});
	}
}