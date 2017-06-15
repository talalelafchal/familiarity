//参考 https://github.com/sharakova/UrlImageView
// コピペしやすいように単一ファイルにまとめた、だけ
// 元ソースにならって　MIT ライセンス

package com.mizchi.android;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import android.util.Log;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlImageView extends ImageView {
	private Context context;
	private Runnable mEndListener;
	private Runnable mStartListener;

	public UrlImageView(Context context) {
		super(context);
		this.context = context;
	}

	public UrlImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public UrlImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void setOnLoadStartRunnable (Runnable runable) {
		mStartListener = runable;
	}

	public void setOnLoadEndRunnable (Runnable runnable) {
		mEndListener = runnable;
	}

	public void setImageUrl(String Url) {
		SoftReference<Bitmap> image = ImageCache.getImage(this.context,Url);
		if(image != null && image.get() != null) {
			setImageBitmap(image.get());
			return;
		}
		if (mStartListener != null) {
			mStartListener.run();
		}
		ImageDownloadTask task = new ImageDownloadTask();
		task.execute(Url);
	}

	private class ImageDownloadTask extends AsyncTask<String, Void, SoftReference<Bitmap>> {
	    @Override
	    protected SoftReference<Bitmap> doInBackground(String... urls) {
	    	SoftReference<Bitmap> image = null;
	    	try {
       			image = HttpClient.getImage(urls[0]);
	            ImageCache.setImage(context, urls[0], image);
	            return image;
	    	} catch (Exception e) {
				e.printStackTrace();
	    	} catch (OutOfMemoryError e) {
	    		e.printStackTrace();
	    	}
			return null;
	    }

	    @Override
	    protected void onPostExecute(SoftReference<Bitmap> result) {
	    	if(result != null && result.get() != null) {
	    		setImageBitmap(result.get());
	    	}

	    	if(mEndListener != null) {
	    		mEndListener.run();
	    	}
	    }
	}
}

class ImageCache {  
    private static HashMap<String,SoftReference<Bitmap>> cache = new HashMap<String,SoftReference<Bitmap>>();  
      
    public static SoftReference<Bitmap> getImage(Context context, String key) {
    	Log.d("ImageCache::getImage", key);
    	SoftReference<Bitmap> bitmap = cache.get(key);
    	if (bitmap == null || bitmap.get() == null) {
    		bitmap = CacheUtils.getFile(context, key);
        }
        return bitmap;
    }
      
    public static void setImage(Context context, String key, SoftReference<Bitmap> bitmap) {
    	try {
	    	Log.d("ImageCache::setImage", key);
	        cache.put(key, bitmap);
	        CacheUtils.saveBitmap(context, key, bitmap);
    	} catch (Exception e) {
    		e.printStackTrace();
    	} catch (OutOfMemoryError e) {
    		e.printStackTrace();
    	}
    }
    
    public static void clear(Context context) {
    	cache.clear();
    	CacheUtils.deleteAll(context);
    }
}
class CacheUtils {

	private static String getFileName(String url) {
		int hash = url.hashCode();
		return String.valueOf(hash);
	}

	public static void saveByteData (Context context, String url, byte[] w) {
		String fileName = getFileName(url);
		FileOutputStream fos = null;
		try {
			File file = new File(context.getCacheDir(), fileName);
			fos = new FileOutputStream(file);
			fos.write(w);
			fos.close();
		} catch (Exception e) {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					Log.w("save", "finally");
				}
			}
		}
	}

	public static void saveBitmap(Context context, String url, SoftReference<Bitmap> bitmap) {
		String fileName = getFileName(url);
		FileOutputStream fos = null;
		try {
			File file = new File(context.getCacheDir(), fileName);
			fos = new FileOutputStream(file);
			bitmap.get().compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (Exception e) {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					Log.w("save", "finally");
				}
			}
		}
	}

	public static SoftReference<Bitmap> getFile(Context context, String url) {
		String fileName = getFileName(url);
		SoftReference<Bitmap> bitmap = null;
		try {
			String filePath = context.getCacheDir() + "/" + fileName;
			bitmap = new SoftReference<Bitmap>(BitmapFactory.decodeFile(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static void deleteAll(Context context) {
		File dir = context.getCacheDir();
		if (!dir.isDirectory()){
			return;
		}
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++){
			File file = files[i];
			if (file.isFile()){
				file.delete();
			}
		}
	}
}

class HttpClient {
    public static SoftReference<Bitmap> getImage(String url) {
   		byte[] byteArray = getByteArrayFromURL(url);
    	return new SoftReference<Bitmap>(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }
    
    private static byte[] getByteArrayFromURL(String strUrl) {
        byte[] byteArray = new byte[1024];
        byte[] result = null;
        HttpURLConnection con = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        int size = 0;
        try {
            URL url = new URL(strUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            in = con.getInputStream();

            out = new ByteArrayOutputStream();
            while ((size = in.read(byteArray)) != -1) {
                out.write(byteArray, 0, size);
            }
            result = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (con != null)
                    con.disconnect();
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return result;
    }

}
