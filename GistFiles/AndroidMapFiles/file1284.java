package com.w4rlock.cacheManager;

import java.io.File;

import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.utils.StorageUtils;
 
public class FileCache {
  private static String logger_name = "[cache-manager][file-manager]";
    private File cacheDir;
 
    public FileCache(Context context){
        //Find the dir to save cached images
    	try{
    		cacheDir = StorageUtils.getCacheDirectory(context);
    	}catch(Exception e){
    		Log.d(logger_name,e.toString());
    		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"com.w4rlock.yourapp.location");
            else
                cacheDir=context.getCacheDir();
            if(!cacheDir.exists())
                cacheDir.mkdirs();
    	}
    }
 
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f; 
    }

    public String getCacheDirPath(){
    	return cacheDir.getAbsolutePath();
    }
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

    public long getSize(){
        File[] files = cacheDir.listFiles();
        long size = 0;
        if(files == null)
            return size;
        for(File f:files)
            size = size+f.length();
        return size;
    }
 
}