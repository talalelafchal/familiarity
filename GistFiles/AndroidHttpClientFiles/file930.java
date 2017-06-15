package com.example.wisatajogja;

import android.util.Log;

public class Utils
{
	public static void TRACE(String tag, String msg)
	{
		if (BuildConfig.DEBUG)
		{
			Log.d(tag, msg);
		}
	}

}
