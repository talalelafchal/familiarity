package com.yifanhao.utils;

import java.util.HashSet;

/**
 *  
 * @author YiFanhao
 * @date 2015-4-21下午3:08:12
 *  
 */
public class BooleanUtil {
	/**
	 * 布尔真值
	 */
	public static final Boolean TRUE = Boolean.TRUE;
	/**
	 * 布尔假值
	 */
	public static final Boolean FALSE = Boolean.FALSE;

	private static final String[] VALID_TRUE = { "True", "true", "TRUE", "Yes",
			"yes", "YES", "On", "on", "ON", "T", "t", "Y", "y", "1" };

	private static final String[] VALID_FALSE = { "False", "false", "FALSE",
			"No", "no", "NO", "Off", "off", "OFF", "F", "f", "N", "n", "0" };

	private static final HashSet<String> TRUE_SET = new HashSet<String>(
			VALID_TRUE.length);
	private static final HashSet<String> FALSE_SET = new HashSet<String>(
			VALID_FALSE.length);

	static {
		for (int i = 0; i < VALID_TRUE.length; i++) {
			TRUE_SET.add(VALID_TRUE[i]);
		}
		for (int i = 0; i < VALID_FALSE.length; i++) {
			FALSE_SET.add(VALID_FALSE[i]);
		}
	}

	/**
	 * 将字符串型转成布尔对象
	 * 
	 * @return [Boolean] 其它则返回 <code>null</code>
	 */
	public static final Boolean toBoolean(String value) {
		if (TRUE_SET.contains(value)) {
			return TRUE;
		} else if (FALSE_SET.contains(value)) {
			return FALSE;
		} else {
			return null;
		}
	}

	/**
	 * 将字符串型转成布尔对象
	 * 
	 * @param value
	 *            以下是可以接受的字符串：<br>
	 * @return [Boolean] 其它则返回 <code>#toBoolean(defValue)</code>
	 */
	public static final Boolean toBoolean(String value, boolean defValue) {
		Boolean bool = toBoolean(value);
		return bool != null ? bool : toBoolean(defValue);
	}

	/**
	 * 将字符串型转成布尔对象
	 * 
	 * @param value
	 *            以下是可以接受的字符串：<br>
	 * @return [Boolean] 其它则返回 <code>defValue</code>
	 */
	public static final boolean toBool(String value, boolean defValue) {
		return TRUE_SET.contains(value) || !FALSE_SET.contains(value)
				&& defValue;
	}

	/**
	 * 将字符串型转成布尔对象
	 * 
	 * @param value
	 *            以下是可以接受的字符串：<br>
	 * @return [Boolean] 其它则返回 <code>defValue</code>
	 */
	public static final boolean toBool(String value) {
		return toBool(value, false);
	}

	/**
	 * 将布尔值转换成布尔对象
	 * 
	 * @param value
	 *            布尔值
	 * @return [Boolean] 对应的布尔对象
	 */
	public static final Boolean toBoolean(boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * 将布尔值转换成字符串
	 * 
	 * @param value
	 *            布尔值
	 * @return 字符串
	 */
	public static final String toString(boolean value) {
		return String.valueOf(value);
	}
}
