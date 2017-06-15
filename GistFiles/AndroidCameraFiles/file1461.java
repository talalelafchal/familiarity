package com.yifanhao.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.yifanhao.MApplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * ���л�����Ϣ
 * @author YiFanhao
 * @date 2015-4-22����2:24:50
 *
 */
public class SysEnv {

	/***Log�����ʶ**/
	private static final String TAG = SysEnv.class.getSimpleName();
	
	/***��Ļ��ʾ����**/
	private static final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
	
	/**������**/
	private static final Context context = MApplication.gainContext();
	
	/**����ϵͳ���(GT-I9100G)***/
	public static final String MODEL_NUMBER = Build.MODEL;
	
	/**����ϵͳ���(I9100G)***/
	public static final String DISPLAY_NAME = Build.DISPLAY;
	
	/**����ϵͳ�汾(4.4)***/
	public static final String OS_VERSION = Build.VERSION.RELEASE;;
	
	/**Ӧ�ó���汾***/
	public static final String APP_VERSION = getVersion();
	
	/***��Ļ���**/
	public static final int SCREEN_WIDTH = getDisplayMetrics().widthPixels;
	
	/***��Ļ�߶�**/
	public static final int SCREEN_HEIGHT = getDisplayMetrics().heightPixels;
	
	/***�����ֻ����**/
	public static final String PHONE_NUMBER = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
	
	/***�豸ID**/
	public static final String DEVICE_ID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	
	/***�豸IMEI����**/
	public static final String IMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
	
	/***�豸IMSI����**/
	public static final String IMSI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
	
	/***Activity֮����ݴ�����ݶ���Key**/
	public static final String ACTIVITY_DTO_KEY = "ACTIVITY_DTO_KEY";
	
	/**��ȡϵͳ��ʾ����***/
	public static DisplayMetrics getDisplayMetrics(){
		  WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		  windowMgr.getDefaultDisplay().getMetrics(mDisplayMetrics);
		  return mDisplayMetrics;
	}
	
	/**��ȡ����ͷ֧�ֵķֱ���***/
	public static List<Camera.Size> getSupportedPreviewSizes(Camera camera){
        Camera.Parameters parameters = camera.getParameters(); 
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        return sizeList;
	}
	
	/**
	 * ��ȡӦ�ó���汾��versionName��
	 * @return ��ǰӦ�õİ汾��
	 */
	public static String getVersion() {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "��ȡӦ�ó���汾ʧ�ܣ�ԭ��"+e.getMessage());
			return "";
		}
		
		return info.versionName;
	}
	
	/**
	 * ��ȡϵͳ�ں˰汾
	 * @return
	 */
	public static String getKernelVersion(){
		String strVersion= "";
		FileReader mFileReader = null;
		BufferedReader mBufferedReader = null;
		try {
			mFileReader = new FileReader("/proc/version");
			mBufferedReader = new BufferedReader(mFileReader, 8192);
			String str2 = mBufferedReader.readLine();
			strVersion = str2.split("\\s+")[2];//KernelVersion

			
		} catch (Exception e) {
			Log.e(TAG, "��ȡϵͳ�ں˰汾ʧ�ܣ�ԭ��"+e.getMessage());
		}finally{
			try {
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return strVersion;
	}
	
	
	/***
	 * ��ȡMAC��ַ
	 * @return
	 */
	public static String getMacAddress(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo.getMacAddress()!=null){
        	return wifiInfo.getMacAddress();
		} else {
			return "";
		}
	}
	
	/**
	 * ��ȡ����ʱ��
	 * @return ����ʱ��(��λ/s)
	 */
	public static long getRunTimes() {
		long ut = SystemClock.elapsedRealtime() / 1000;
		if (ut == 0) {
			ut = 1;
		}
		return ut;
	}
}
