package com.yifanhao.utils;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;

/**
 * Intent跳转
 * @author YiFanhao
 * @date 2015-4-21下午4:13:24
 *
 */  
public class IntentUtil {
	/**
	 * 
	 * @param activity 当前Activity
	 * @param cls 跳转Activity
	 * @param name 传递的参数
	 */
	public static void start_activity(Activity activity, Class<?> cls,
			BasicNameValuePair... name) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		for (int i = 0; i < name.length; i++) {
			intent.putExtra(name[i].getName(), name[i].getValue());
		}
		activity.startActivity(intent);
	}

}
