package com.cndatacom.pmg.application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Application;
import android.util.DisplayMetrics;

import com.baidu.mapapi.SDKInitializer;
import com.cndatacom.pmg.album.ImageUtil;
import com.cndatacom.pmg.db.DataBase;
import com.cndatacom.pmg.file.DownloadTask;
import com.cndatacom.pmg.file.DownloadTaskHelper;
import com.cndatacom.pmg.model.OperatorTypeForm;
import com.cndatacom.pmg.util.Constants;
import com.cndatacom.pmg.util.UrlConstants;

public class UIApplication extends Application implements DownloadTaskHelper {
	public static int currentNetwork = Constants.NETWORK_TYPE_MOBILE;
	private Map<String, Integer> notificationIds = new HashMap<String, Integer>(); // 用于通知栏显示新消息推送

	/** 屏幕宽高，标题栏高度 */
	private int displayWidth;
	private int displayHeight;
	private int statusBarHeight;
	/** 登陆获取定位信息 */
	private String currProvince;
	private String currCity;
	public static boolean isLogin;

	/** 运营商列表 */
	private List<OperatorTypeForm> mOperatorList = new ArrayList<OperatorTypeForm> ();
	/** 当前选择的运营商 */
	private OperatorTypeForm mCurrentOperator = new OperatorTypeForm(
			Constants.OPERATOR_ALL_ID, Constants.OPERATOR_ALL_NAME);
	/** 上次选择的运营商 */
	private OperatorTypeForm mPreviousOperator = new OperatorTypeForm(
			Constants.OPERATOR_ALL_ID, Constants.OPERATOR_ALL_NAME);

	/** 微聊下载语音|图片任务集合 */
	private Set<String> chatFileList = new HashSet<String>(); // 微聊中，正在下载的语音|图片的路径集合

	public Set<String> getChatFileList() {
		return chatFileList;
	}

	public void setChatFileList(Set<String> chatFileList) {
		this.chatFileList = chatFileList;
	}

	@Override
	public void onCreate() {
		getParameters();
		super.onCreate();
		ImageUtil.initImageLoader(this, UrlConstants.SERVERURL);
		SDKInitializer.initialize(getApplicationContext());
		DataBase dBase = new DataBase(getApplicationContext());
		dBase.close();

		// startService(new Intent(this, NetworkStateMonitorService.class)); // 暂时先不使用

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		
		mOperatorList.add(new OperatorTypeForm(
				Constants.OPERATOR_ALL_ID, Constants.OPERATOR_ALL_NAME));
	}

	/**
	 * 获取屏幕宽高等参数
	 */
	private void getParameters() {
		// DisplayMetrics displayMetrics = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		DisplayMetrics displayMetrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		displayWidth = displayMetrics.widthPixels;
		displayHeight = displayMetrics.heightPixels;
		//
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		statusBarHeight = sbar;
	}

	public Map<String, Integer> getNotificationIds() {
		return notificationIds;
	}

	public void setNotificationIds(Map<String, Integer> notificationIds) {
		this.notificationIds = notificationIds;
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public int getStatusBarHeight() {
		return statusBarHeight;
	}

	public String getCurrProvince() {
		return currProvince;
	}

	public void setCurrProvince(String currProvince) {
		this.currProvince = currProvince;
	}

	public String getCurrCity() {
		return currCity;
	}

	public void setCurrCity(String currCity) {
		this.currCity = currCity;
	}

	private Map<String, DownloadTask> mDownloadMap = new HashMap<String, DownloadTask>();

	@Override
	public Map<String, DownloadTask> getDownloadMap() {
		return this.mDownloadMap;
	}

	@Override
	public void setDownloadMap(Map<String, DownloadTask> downloadMap) {
		this.mDownloadMap = downloadMap;
	}

	public List<OperatorTypeForm> getmOperatorList() {
		return mOperatorList;
	}

	public void addmOperatorList(List<OperatorTypeForm> mOperatorList) {
		this.mOperatorList.addAll(mOperatorList);
	}

	public OperatorTypeForm getmCurrentOperator() {
		return mCurrentOperator;
	}

	public void setmCurrentOperator(OperatorTypeForm mCurrentOperator) {
		this.mCurrentOperator = mCurrentOperator;
	}

	public OperatorTypeForm getmPreviousOperator() {
		return mPreviousOperator;
	}

	public void setmPreviousOperator(OperatorTypeForm mPreviousOperator) {
		this.mPreviousOperator = mPreviousOperator;
	}

}
