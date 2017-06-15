import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// Create global configuration and initialize ImageLoader with this
		// configuration

		File cacheDir = StorageUtils.getCacheDirectory(getBaseContext());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
		        .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
		        .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
		        .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
		        .threadPoolSize(3) // default
		        .threadPriority(Thread.NORM_PRIORITY - 1) // default
		        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
		        .denyCacheImageMultipleSizesInMemory()
		        .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // default
		        .memoryCacheSize(2 * 1024 * 1024)
		        .discCache(new UnlimitedDiscCache(cacheDir)) // default
		        .discCacheSize(50 * 1024 * 1024)
		        .discCacheFileCount(100)
		        .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
		        .imageDownloader(new BaseImageDownloader(getBaseContext())) // default
		        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
		        .build();
		ImageLoader.getInstance().init(config);
	}
}