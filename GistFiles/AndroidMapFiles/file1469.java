package com.w4rlock.cacheManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class UrlCacheManager extends getURLdata {
  private static final String DEBUG_TAG = "[cache-manager][url-cache-manager]";
	
	private CacheManager cm=null;
	private Context context;
	private Boolean reload = false;
	private String url;
	public UrlCacheManager(Context context) {
		this.context = context;
		cm = new CacheManager(context);
	}

	public Boolean getReload() {
		return reload;
	}

	public void setReload(Boolean reload) {
		this.reload = reload;
	}

	@Override
	public void getData(String url) {
		setUrl(url);
		if (reload) {
			if (cm.checkFileFromUrl(url)) {
				goOnFile(url);
			} else {
				goOnNetwork(url);
			}
			new DownloadWebpageText().execute(url);
		} else {
			goOnFile(url);
		}
	}
	
	private void setUrl(String url){
		try{
			this.url = url;
		}catch(Exception e){
			this.url = new String(url);
		}
	}
	protected void goOnFile(String url) {
		try {
			String result2 = cm.readFromUrl(url);
			postresult(result2);
		} catch (Exception e) {
			Log.d(DEBUG_TAG, "error while reading file " + e.toString());
			goOnNetwork(url);
		}
	}

	protected void goOnNetwork(String url) {
		if (!getNetStatus()) {
			onTimeOut("no network");
		} else {
			new DownloadWebpageText().execute(url);
		}
	}

	@Override
	protected void postresult_internal(Object result) {
		try{
			postresult(result);
			CacheManager cm2 = new CacheManager(this.context);
			cm2.addtoCacheFile((String)result, url);
		}catch(Exception e){
			Log.d(DEBUG_TAG,"unable to write to cache file "+e.toString());
		}
	}
	
	protected Boolean getNetStatus(){
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()){
      		Log.d(DEBUG_TAG, "loading data");
    		try{
    			Log.d(DEBUG_TAG,"connecting");
    			return true;
    		}catch(Exception e){
    			return false;
    		}
    	}else {
    		return false;
    	}
	}
}