package com.w4rlock.cacheManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import android.content.Context;
import android.util.Log;

public class CacheManager extends FileCache{
  private static String logger_name = "[cache-manager]";
	public CacheManager(Context context) {
		super(context);

	}
	
	public String getFileNameFromUrl(String url){
		try{
			File f = this.getFile(url);
			return f.getAbsolutePath();
		}
		catch(Exception e){
			return null;
		}
	}
	
	public Boolean checkFileFromUrl(String url){
		try {
			if((this.getFile(url)).exists()){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public String readFromUrl(String url) throws IOException{
		String res1 = "";
		File f = this.getFile(url);
		if (f.exists()) {
			FileReader fr = new FileReader(f);
			if (fr.read() == -1) {
				throw new IOException("Unable to read file");
			} else {
				try {
					res1 = readFromFile(f.getAbsolutePath());
				} catch (IOException e) {
					Log.d(logger_name,"error while reading cache file after its not empty "+ e.toString());
					throw e;
				}
			}
		} else {
			throw new IOException("DOES NOT EXISTS");
		}
		return res1;
	}
	
	public String readFromFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}
	
	public Boolean addtoCacheFile(String string, String url) throws IOException {
		PrintWriter out = null;
		   try {
			    File f = this.getFile(url);
			    out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath(), false)));
		   } catch (IOException e) {
			    throw e;
			}
		   if (out != null)
	    		out.println(string);
		   if (out !=null)
		    	out.close();
		return true;
	}
}