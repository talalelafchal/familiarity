package com.the9.playme.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.the9.playme.R;
import com.the9.playme.annotation.Ignore;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();
	
	private static final String CHARSET = "UTF-8";
	
	/**
	 * 将字符串经MD5转换后返回
	 * 
	 * @param originalString
	 *            原始字符串
	 * @return 经MD5转换后产生的字符串
	 */
	public static String getMD5String(String originalString) {
		try {
			// 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			// 输入的字符串转换成字节数组
			byte[] inputByteArray = originalString.getBytes(CHARSET);
			// inputByteArray是输入字符串转换得到的字节数组
			messageDigest.update(inputByteArray);
			// 转换并返回结果，也是字节数组，包含16个元素
			byte[] resultByteArray = messageDigest.digest();
			// 字符数组转换成字符串返回
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将byte数组转化为小写十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String byteArrayToHex(byte[] byteArray) {
		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
		char[] resultCharArray = new char[byteArray.length * 2];
		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		// 字符数组组合成字符串返回
		return new String(resultCharArray);
	}

	/**
	 * 获取对象中的所有属性，将属性的名称作为key,属性的值转化为String后作为value，依次放入到一个NavigableMap中并将其返回
	 * 
	 * @param obj
	 * @return 包含所有属性键值对的NavigableMap，不会为null
	 */
	public static NavigableMap<String, String> reflectToSortedMap(Object obj) {
		NavigableMap<String, String> map = new TreeMap<String, String>();
		if (obj == null) {
			return map;
		}
		
		for (Class<?> c = obj.getClass(); c != Object.class; c = c.getSuperclass()) {
			Field[] fields = c.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				// 如果有@Ignore的Annotation则跳过
				if (fields[j].getAnnotation(Ignore.class) != null) {
					continue;
				}
				fields[j].setAccessible(true);
				// 字段名
				String key = fields[j].getName();
				// 字段值
				String value = null;
				try {
					if (fields[j].getType().getName()
							.equals(java.lang.String.class.getName())) {
						// String type
						value = (String) fields[j].get(obj);
//						String temp = (String) fields[j].get(obj);
//						if (temp != null) {
//							// 把中文转换成%xy的形式
//							value = URLEncoder.encode(temp, CHARSET);
//						}
					} else if (fields[j].getType().getName()
							.equals(java.lang.Integer.class.getName())
							|| fields[j].getType().getName().equals("int")) {
						// Integer type
						value = String.valueOf(fields[j].getInt(obj));
					} else if (fields[j].getType().getName()
							.equals(java.lang.Long.class.getName())
							|| fields[j].getType().getName().equals("long")) {
						// Integer type
						value = String.valueOf(fields[j].getLong(obj));
					} else if (fields[j].getType().getName()
							.equals(java.lang.Float.class.getName())
							|| fields[j].getType().getName().equals("float")) {
						// Float type
						value = String.valueOf(fields[j].getFloat(obj));
					} else if (fields[j].getType().getName()
							.equals(java.lang.Double.class.getName())
							|| fields[j].getType().getName().equals("double")) {
						// Float type
						value = String.valueOf(fields[j].getDouble(obj));
					} else {
						// 其他类型暂不支持
						throw new IllegalArgumentException("其他类型暂不支持");
					}
					if (value != null) {
						map.put(key, value);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} finally {
					fields[j].setAccessible(false);
				}
			}
		}
		return map;
	}

	/** 
     * 使用DecimalFormat,保留小数点后两位 
     */  
    public static String format2(double value) {  
        DecimalFormat df = new DecimalFormat("#%");  
        df.setRoundingMode(RoundingMode.HALF_UP);  
        return df.format(value);  
    }  

	/**
	 * 获取VersionName
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return context.getString(R.string.version_unknown);
		}
	}

	/**
	 * 获取VersionCode(内部识别号)
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 是否有sd卡
	 * 
	 * @return
	 */
	public static boolean isSdCardMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 查看SD卡的剩余空间(MB)
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getSdFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}
	
	/**
	 * 查看SD卡总容量(MB)
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getSdAllSize(){
		//取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory(); 
		StatFs sf = new StatFs(path.getPath()); 
		//获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize(); 
		//获取所有数据块数
		long allBlocks = sf.getBlockCount();
		//返回SD卡大小
		//return allBlocks * blockSize; //单位Byte
		//return (allBlocks * blockSize)/1024; //单位KB
		return (allBlocks * blockSize) / 1024 / 1024; //单位MB
	}
	
	/**
	 * 获得应用存储目录
	 * @param context
	 * @return
	 */
	public static String getRepositoryPath(Context context){
		String path;
		if(isSdCardMounted()){
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		}else{
			path = context.getFilesDir().getAbsolutePath();
		}
		return path + File.separator + context.getPackageName() + File.separator;
	}
	
	public static File getRepositoryFile(Context context, String name){
		return getRepositoryFile(context, null, name);
	}
	
	public static File getRepositoryFile(Context context, String dir, String name){
		String fullDir = getRepositoryPath(context);
		if(dir != null){
			fullDir += dir;
		}
		File dirFile = new File(fullDir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		
		File file = new File(dirFile, name);
		return file;
	}
	
	public static String getRepositoryFilePath(Context context, String dir, String name){
		return getRepositoryFile(context, dir, name).getAbsolutePath();
	}
	
	/**
	 * 手机号是否合法
	 * @param number
	 * @return
	 */
	public static boolean isMobilePhoneNumberValid(String number){
		if(!TextUtils.isEmpty(number) && number.length() == 11){
			return true;
		}
		return false;
	}
	
	/**
	 * 隐藏虚拟键盘
	 * @param v
	 */
	public static void hideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}
	
	/**
	 * 显示虚拟键盘
	 * @param v
	 */
	public static void showKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}
	
	/**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
	public static int readImageRotationDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/**
	 * 旋转图片 
	 * @param degrees 
	 * @param bitmap 
	 * @return Bitmap 
	 */ 
	public static Bitmap rotaingBitmap(int degrees , Bitmap bitmap) {  
		// 旋转图片 动作   
		Matrix matrix = new Matrix();;  
		matrix.postRotate(degrees);  
		// 创建新的图片   
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
		return resizedBitmap;  
	}
	
	/**
	 * 压缩图片尺寸
	 * @return
	 */
	public static Bitmap sampleBitmap(Context context, Uri uri,
			int width, int height){
		
		Bitmap bitmap = null;
		//先量尺寸，如果太大，要作sample，否则会报OutOfMemory错误
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri), null, options);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return bitmap;
		}
		Log.i(TAG, "Original Width = " + options.outWidth);
		Log.i(TAG, "Original Height = " + options.outHeight);
		
		int sampleWidth = 0;
		int sampleHeight = 0;
		if(options.outWidth < options.outHeight){
			if(width < height){
				sampleWidth = options.outWidth / width;
				sampleHeight = options.outHeight / height;
			}else{
				sampleWidth = options.outWidth / height;
				sampleHeight = options.outHeight / width;
			}
		}else{
			if(width < height){
				sampleWidth = options.outHeight / width;
				sampleHeight = options.outWidth / height;
			}else{
				sampleWidth = options.outHeight / height;
				sampleHeight = options.outWidth / width;
			}
		}
		int sampleSize = Math.max(sampleWidth, sampleHeight);
		options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri), null, options);
			Log.i(TAG, "Scaled Width = " + bitmap.getWidth());
			Log.i(TAG, "Scaled Height = " + bitmap.getHeight());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static File bmpToFile(final Bitmap bmp, final String filePath){
		File file = new File(filePath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static byte[] getHtmlByteArray(final String url) {
		 URL htmlUrl = null;     
		 InputStream inStream = null;     
		 try {         
			 htmlUrl = new URL(url);         
			 URLConnection connection = htmlUrl.openConnection();         
			 HttpURLConnection httpConnection = (HttpURLConnection)connection;         
			 int responseCode = httpConnection.getResponseCode();         
			 if(responseCode == HttpURLConnection.HTTP_OK){             
				 inStream = httpConnection.getInputStream();         
			  }     
			 } catch (MalformedURLException e) {               
				 e.printStackTrace();     
			 } catch (IOException e) {              
				e.printStackTrace();    
		  } 
		byte[] data = inputStreamToByte(inStream);

		return data;
	}
	
	public static byte[] inputStreamToByte(InputStream is) {
		try{
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String readTextFromAssets(Context context, String fileName){
		byte[] buffer = null;
		try {
			InputStream is = context.getResources().getAssets().open(fileName);
			buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String text = null;
		if(buffer != null){
			try {
				text = new String(buffer, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				text = "";
			}
		}else{
			text = "";
		}
		return text;
	}
	
	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

		if(offset <0){
			Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if(len <=0 ){
			Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if(offset + len > (int) file.length()){
			Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // 创建合适文件大小的数组
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * 判断TextView是否Ellipsized
	 * @param textView
	 * @return
	 */
	public static boolean isTextViewEllipsized(TextView textView){
		boolean isEllipsized = false;
		Layout layout = textView.getLayout();
		if(layout != null) {
		    int lines = layout.getLineCount();
		    if(lines > 0) {
		        int ellipsisCount = layout.getEllipsisCount(lines-1);
		        if ( ellipsisCount > 0) {
//		            Log.d(TAG, "Text is ellipsized");
		            isEllipsized = true;
		        } 
		    } 
		}
		return isEllipsized;
	}
	
	/**
	 * 格式化货币
	 * @param value 货币值，单位为分
	 * @return
	 */
	public static String formatMoneyInCents(int value){
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(value / 100.0f);
	}
	
	/**
	 * 格式化货币
	 * @param currency 货币类型
	 * @param value 货币值，单位为分
	 * @return
	 */
	public static String formatMoneyInCentsWithCurrencySign(
			Currency currency, int value){
		return currency.getSymbol() + formatMoneyInCents(value);
	}
	
	public static int generateNonNegativeRandomInteger(int max){
		Random random = new Random();
		return random.nextInt(max);
	}
	
	public static String generateIntentActionName(Context context, String action){
		return context.getPackageName() + "_" + action;
	}
}
