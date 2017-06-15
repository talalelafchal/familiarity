package com.sm.commoncare.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * 枚举工具类
 *
 */
public class EnumUtil {

	/**
	 * 判断枚举值是否存在
	 * 
	 * @Author: liangqing@hsyuntai.com
	 * @Date: 2016年8月5日 下午4:06:23
	 */
	public static boolean contains(String value, Class<?> clazz, String fieldName) throws Exception {
		if (StringUtils.isNotEmpty(value) && clazz.isEnum() && null != clazz.getEnumConstants()) {
			for (Object obj : clazz.getEnumConstants()) {
				Field field = clazz.getDeclaredField(fieldName);
				if (null != field) {
					field.setAccessible(true);
					Object codeValue = field.get(obj);
					if (value.equals(codeValue)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
