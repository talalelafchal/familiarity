package com.yifanhao.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Vibrator;
import android.widget.Toast;

import com.yifanhao.androidutils.R;

/**
 * 
 * @author YiFanhao
 * @date 2015-4-21下午6:06:36
 * 
 */
public class StringUtil {

	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat dateFormater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat(
			"yyyy-MM-dd");
	/** 震动 */
	private static Vibrator mVibrator;
	private final static Pattern trimer = Pattern
			.compile("[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]");

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return new Date(sdate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.format(cal.getTime());
		String paramDate = dateFormater2.format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = dateFormater2.format(time);
		}
		return ftime;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.format(today);
			String timeDate = dateFormater2.format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 将时间戳变成字符串
	 * 
	 * @param sdate
	 * @return
	 */
	public static String toString(long sdate) {
		try {
			long lcc_time = Long.valueOf(sdate);
			String mDate = dateFormater.format(new Date(lcc_time * 1000L));
			return mDate;
		} catch (Exception e) {
			return null;
		}
	}

	public static String toString(Date sdate) {
		try {
			return dateFormater2.format(sdate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */

	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input) || "null".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(String input) {
		if (input == null || "".equals(input))
			return false;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	public static String getFormattedSnippet(String snippet) {
		if (snippet != null) {
			snippet = snippet.trim();
			int index = snippet.indexOf('\n');
			if (index != -1) {
				snippet = snippet.substring(0, index);
			}
		}
		return snippet;
	}

	/**
	 * 获取资源字符串
	 */
	public static String getString(Context context, int strResourceId) {
		return context.getResources().getString(strResourceId);
	}

	/**
	 * 随即生成字符串
	 * 
	 * @param length
	 *            生成字符串长度
	 * @return
	 */
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 转String为utf-8编码
	 * 
	 * @param key
	 * @return
	 */
	public static String toUtf8(String key) {
		String result = null;
		try {
			result = new String(key.getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 显示指定长度的String
	 * 
	 * @param key
	 * @return
	 */
	public static String toSub(String mStr, int mLength) {
		return mStr.substring(0, mLength) + "...";
	}

	/**
	 * 根据指定字符格式化字符串（换行）
	 * 
	 * @param data
	 *            需要格式化的字符串
	 * @param formatChar
	 *            指定格式化字符
	 * @return
	 */
	public static String parseTxtFormat(String data, String formatChar) {
		StringBuffer backData = new StringBuffer();
		String[] txts = data.split(formatChar);
		for (int i = 0; i < txts.length; i++) {
			backData.append(txts[i]);
			backData.append("\n");
		}
		return backData.toString();
	}

	/**
	 * 格式化名字 用于保存图像，截取url的最后一段做为图像文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String formatName(String url) {
		if (url == null || "".equals(url)) {
			return url;
		}
		int start = url.lastIndexOf("/");
		int end = url.lastIndexOf(".");
		if (start == -1 || end == -1) {
			return url;
		}
		return url.substring(start + 1, end);
	}

	/**
	 * 处理空字符串
	 * 
	 * @param str
	 * @return String
	 */
	public static String doEmpty(String str) {
		return doEmpty(str, "");
	}

	/**
	 * 处理空字符串
	 * 
	 * @param str
	 * @param defaultValue
	 * @return String
	 */
	public static String doEmpty(String str, String defaultValue) {
		if (str == null || str.equalsIgnoreCase("null")
				|| str.trim().equals("") || str.trim().equals("－请选择－")) {
			str = defaultValue;
		} else if (str.startsWith("null")) {
			str = str.substring(4, str.length());
		}
		return str.trim();
	}

	/**
	 * 请选择
	 */
	final static String PLEASE_SELECT = "请选择...";

	public static boolean notEmpty(Object o) {
		return o != null && !"".equals(o.toString().trim())
				&& !"null".equalsIgnoreCase(o.toString().trim())
				&& !"undefined".equalsIgnoreCase(o.toString().trim())
				&& !PLEASE_SELECT.equals(o.toString().trim());
	}

	/**
	 * 判读是否是正确格式
	 * 
	 * @param mobileNumber
	 * @return
	 */

	public static boolean isFormat(String content, String format) {
		boolean isFormat;
		Pattern regex = Pattern.compile(format);
		Matcher matcher = regex.matcher(content);
		isFormat = matcher.matches();
		return isFormat;
	}

	/**
	 * 判读是否是邮箱格式
	 * 
	 * @param url
	 * @return
	 */
	public static boolean checkEmail(String email) {
		return isFormat(
				email,
				"^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	}

	/**
	 * 判读是否是姓名格式
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isName(String name) {
		return isFormat(name, "[\u4E00-\u9FA5]{2,50}");
	}

	/**
	 * 判读是否是身份证格式
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		return isFormat(idCard,
				"^(\\d{6})(18|19|20)?(\\d{2})([01]\\d)([0123]\\d)(\\d{3})(\\d|X|x)?$");
	}

	public static boolean empty(Object o) {
		return o == null || "".equals(o.toString().trim())
				|| "null".equalsIgnoreCase(o.toString().trim())
				|| "undefined".equalsIgnoreCase(o.toString().trim())
				|| PLEASE_SELECT.equals(o.toString().trim());
	}

	public static boolean num(Object o) {
		int n = 0;
		try {
			n = Integer.parseInt(o.toString().trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (n > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean decimal(Object o) {
		double n = 0;
		try {
			n = Double.parseDouble(o.toString().trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (n > 0.0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 给JID返回用户名
	 * 
	 * @param Jid
	 * @return
	 */
	public static String getUserNameByJid(String Jid) {
		if (empty(Jid)) {
			return null;
		}
		if (!Jid.contains("@")) {
			return Jid;
		}
		return Jid.split("@")[0];
	}

	/**
	 * 给用户名返回JID
	 * 
	 * @param userName
	 *            用户名称
	 * @param serverName
	 *            服务器名称
	 * @return
	 */
	public static String getJidByName(String userName, String serverName) {
		if (empty(userName) || empty(serverName)) {
			return null;
		}
		return userName + "@" + serverName;
	}

	/**
	 * 根据给定的时间字符串，返回月 日 时 分 秒
	 * 
	 * @param allDate
	 *            like "yyyy-MM-dd hh:mm:ss SSS"
	 * @return
	 */
	public static String getMonthTomTime(String allDate) {
		return allDate.substring(5, 19);
	}

	/**
	 * 根据给定的时间字符串，返回月 日 时 分 月到分钟
	 * 
	 * @param allDate
	 *            like "yyyy-MM-dd hh:mm:ss SSS"
	 * @return
	 */
	public static String getMonthTime(String allDate) {
		return allDate.substring(5, 16);

	}

	public static boolean isStrNull(String str) {
		if (null == str) {
			return true;
		} else if ("".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}

	public static void showToast(Context context, String msg) {
		try {// 震动
			mVibrator = (Vibrator) context
					.getSystemService(Service.VIBRATOR_SERVICE);
			mVibrator.vibrate(80);
			// 弹框
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 清除字符串中特殊字符包括空格
	 * 
	 * @param str
	 * @return
	 */
	public static String Trim(String str) {
		Matcher m = trimer.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return [0-9]{5,9}
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(14[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 判读是否是特殊格式
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isSpec(String content) {
		return isFormat(content, "^[a-zA-Z0-9\u4E00-\u9FA5]{1,50}$");
	}

	/**
	 * 判读是否是座机号码
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isLandline(String content) {
		return isFormat(content, "^(0[0-9]{2,3})?([2-9][0-9]{6,7})?$");
	}

	/**
	 * 判读是否含有特殊符号
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isContainsSpec(String content) {
		return isFormat(content, "^[\\u4e00-\\u9fa5A-Za-z0-9]*$");

	}

	/**
	 * 下拉刷新时间
	 * 
	 * @param context
	 * @param t
	 * @return
	 */
	public static String howTimeAgo(Context context, long t) {
		String msg = "";
		long nowTime = Calendar.getInstance().getTimeInMillis();
		long time = (nowTime - t) / (60 * 1000);
		if (time > 0 && time < 60) {
			msg = time + context.getString(R.string.minuteago);
		} else if (time == 0) {
			msg = context.getString(R.string.at_now);
		}
		time = (nowTime - t) / (60 * 1000 * 60);
		if (time > 0 && time < 24) {
			msg = time + context.getString(R.string.hourago);
		}
		time = (nowTime - t) / (60 * 1000 * 60 * 24);
		if (time > 0) {
			msg = time + context.getString(R.string.dayago);
		}
		return msg;
	}

	/**
	 * 获取当前的操作系统版本
	 * 
	 * @param version
	 * @return
	 */
	public static String getClientVersionNO() {
		return android.os.Build.VERSION.RELEASE;
	}

	// 获取当前操作系统类型
	public static String getClientModel() {
		return "android";
	}

	// 获取手机型号
	public static String getClientType() {
		return android.os.Build.MODEL;
	}

	// 获取当前应用版本号
	public static String getClientVersionName(Context context) {
		String versionName = "";
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				versionName = pi.versionName == null ? "null" : pi.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	// 获取当前系统versionCode
	public static String getClientVersionCode(Context context) {
		String versionCode = "";
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				versionCode = pi.versionCode + "";
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

}
