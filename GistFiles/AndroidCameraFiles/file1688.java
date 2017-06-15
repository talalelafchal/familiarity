package com.model.utility;

import android.content.SharedPreferences;

public class ConfigurationManager {
	private static SharedPreferences settings = null;
	
	public static final String mConfigFileName = "info_cache";
	
	public static void setSharedPreference (SharedPreferences setting) {
		settings = setting;
	}

	public static void write_Cache(String contents){
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("odls", contents);
		editor.commit();
	}
	
	public static String read_Cache(){
		String content = settings.getString("odls", null);
		return content;
	}
}