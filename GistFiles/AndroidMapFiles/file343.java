package com.stonete.qrtoken.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;

/**
 * Created by kangwei on 2015/3/19.
 */
public class FileUtils {

    public static String getCatchPath(Context ctx){
        File f = ctx.getExternalCacheDir();
        if (f != null)
        {
            return f.getAbsolutePath();
        }
        return null;

    }


    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        }
        return null;
    }

    public static byte[] readBytes(File f) {
        if(f.exists() && f.canRead()){
            int len = (int) f.length();
            byte[] bts = new byte[len];
            try {
                FileInputStream fips = new FileInputStream(f);
                fips.read(bts);
                fips.close();
                return bts;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return null;
    }

    public static byte[] readBytes(String f) {

        return readBytes(new File(f));
    }

    public static boolean writeByte2File(byte[] encoded, String fileName) {
        File file = new File(fileName);

        try {
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(encoded);
            fops.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String read(String s) {
        byte[] bts = readBytes(new File(s));
        if(bts == null){
            return null;
        }
        return new String(bts);
    }
}
