package com.example.android;

import android.util.Log;

public class Logger {

	private final static String TAG = "android_test";

	private static String _message(String message, String tag) {
		return "[" + tag + "] " + message;
	}

	public static void v(String message, String tag) {
		Log.v(TAG, _message(message, tag));
	}

	public static void e(String message, String tag) {
		Log.e(TAG, _message(message, tag));
	}
	
	public static void i(String message, String tag) {
		Log.i(TAG, _message(message, tag));
	}
}
