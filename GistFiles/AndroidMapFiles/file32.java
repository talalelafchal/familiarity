package com.sugaishun.sample;

import java.util.HashMap;

import android.graphics.drawable.Drawable;

public class ImageCache {
	private static HashMap<String, Drawable> cache = new HashMap<String, Drawable>();
	
	public static Drawable get(String key) {
		if (cache.containsKey(key)) 
			return cache.get(key);
		return null;
	}
	
	public static void set(String key, Drawable Image) {
		cache.put(key, Image);
	}
	
	public static void clear() {
		cache = null;
		cache = new HashMap<String, Drawable>();
	}
}
