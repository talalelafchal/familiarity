package com.anenda.smarthome.util;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.anenda.smarthome.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Wen_en on 2015/11/13.
 */
public class FileHelper {

	public static final String GALLERY_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/DCIM/Camera/";
	public static final String AVATAR_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
    		+ "/smart/avatars/";
	public static final String PICTURE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
    		+ "/smart/pictures/";
	public static final String VIDEO_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
    		+ "/smart/videos/";
	public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
    		+ "/smart/download/";
	
    private static FileHelper mInstance = new FileHelper();

    public static FileHelper getInstance() {
        return mInstance;
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
    public static File createGalleryDir() {
		File galleryDir = new File(GALLERY_DIR);
		if (!galleryDir.exists()) {
			galleryDir.mkdirs();
		}
		return galleryDir;
    }

    public static File createAvatarDir() {
		File avatarDir = new File(AVATAR_DIR);
		if (!avatarDir.exists()) {
			avatarDir.mkdirs();
		}
		return avatarDir;
    }
    
    public static File createPictureDir() {
		File pictureDir = new File(PICTURE_DIR);
		if (!pictureDir.exists()) {
			pictureDir.mkdirs();
		}
		return pictureDir;
    }
    
    public static File createVideoDir() {
		File videoDir = new File(VIDEO_DIR);
		if (!videoDir.exists()) {
			videoDir.mkdirs();
		}
		return videoDir;
    }
    
    public static File createDownloadDir() {
		File downloadDir = new File(DOWNLOAD_DIR);
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		return downloadDir;
    }
    
    public static String createAvatarPath(String userName) {
        createAvatarDir();
        File file;
        if (userName != null) {
            file = new File(AVATAR_DIR, userName + ".png");
        }else {
            new DateFormat();
			file = new File(AVATAR_DIR, DateFormat.format("yyyy_MMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA)) + ".png");
        }
        return file.getAbsolutePath();
    }

    public static String getUserAvatarPath(String userName) {
        return AVATAR_DIR + userName + ".png";
    }


    public interface CopyFileCallback {
        public void copyCallback(Uri uri);
    }

    /**
     * 复制后裁剪文件
     * @param file 要复制的文件
     */
    public void copyAndCrop(final File file, final Activity context, final CopyFileCallback callback) {
        if (isSdCardExist()) {
            final Dialog dialog = DialogCreator.createLoadingDialog(context,
                    context.getString(R.string.loading));
            dialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        String path = createAvatarPath(AVATAR_DIR);
                        final File tempFile = new File(path);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        byte[] bt = new byte[1024];
                        int c;
                        while((c = fis.read(bt)) > 0) {
                            fos.write(bt,0,c);
                        }
                        //关闭输入、输出流
                        fis.close();
                        fos.close();

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.copyCallback(Uri.fromFile(tempFile));
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
            thread.start();
        }else {
            Toast.makeText(context, context.getString(R.string.sdcard_not_exist_toast), Toast.LENGTH_SHORT).show();
        }
    }
}
