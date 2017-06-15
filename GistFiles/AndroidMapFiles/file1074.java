import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is just a rewritten version of Gilles Debunne's ImageDownloader class, rewritten to handle files from disk instead
 * 
 * This helper class reads images from file and binds those with the provided ImageView.
 *
 * <p>It requires the WRITE_EXTERNAL_STORAGE permission, which should be added to your application's manifest
 * file.</p>
 *
 * A local cache of read images is maintained internally to improve performance.
 *
 * @author Joar Gullestad Pettersen, joargp@gmail.com
 */
public class ImageReader {
	
	
    private static final String LOG_TAG = "ImageReader";
	private static final int IMAGE_MAX_SIZE = 600; // maximum width and height of output Bitmap 

    public enum Mode { NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT }
    private Mode mode = Mode.CORRECT;
    
    
    /**
     * Reads the specified image from file and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param path the path to the file i.e. /mnt/sdcard/images/image.jpg
     * @param imageView The ImageView to bind the read image to.
     */
    public void readFile(String path, ImageView imageView) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(path);

        if (bitmap == null) {
            forceFileRead(path, imageView);
        } else {
            cancelPotentialFileRead(path, imageView);
            imageView.setImageBitmap(bitmap);
        }
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
     */
    private void forceFileRead(String path, ImageView imageView) {
        // State sanity: path is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (path == null) {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialFileRead(path, imageView)) {
   
        	BitmapFileReadTask task = new BitmapFileReadTask(imageView);
        	FileReadDrawable fileReadDrawable = new FileReadDrawable(task);
        	imageView.setImageDrawable(fileReadDrawable);
        	imageView.setMinimumHeight(156);
        	task.execute(path);
        }
    }

    /**
     * Returns true if the current file reading has been canceled or if there was file read in
     * progress on this image view.
     * Returns false if the file read in progress deals with the same path. The file reading is not
     * stopped in that case.
     */
    private static boolean cancelPotentialFileRead(String url, ImageView imageView) {
        BitmapFileReadTask bitmapFileReadTask = getBitmapFileReadTask(imageView);

        if (bitmapFileReadTask != null) {
            String bitmapUrl = bitmapFileReadTask.path;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapFileReadTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any ImageView
     * @return Retrieve the currently active file read task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapFileReadTask getBitmapFileReadTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof FileReadDrawable) {
                FileReadDrawable fileReadDrawable = (FileReadDrawable)drawable;
                return fileReadDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }
	
    Bitmap decodeFile(File f){
		Bitmap b = null;
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis;
			fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inPurgeable = true;
			o2.inSampleSize = scale;
			o2.inDither=false;            
			o2.inInputShareable=true;              
			o2.inTempStorage=new byte[32 * 1024];
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
		}
		return b;
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
    class BitmapFileReadTask extends AsyncTask<String, Void, Bitmap> {
        private String path;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapFileReadTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            path = params[0];
            return decodeFile(new File(path));
        }

        /**
         * Once the image is read, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(path, bitmap);

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapFileReadTask bitmapFileReadTask = getBitmapFileReadTask(imageView);
                // Change bitmap only if this process is still associated with it
                if ((this == bitmapFileReadTask)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    
    /**
     * A fake Drawable that will be attached to the imageView while the file read is in progress.
     *
     * <p>Contains a reference to the actual file read task, so that a file read task can be stopped
     * if a new binding is required, and makes sure that only the last started the file reading process can
     * bind its result, independently of the download finish order.</p>
     */
    static class FileReadDrawable extends ColorDrawable {
        private final WeakReference<BitmapFileReadTask> bitmapFileReadTaskReference;
   
        public FileReadDrawable(BitmapFileReadTask bitmapFileReadTask) {
            super(Color.BLACK);
            bitmapFileReadTaskReference =
                new WeakReference<BitmapFileReadTask>(bitmapFileReadTask);
        }

		public BitmapFileReadTask getBitmapDownloaderTask() {
            return bitmapFileReadTaskReference.get();
        }
    }


    public void setMode(Mode mode) {
        this.mode = mode;
        clearCache();
    }

    
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */
    
    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    @SuppressWarnings("serial")
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
        @Override
		public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly read bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
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
    	if (url == null) return null;
        // First try the hard reference cache
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