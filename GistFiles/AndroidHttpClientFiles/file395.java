package com.example.android;

import android.app.Application;

public class App extends Application {
	
	public App() {
		Thread.setDefaultUncaughtExceptionHandler(new SentryLog("/sdcard/tmp", "http//www.miiicasa.com"));
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
}
