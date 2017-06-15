/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.arte.plus7.business.controller.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * <p>It requires the INTERNET permission, which should be added to your application's manifest
 * file.</p>
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader {
    private static final String LOG_TAG = "ImageDownloader";
    private static ImageDownloader INSTANCE;
    public enum Mode { NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT }
    private Mode mode = Mode.CORRECT;
    public static boolean SAVE_ON_CARD = false;
    
    public static final String IMG_FOLDER = "img/";
    public static final long IMG_FOLDER_SIZE = 5000; //taille en Ko

	public static String IMAGES_FOLDER = "/img/";
    
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */

    private static final int HARD_CACHE_CAPACITY = 80;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000000000; // in milliseconds

    
    private ImageDownloader(){
    	
    }
    
    public static ImageDownloader getInstance(){
    	if (INSTANCE == null){
    		INSTANCE = new ImageDownloader();
    	}
    	return INSTANCE;
    }
    
    
    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void download(String url, ImageView imageView, ProgressBar progressBar ) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (SAVE_ON_CARD && bitmap == null ){
        	bitmap = loadBitmapFromHard(url);
        }
        if (bitmap == null) {
            forceDownload(url, imageView,progressBar);
        } else {
            cancelPotentialDownload(url, imageView);
            imageView.setImageBitmap(bitmap);
        	progressBar.setVisibility(View.GONE);
        	imageView.setVisibility(View.VISIBLE);
        }
    }

    /**.
     * Load an image from SD card
     * @param url of the file we want
     * @return the bitmap if exist
     */
    public Bitmap loadBitmapFromHard(String url){
		File root = Environment.getExternalStorageDirectory();
    	return BitmapFactory.decodeFile(root + "/" + ManagerPreferences.FILES_FOLDER + IMG_FOLDER+ getFileName(url)+".png");
    }
    
    /**.
     * return the size of a folder
     * @param dir : directory path
     * @return size of the folder en Ko
     */
	private static long dirSize(File dir) {
		long result = 0; // en Octets

		Stack<File> dirlist = new Stack<File>();
		dirlist.clear();

		dirlist.push(dir);

		while (!dirlist.isEmpty()) {
			File dirCurrent = dirlist.pop();

			File[] fileList = dirCurrent.listFiles();
			if (fileList != null) {
				for (int i = 0; i < fileList.length; i++) {

					if (fileList[i].isDirectory())
						dirlist.push(fileList[i]);
					else
						result += fileList[i].length();
				}
			}
		}

		return result / 1024;
	}
    
    /**.
     * Save the bitmap object into a file on the SD card
     * @param bitmap file
     * @param url from where we got the image
     * @return true if saved, else false;
     */
    private boolean saveBitmap(Bitmap bitmap,String url){
    	OutputStream outStream = null;
    	Bitmap bm = null;
    	try {
			File root = Environment.getExternalStorageDirectory();

			final String directory = root + "/" + ManagerPreferences.FILES_FOLDER + IMG_FOLDER;
			File folderPath = new File(directory, "/");

			if (dirSize(folderPath) < IMG_FOLDER_SIZE) {
				if (! folderPath.exists()) {
					folderPath.mkdirs();
				}
				if (folderPath.canWrite()){
					File file = new File(directory, getFileName(url)+".png");
					file.createNewFile();
					outStream = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
					outStream.flush();
					outStream.close();
				}
			} else {
				Log.v(this.getClass().getName(),"Images' folder is too fat! The limite is : " + IMG_FOLDER_SIZE + "Ko; let's clean the img folder!");
				clearFolder(folderPath);
				return false;
			}

    	}  catch (Exception e) {
    		Log.e(this.getClass().getName(), "Can not create the file: " + e.toString());
    		return false;
    	}
    	return true;
    }
    
    /**.
     * Get le Day In Year d'une image (information rajoutee prealablement)
     * @param fileName nom du fichier dans lequel (\d*?)#* correspond au Day In Year
     * @return Day In Year en int
     */
    public static int getDayInYearOfPicture(String fileName){
    	Pattern pattern = Pattern.compile("^(\\d*?)#");
    	Matcher m = pattern.matcher(fileName);
    	m.find();
    	return Integer.valueOf(m.group().substring(0,m.group().length() - 1));
    }

    
	/**.
	 * Clear the dossier pass en parametre
	 * @param folderPath dossier a cleaner
	 */
	public static void clearFolder(File folderPath) {

		if (folderPath == null) {
			return;
		}

		if (!folderPath.isDirectory()) {
			return;
		}

		File[] files = folderPath.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				clearFolder(file);
				file.delete();
			}
		}
	}

    /**.
     * Get file name with extension from full url
     * @param url
     * @return file name
     */
    public static String getFileName(String url){
    	Pattern pattern = Pattern.compile("[\\w_.-]*?(?=[\\?\\#])|[\\w_.-]*$");
    	Matcher m = pattern.matcher(url);
    	m.find();
    	return m.group();
    }
    
    /*
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
       private void forceDownload(String url, ImageView view) {
          forceDownload(url, view, null);
       }
     */

    /**
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
     * @param adapter 
     */
    private void forceDownload(String url, ImageView imageView, ProgressBar progressBar/*, BaseAdapter adapter*/) {
        //on affiche le progessbar
        if (progressBar != null){
        	progressBar.setVisibility(View.VISIBLE);
        }
        //on cache l'image
        if (imageView != null){
        	imageView.setVisibility(View.INVISIBLE);
        }
        
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (url == null) {
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.VISIBLE);
            return;
        }

        if (cancelPotentialDownload(url, imageView)) {
            switch (mode) {
                case NO_ASYNC_TASK:
                    Bitmap bitmap = downloadBitmap(url);
                    addBitmapToCache(url, bitmap);
                    if (SAVE_ON_CARD){
                    	saveBitmap(bitmap,url);
                    }
                    imageView.setImageBitmap(bitmap);
                    //adapter.notifyDataSetChanged();
                    break;

                case NO_DOWNLOADED_DRAWABLE:
//                    imageView.setMinimumHeight(156);
                    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView,progressBar );
                    task.execute(url);
                    break;

                case CORRECT:
                    task = new BitmapDownloaderTask(imageView,progressBar );
                    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                    imageView.setImageDrawable(downloadedDrawable);
//                    imageView.setMinimumHeight(156);
                    task.execute(url);
                    break;
            }
        }
    }

    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    Bitmap downloadBitmap(String url) {
        final int IO_BUFFER_SIZE = 4 * 1024;

        BufferedInputStream bis = null;
        ByteArrayOutputStream out =null;
        
        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :
            AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
//                    Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream)); 
                    
                    bis = new BufferedInputStream(inputStream,1024 * 8);
                    out = new ByteArrayOutputStream();
                    int len=0;
                    byte[] buffer = new byte[1024];
                    while((len = bis.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    bis.close();
                    
                    byte[] data = out.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ImageView> imageViewReference;
        private ProgressBar progressBar;
        /*private BaseAdapter adapter;*/

        public BitmapDownloaderTask(ImageView imageView,ProgressBar progressBar/*, BaseAdapter adapter*/) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.progressBar = progressBar;
            /*this.adapter = adapter;*/
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadBitmap(url);
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);

            if ( SAVE_ON_CARD ) {
            	saveBitmap(bitmap,url);
            }
            
            if (imageViewReference != null) {
            	ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) {
                    imageView.setImageBitmap(bitmap);
                }
                //on cache le progessbar
                if (this.progressBar != null){
                	this.progressBar.setVisibility(View.GONE);
                }
                //on rend l'image visible
                if (bitmap != null && imageView!=null){
                	imageView.setVisibility(View.VISIBLE);
                }
                //on notify le changement des donnees
                /*if (adapter  != null){
                	adapter.notifyDataSetChanged();
                }*/
            }
        }
    }


    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.BLACK);
            bitmapDownloaderTaskReference =
                new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        clearCache();
    }
    

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache =
        new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                // Entries push-out of hard reference cache are transferred to soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
    	//Log.v(this.getClass().getName(),"Image ajouté au cache");
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String url) {
        // First try the hard reference cache
    	//Log.v(this.getClass().getName(),"Image cherchée dans le cache");
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }
 
    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }
}
