package com.example.wisatajogja;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

public class ImageUtils
{
	public static ImageLoaderConfiguration	imgLoaderConf;
	public static DisplayImageOptions		imgOptions;

	private static File getCacheDir(Context ctx)
	{
		return StorageUtils.getCacheDirectory(ctx);
	}

	/**
	 * configurasi untuk imageloader
	 * 
	 * @param ctx
	 *            Context
	 * @return imgLoaderConfiguration
	 */
	public static ImageLoaderConfiguration getImgConfig(Context ctx)
	{
		imgLoaderConf = new ImageLoaderConfiguration.Builder(ctx)
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.discCache(new UnlimitedDiscCache(getCacheDir(ctx)))
				.writeDebugLogs().build();
		return imgLoaderConf;
	}

	/**
	 * image options untuk imageloader
	 * 
	 * @return imgOptions
	 */
	public static DisplayImageOptions getImgOpt()
	{
		imgOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher).cacheOnDisc(true)
				.cacheInMemory(true).delayBeforeLoading(10).build();
		return imgOptions;
	}

}
